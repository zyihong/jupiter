package com.example.jupiter.twitch;

// Wrap all exceptions into TwitchException for convenience
public class TwitchException extends RuntimeException{
    public TwitchException(String errorMsg) {
        super(errorMsg);
    }
}
