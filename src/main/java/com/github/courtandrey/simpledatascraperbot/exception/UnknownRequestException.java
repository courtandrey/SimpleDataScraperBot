package com.github.courtandrey.simpledatascraperbot.exception;

public class UnknownRequestException extends RuntimeException {
    public UnknownRequestException(String unknownTypeOfRequest) {
        super(unknownTypeOfRequest);
    }
}
