package com.example.jupiter.recommendation;

import com.example.jupiter.entity.Item;
import com.example.jupiter.entity.Game;
import com.example.jupiter.entity.ItemType;
import com.example.jupiter.twitch.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ItemRecommender {
    private static final int DEFAULT_GAME_LIMIT = 5;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;

    // If user does not logged in OR the user does not favorite certain type, use this API.
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

    // if user logged in and has favorited some resource, use this API.
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
}
