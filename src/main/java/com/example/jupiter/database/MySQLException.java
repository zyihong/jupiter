package com.example.jupiter.database;

// Wrap all DB related exceptions into TwitchException for convenience
public class MySQLException extends RuntimeException {
    public MySQLException(String errorMsg) {
        super(errorMsg);
    }
}
