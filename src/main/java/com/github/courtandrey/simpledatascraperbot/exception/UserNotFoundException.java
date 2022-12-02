package com.github.courtandrey.simpledatascraperbot.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException() {
        super("Cannot continue process for unknown user");
    }
}
