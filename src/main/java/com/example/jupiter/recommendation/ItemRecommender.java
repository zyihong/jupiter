package com.example.jupiter.recommendation;

import com.example.jupiter.entity.Item;
import com.example.jupiter.entity.Game;
import com.example.jupiter.entity.ItemType;
import com.example.jupiter.twitch.*;

import java.util.List;
import java.util.Set;


public class ItemRecommender {
    private static final int DEFAULT_GAME_LIMIT = 5;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;

    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        System.out.println("TODO");
        return null;
    }

    private List<Item> recommendByFavorite(ItemType type, Set<String> favoriteItemIds,
                                           List<String> favoriteGameIds) throws RecommendationException {
        System.out.println("TODO");
        return null;
    }
}
