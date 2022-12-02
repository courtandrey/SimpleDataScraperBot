package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import com.github.courtandrey.simpledatascraperbot.entity.repository.UserRepository;
import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class SimpleDataScraperBot extends TelegramLongPollingCommandBot {
    @Value("${BOT_NAME}")
    private String BOT_USERNAME;
    @Value("${BOT_TOKEN}")
    private String BOT_TOKEN;
    @Autowired
    private StateRegistry registry;
    @Autowired
    private UserRepository repository;

    public SimpleDataScraperBot(CommandConfiguration commandConfiguration) {
        for (BaseCommand command:commandConfiguration.getCommands()) {
            register(command);
        }
    }
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update u : updates) {
            registry.register(u.getMessage(), u.getMessage().getChatId());
            if (registry.getChain(u.getMessage().getChatId()) != null) {
                StateRegistry.State state = registry.getLastState(u.getMessage().getChatId());

                if (state.dialog() != null) {
                    try {
                        proceedDialog(u);
                    } catch (TelegramApiException ignored) {}
                }
            }
        }
        super.onUpdatesReceived(updates);
    }



    private void proceedDialog(Update update) throws TelegramApiException {
        if (update.getMessage().isCommand()) return;

        if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
                == StateRegistry.DialogType.ADD_REQUEST) {
            proceedAddRequestDialog(update);
        }

    }

    private void proceedAddRequestDialog(Update update) throws TelegramApiException {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();

        switch (dialog.getStep()) {
            case 0 -> {
                if (!update.getMessage().getText().equals("1") &&
                    !update.getMessage().getText().equals("2")) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Type number of scraper you'd like to add"));
                } else if (update.getMessage().getText().equals("1")) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Type text you'd like to find in vacancy"));
                    dialog.setNextStep(10);
                } else if (update.getMessage().getText().equals("2")) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Type integer representing main skill you'd like to find in vacancy " +
                                    "(you can find one in career.habr url)"));
                    dialog.setNextStep(20);
                }
            }
            case 10 -> {
                if (update.getMessage().getText().length() < 2) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "You should provide more text."));
                } else {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            """
                                    What experience do you have:
                                    1. No experience
                                    2. Between 1 and 3 years
                                    3. Between 3 and 6 years
                                    4. More than 6 years
                                    """));
                    dialog.setNextStep(11);
                }
            }
            case 11 -> {
                if (!update.getMessage().getText().equals("1") &&
                        !update.getMessage().getText().equals("2") &&
                        !update.getMessage().getText().equals("3") &&
                        !update.getMessage().getText().equals("4")) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "You should type number between 1 and 4"));
                } else {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Do you consider only remote work? y/n"));
                    dialog.setNextStep(Integer.MAX_VALUE);
                }
            }
            case 20 -> {
                try {
                    Integer.parseInt(update.getMessage().getText());
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            """
                                   Which level are you:
                                   1. Intern
                                   2. Junior
                                   3. Middle
                                   4. Senior
                                   5. Lead
                                    """));
                    dialog.setNextStep(21);
                } catch (Exception e) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "This is not a number"));
                }
            }
            case 21 -> {
                try {
                    int num = Integer.parseInt(update.getMessage().getText());
                    if (num > 0 && num < 6) {
                        this.execute(new SendMessage(
                                String.valueOf(update.getMessage().getChatId()),
                                "Do you consider only remote work? y/n"));
                        dialog.setNextStep(Integer.MAX_VALUE);
                    }
                } catch (Exception e) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "This is not a number"));
                }
            }
            case Integer.MAX_VALUE -> {
                if (!update.getMessage().getText().equals("y") && !update.getMessage().getText().equals("n")) {
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "You should type y - for yes and n - for no"));
                } else {
                    wrapAddRequest(update);
                    this.execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "You request is registered"));
                }
            }
        }
    }

    private void wrapAddRequest(Update update) {
        LinkedList<StateRegistry.State> rawChain = registry.getChain(update.getMessage().getChatId());
        Map<Integer, StateRegistry.State> lastDialogChain = new HashMap<>();

        StateRegistry.State workingState = rawChain.getLast();
        lastDialogChain.put(workingState.dialog().getStep(), workingState);
        int workingInt = 2;

        while (true) {
            workingState = rawChain.get(rawChain.size() - workingInt);
            if (workingState.dialog() == null) break;
            lastDialogChain.putIfAbsent(workingState.dialog().getStep(), workingState);
            if (workingState.dialog().getStep() == 0) break;
            ++workingInt;
        }

        if (lastDialogChain.get(0).message().getText().equals("1")) {
           HHVacancyRequest request = new HHVacancyRequest();
           request.setSearchText(lastDialogChain.get(10).message().getText());

           switch (lastDialogChain.get(11).message().getText()) {
               case "1" -> request.setExperience(HHVacancyRequest.Experience.NO);
               case "2" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_1_AND_3);
               case "3" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_3_AND_6);
               case "4" -> request.setExperience(HHVacancyRequest.Experience.MORE_THAN_6);
           }

           if (lastDialogChain.get(Integer.MAX_VALUE).message().getText().equals("y")) {
               request.setRemote(true);
           }

           User user =
                   repository.findByUserId(update.getMessage().getFrom().getId()).orElseThrow(UserNotFoundException::new);
           user.getRequests().add(request);
           repository.save(user);
        }
        else {
            HabrCareerVacancyRequest request = new HabrCareerVacancyRequest();
            request.setSkill(Integer.parseInt(lastDialogChain.get(20).message().getText()));

            switch (lastDialogChain.get(21).message().getText()) {
                case "1" -> request.setLevel(HabrCareerVacancyRequest.Level.INTERN);
                case "2" -> request.setLevel(HabrCareerVacancyRequest.Level.JUNIOR);
                case "3" -> request.setLevel(HabrCareerVacancyRequest.Level.MIDDLE);
                case "4" -> request.setLevel(HabrCareerVacancyRequest.Level.SENIOR);
                case "5" -> request.setLevel(HabrCareerVacancyRequest.Level.LEAD);
            }
            if (lastDialogChain.get(Integer.MAX_VALUE).message().getText().equals("y")) {
                request.setRemote(true);
            }

            User user =
                    repository.findByUserId(update.getMessage().getFrom().getId()).orElseThrow(UserNotFoundException::new);
            user.getRequests().add(request);
            repository.save(user);
        }

        registry.forget(update.getMessage().getChatId());
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }
}

