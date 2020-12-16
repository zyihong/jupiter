package com.example.jupiter.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

// Create request body for favorite field.
public class Favorite {
    private final Item favoriteItem;

    @JsonCreator // Jackson use this construct to convert json to java object.
    public Favorite(@JsonProperty("favorite") Item favoriteItem) {
        this.favoriteItem = favoriteItem;
    }

    public Item getFavoriteItem() {
        return favoriteItem;
    }
}
