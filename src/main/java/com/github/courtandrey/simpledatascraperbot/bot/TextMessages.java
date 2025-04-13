package com.github.courtandrey.simpledatascraperbot.bot;

public class TextMessages {
    private TextMessages() {}

    public static final String COMMAND_DECLARATIONS = """
            You can use /init command to init scraping if you already have requests
            If you don't have any request or want to add new use /add command
            To show all registered requests use /show command
            To delete one of requests use /delete command
            To stop all cycled processes use /stop command
            """;

    public static final String ADMIN_COMMAND_DECLARATIONS = """
            To stop specific processes use /stopAdmin
            To delete specific request use /deleteAdmin
            To show all running processes use /showAllProcesses
            To show all requests use /showAll
            """;

    public static final String ALL_COMMANDS = COMMAND_DECLARATIONS + "\n" + ADMIN_COMMAND_DECLARATIONS;

    public static final String UNKNOWN_REQUEST_STARTING = "It is not registered command. You can use following commands:";

    public static final String UNKNOWN_REQUEST = UNKNOWN_REQUEST_STARTING + "\n" + COMMAND_DECLARATIONS;

    public static final String UNKNOWN_REQUEST_ADMIN = UNKNOWN_REQUEST_STARTING + "\n" + ALL_COMMANDS;
}
