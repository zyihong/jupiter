package com.example.jupiter.recommendation;

import com.example.jupiter.database.MySQLClient;
import com.example.jupiter.database.MySQLException;
import com.example.jupiter.entity.Item;
import com.example.jupiter.entity.Game;
import com.example.jupiter.entity.ItemType;
import com.example.jupiter.twitch.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ItemRecommender {
    private static final int DEFAULT_GAME_LIMIT = 5;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;

    // If user does not logged in OR the user does not favorite certain type, use this method.
    // Just get the resource for the top games.
    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();
        TwitchClient client = new TwitchClient();

        for (Game game : topGames) {
            List<Item> items;
            try {
                items = client.searchByTypes(game.getId(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Fail to get top game recommendations!");
            }

            for (Item item : items) {
                if (recommendedItems.size() >= DEFAULT_TOTAL_RECOMMENDATION_LIMIT) break;
                recommendedItems.add(item);
            }

            if (recommendedItems.size() >= DEFAULT_TOTAL_RECOMMENDATION_LIMIT) break;
        }

        return recommendedItems;
    }

    // if user logged in and has favorited some resource, use this method.
    private List<Item> recommendByFavorite(ItemType type, Set<String> favoriteItemIds,
                                           List<String> favoriteGameIds) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();
        TwitchClient client = new TwitchClient();

        // Turn the favorite game id to a count map and sort by count ao descending.
        Map<String, Long> favoriteGameIdByCount = favoriteGameIds.parallelStream()
                .collect(Collectors.groupingBy(str -> str, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedFavoriteGameIdByCount = new ArrayList<>(favoriteGameIdByCount.entrySet());
        sortedFavoriteGameIdByCount.sort(
                (e1, e2) -> Long.compare(e2.getValue(), e1.getValue())
        );

        // Truncate the games.
        if (sortedFavoriteGameIdByCount.size() > DEFAULT_GAME_LIMIT) {
            sortedFavoriteGameIdByCount = sortedFavoriteGameIdByCount.subList(0, DEFAULT_GAME_LIMIT);
        }

        for (Map.Entry<String, Long> gameEntry : sortedFavoriteGameIdByCount) {
            List<Item> items;
            try {
                items = client.searchByTypes(gameEntry.getKey(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Fail to get favorite game recommendations!");
            }

            for (Item item : items) {
                if (recommendedItems.size() >= DEFAULT_TOTAL_RECOMMENDATION_LIMIT) break;

                // Avoid recommending item that is already favorited.
                if (!favoriteItemIds.contains(item.getId())) recommendedItems.add(item);
            }

            if (recommendedItems.size() >= DEFAULT_TOTAL_RECOMMENDATION_LIMIT) break;
        }
        return recommendedItems;
    }

    public Map<String, List<Item>> recommendItemsByDefault() throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        TwitchClient client = new TwitchClient();
        List<Game> topGames;
        try {
            topGames = client.getTopGames(DEFAULT_GAME_LIMIT);
        } catch (TwitchException e) {
            throw new RecommendationException("Fail to get top games for recommendations!");
        }

        for (ItemType type : ItemType.values()) {
            recommendedItemMap.put(type.toString(), recommendByTopGames(type, topGames));
        }

        return recommendedItemMap;
    }

    public Map<String, List<Item>> recommendItemsByUser(String userId) throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        Set<String> favoriteItemIds;
        Map<String, List<String>> favoriteGameIds;

        MySQLClient connection = null;

        try {
            connection = new MySQLClient();
            favoriteItemIds = connection.getFavoriteItemIds(userId);
            favoriteGameIds = connection.getFavoriteGameIds(favoriteItemIds);
        } catch (MySQLException e) {
            throw new RecommendationException("Fail to get favorite game ids from database for recommendation!");
        } finally {
            connection.close();
        }

        for (Map.Entry<String, List<String>> entry : favoriteGameIds.entrySet()) {
            if (entry.getValue().size() == 0) {
                TwitchClient client = new TwitchClient();
                List<Game> topGames;

                try {
                    topGames = client.getTopGames(DEFAULT_GAME_LIMIT);
                } catch (TwitchException e) {
                    throw new RecommendationException("Fail to get top games for recommendations!");
                }

                recommendedItemMap.put(entry.getKey(), recommendByTopGames(ItemType.valueOf(entry.getKey()), topGames));
            }
            else {
                recommendedItemMap.put(
                        entry.getKey(),
                        recommendByFavorite(ItemType.valueOf(entry.getKey()), favoriteItemIds, entry.getValue())
                );
            }
        }

        return recommendedItemMap;
    }


}
