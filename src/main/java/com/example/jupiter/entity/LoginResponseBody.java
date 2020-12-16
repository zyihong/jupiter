package com.example.jupiter.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseBody {
    @JsonProperty("user_id")
    private final String usrId;

    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public LoginResponseBody(String usrId, String name) {
        this.usrId = usrId;
        this.name = name;
    }

    public String getUsrId() {return this.usrId;}

    public String getName() {return this.name;}
}
