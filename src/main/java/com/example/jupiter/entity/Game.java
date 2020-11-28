package com.example.jupiter.entity;

// the annotation convert the object attribute to json keys
import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {
    @JsonProperty("name")
    public String name;

    @JsonProperty("developer")
    public String developer;

    @JsonProperty("release_time")
    public String releaseTime;

    @JsonProperty("website")
    public String website;

    @JsonProperty("price")
    public double price;

    public Game(String name, String developer, String releaseTime, String website, double price) {
        this.name = name;
        this.developer = developer;
        this.releaseTime = releaseTime;
        this.website = website;
        this.price = price;
    }

}
