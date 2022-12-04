package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    private UserService userService;
    private final DialogKeeper keeper = new DialogKeeper();

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

        else  if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
            == StateRegistry.DialogType.DELETE_REQUEST) {
            proceedDeleteRequestDialog(update);
        }

    }

    private void proceedDeleteRequestDialog(Update update) throws TelegramApiException {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();

        switch (dialog.getStep()) {
            case 0 -> {
                if (keeper.check1Step(update)) {
                    getBot().execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Request was successfully deleted"));
                    registry.forget(update.getMessage().getChatId());
                }
            }
            default -> throw new UnsupportedOperationException("Unknown step of dialog");
        }
    }

    private void proceedAddRequestDialog(Update update) throws TelegramApiException {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();

        switch (dialog.getStep()) {
            case 0 -> {
                int nextStep = keeper.checkAndProceed0Step(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 10 -> {
                int nextStep = keeper.checkAndProceed10Step(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 11 -> {
                int nextStep = keeper.checkAndProceed11Step(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 20 -> {
                int nextStep = keeper.checkAndProceed20Step(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 21 -> {
                int nextStep = keeper.checkAndProceed21Step(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 100 -> {
                if (keeper.check100Step(update)) {
                    wrapAddRequest(update);
                    getBot().execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "You request is registered"));
                }
            }
        }
    }

    private void wrapAddRequest(Update update) {
        RequestWrapper wrapper = new RequestWrapper();

        Request request = wrapper.wrapRequest(update.getMessage().getChatId());

        User user =
                userService.getUserById(update.getMessage().getFrom().getId()).orElseThrow(UserNotFoundException::new);

        user.getRequests().add(request);

        userService.save(user);

        registry.forget(update.getMessage().getChatId());
    }

    private class RequestWrapper {
        private Request wrapRequest(Long chatId) {
            Map<Integer, Message> lastDialogChain = retrieveLastDialogChain(chatId);
            int requestInt = Integer.parseInt(lastDialogChain.get(0).getText());

            switch (requestInt) {
                case 1 -> {
                    return getHHVacancyRequest(lastDialogChain);
                }

                case 2 -> {
                    return getHabrCareerVacancyRequest(lastDialogChain);
                }

                default -> throw new UnsupportedOperationException("Unknown request type");
            }

        }

        private HHVacancyRequest getHHVacancyRequest(Map<Integer, Message> dialogChain) {
            HHVacancyRequest request = new HHVacancyRequest();
            request.setSearchText(dialogChain.get(10).getText());

            switch (dialogChain.get(11).getText()) {
                case "1" -> request.setExperience(HHVacancyRequest.Experience.NO);
                case "2" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_1_AND_3);
                case "3" -> request.setExperience(HHVacancyRequest.Experience.BETWEEN_3_AND_6);
                case "4" -> request.setExperience(HHVacancyRequest.Experience.MORE_THAN_6);
            }

            if (dialogChain.get(100).getText().equals("y")) {
                request.setRemote(true);
            }

            request.setUser(userService.getUserById(dialogChain.get(0).getChatId()).orElseThrow(
                    UserNotFoundException::new
            ));

            return request;
        }

        private HabrCareerVacancyRequest getHabrCareerVacancyRequest(Map<Integer, Message> dialogChain) {
            HabrCareerVacancyRequest request = new HabrCareerVacancyRequest();
            request.setSkill(Integer.parseInt(dialogChain.get(20).getText()));

            switch (dialogChain.get(21).getText()) {
                case "1" -> request.setLevel(HabrCareerVacancyRequest.Level.INTERN);
                case "2" -> request.setLevel(HabrCareerVacancyRequest.Level.JUNIOR);
                case "3" -> request.setLevel(HabrCareerVacancyRequest.Level.MIDDLE);
                case "4" -> request.setLevel(HabrCareerVacancyRequest.Level.SENIOR);
                case "5" -> request.setLevel(HabrCareerVacancyRequest.Level.LEAD);
            }
            if (dialogChain.get(100).getText().equals("y")) {
                request.setRemote(true);
            }
            request.setUser(userService.getUserById(dialogChain.get(0).getChatId()).orElseThrow(
                    UserNotFoundException::new
            ));
            return request;
        }

        private Map<Integer, Message> retrieveLastDialogChain(Long chatId) {
            LinkedList<StateRegistry.State> rawChain = registry.getChain(chatId);
            Map<Integer, Message> lastDialogChain = new HashMap<>();

            Iterator<StateRegistry.State> stateIterator = rawChain.descendingIterator();
            StateRegistry.State state;

            while (stateIterator.hasNext() && (state = stateIterator.next()).dialog() != null) {
                lastDialogChain.putIfAbsent(state.dialog().getStep(), state.message());
            }

            return lastDialogChain;
        }
    }

    private class DialogKeeper{

        private int checkAndProceed0Step(Update update) throws TelegramApiException {
            if (update.getMessage().getText().equals("1")) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Type text you'd like to find in vacancy"));
                return 10;
            }

            else if (update.getMessage().getText().equals("2")) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Type integer representing main skill you'd like to find in vacancy " +
                                "(you can find one in career.habr url)"));
                return 20;
            }

            else {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Type number of scraper you'd like to add"));
                return -1;
            }
        }

        private boolean check1Step(Update update) throws TelegramApiException {
            Long requestId;

            try {
                requestId = Long.parseLong(update.getMessage().getText());
            }

            catch (Exception e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Id should be number"));
                return false;
            }

            User user = userService.getUserById(update.getMessage().getFrom().getId())
                    .orElseThrow(UserNotFoundException::new);

            for (Request r:user.getRequests()) {
                if (r.getId().equals(requestId)) {
                    user.getRequests().remove(r);
                    userService.save(user);
                    return true;
                }
            }

            getBot().execute(new SendMessage(
                    String.valueOf(update.getMessage().getChatId()),
                    "There is no request with id: " + requestId));

            return false;
        }

        private int checkAndProceed10Step(Update update) throws TelegramApiException {
            if (update.getMessage().getText().length() < 2) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "You should provide more text."));
                return -1;
            }

            else {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        """
                                What experience do you have:
                                1. No experience
                                2. Between 1 and 3 years
                                3. Between 3 and 6 years
                                4. More than 6 years
                                """));
                return 11;
            }
        }

        private int checkAndProceed11Step(Update update) throws TelegramApiException {
            if (!update.getMessage().getText().equals("1") &&
                    !update.getMessage().getText().equals("2") &&
                    !update.getMessage().getText().equals("3") &&
                    !update.getMessage().getText().equals("4")) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "You should type number between 1 and 4"));
                return -1;
            }

            else {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Do you consider only remote work? y/n"));
                return 100;
            }
        }

        private int checkAndProceed20Step(Update update) throws TelegramApiException {
            try {
                Integer.parseInt(update.getMessage().getText());
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        """
                               Which level are you:
                               1. Intern
                               2. Junior
                               3. Middle
                               4. Senior
                               5. Lead
                                """));
                return 21;
            } catch (Exception e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "This is not a number"));
                return -1;
            }
        }

        private int checkAndProceed21Step(Update update) throws TelegramApiException {
            try {
                int num = Integer.parseInt(update.getMessage().getText());
                if (num > 0 && num < 6) {
                    getBot().execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "Do you consider only remote work? y/n"));
                    return 100;
                }
                return -1;
            }

            catch (Exception e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "This is not a number"));
                return -1;
            }
        }

        private boolean check100Step(Update update) throws TelegramApiException {
            if (!update.getMessage().getText().equals("y") && !update.getMessage().getText().equals("n")) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "You should type y - for yes and n - for no"));
                return false;
            }

            return true;
        }
    }

    private SimpleDataScraperBot getBot() {
        return this;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
    }
}

