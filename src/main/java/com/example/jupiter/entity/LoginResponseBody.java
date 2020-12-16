package com.example.jupiter.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseBody {
    @JsonProperty("user_id")
    private final String usrId;

    @JsonProperty("password")
    private final String password;

    @JsonCreator
    public LoginResponseBody(String usrId, String password) {
        this.usrId = usrId;
        this.password = password;
    }

    public String getUsrId() {return this.usrId;}

    public String getPassword() {return this.password;}
}
