package com.github.courtandrey.simpledatascraperbot.audit;

import java.util.Optional;

public enum CommandName {
    ADD("AddRequest"),
    DELETE_ADMIN("DeleteAdmin"),
    DELETE("DeleteRequest"),
    INIT("InitScraping"),
    SHOW_ALL("ShowAll"),
    SHOW_ALL_PROCESSES("ShowAllRunningProcesses"),
    SHOW("ShowRequests"),
    START("Start"),
    STOP_ADMIN("StopAdmin"),
    STOP("Stop");

    private final String token;

    CommandName(String token) {
        this.token = token;
    }

    public static Optional<CommandName> fromClassSimpleName(String simpleName) {
        String candidate = simpleName.endsWith("Command")
                ? simpleName.substring(0, simpleName.length() - "Command".length())
                : simpleName;
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        for (CommandName c : values()) {
            if (c.token.equals(candidate)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
