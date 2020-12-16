package com.example.jupiter.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestBody {
    private final String usrId;
    private final String password;

    @JsonCreator
    public LoginRequestBody(@JsonProperty("user_id") String usrId,
                            @JsonProperty("password") String password) {
        this.usrId = usrId;
        this.password = password;
    }

    public String getUsrId() {return this.usrId;}

    public String getPassword() {return this.password;}
}
