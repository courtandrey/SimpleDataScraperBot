package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import com.github.courtandrey.simpledatascraperbot.entity.request.HHVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.HabrCareerVacancyRequest;
import com.github.courtandrey.simpledatascraperbot.entity.request.Region;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.exception.UnknownRequestException;
import com.github.courtandrey.simpledatascraperbot.exception.UnmetRequirementsException;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.process.CycledProcess;
import com.github.courtandrey.simpledatascraperbot.process.ProcessManager;
import com.github.courtandrey.simpledatascraperbot.service.RequestService;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleDataScraperBot extends TelegramLongPollingCommandBot {
    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleDataScraperBot.class);
    @Value("${BOT_NAME}")
    private String BOT_USERNAME;
    @Value("${BOT_TOKEN}")
    private String BOT_TOKEN;
    @Autowired
    private StateRegistry registry;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private ProcessManager processManager;
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
                    } catch (TelegramApiException e) {
                        LOGGER.error("Error occurred: " + e);
                    }
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
            return;
        }

        else  if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
            == StateRegistry.DialogType.DELETE_REQUEST) {
            proceedDeleteRequestDialog(update);
            return;
        }

        else if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
            == StateRegistry.DialogType.INIT_SCRAPING) {
            proceedInitScrapingDialog(update);
            return;
        }

        throw new UnsupportedOperationException("This is not a registered dialog type");
    }

    private void proceedInitScrapingDialog(Update update) throws TelegramApiException {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();

        switch (dialog.getStep()) {
            case 0 -> {
                if (keeper.check0StepForInitDialog(update)) {
                    CycledProcess process =
                            processManager.cycledProcess(
                                    Long.parseLong(update.getMessage().getText()) * 1000 * 60,
                                    update.getMessage().getChatId(),
                                    processManager.sendNewDataStrategy(update.getMessage().getChatId(), getBot()));

                    getBot().execute(new SendMessage(
                            String.valueOf(update.getMessage().getChatId()),
                            "It will take some time. It depends on amount of requests on how broad they are and on internet " +
                                    "connection stability\nIt is cycled process it means that it will end only when you send " +
                                    "/stop command"
                    ));

                    process.start();
                }
            }

            default -> throw new UnsupportedOperationException("Unknown step of dialog");
        }
    }

    private void proceedDeleteRequestDialog(Update update) throws TelegramApiException {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();

        switch (dialog.getStep()) {
            case 0 -> {
                if (keeper.check0StepForDeleteDialog(update)) {
                    execute(new SendMessage(
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
                int nextStep = keeper.checkAndProceed0StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 10 -> {
                int nextStep = keeper.checkAndProceed10StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 11 -> {
                int nextStep = keeper.checkAndProceed11StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 12 -> {
                int nextStep = keeper.checkAndProceed12StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 20 -> {
                int nextStep = keeper.checkAndProceed20StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 21 -> {
                int nextStep = keeper.checkAndProceed21StepForAddDialog(update);
                if (nextStep != -1) {
                    dialog.setNextStep(nextStep);
                }
            }

            case 100 -> {
                if (keeper.check100StepForAddDialog(update)) {
                    Request request = wrapAddRequest(update);

                    requestService.addRequestToUser(
                            update.getMessage().getChatId(),
                            request
                    );

                    registry.forget(update.getMessage().getChatId());

                    execute(new SendMessage(
                       String.valueOf(update.getMessage().getChatId()),
                        "You request is registered"));
                }
            }

            default -> throw new UnsupportedOperationException("Unknown step of dialog");
        }
    }

    private Request wrapAddRequest(Update update) {
        RequestWrapper wrapper = new RequestWrapper();

        return wrapper.wrapRequest(update.getMessage().getChatId());
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

            Set<Integer> regionInts =
                    Arrays.stream(dialogChain.get(12).getText().trim().split(","))
                    .map(String::trim)
                    .filter(x -> x.length() > 0)
                    .map(Integer::parseInt)
                    .filter(x -> (x > 0) && (x < 11))
                    .collect(Collectors.toSet());
            Set<Region> regions = new HashSet<>();

            if (!regionInts.contains(1)) {
                for (int regionInt:regionInts) {
                    switch (regionInt) {
                        case 2 -> regions.add(Region.RUSSIA);
                        case 3 -> regions.add(Region.UKRAINE);
                        case 4 -> regions.add(Region.AZERBAIJAN);
                        case 5 -> regions.add(Region.BELARUS);
                        case 6 -> regions.add(Region.KAZAKHSTAN);
                        case 7 -> regions.add(Region.KYRGYZSTAN);
                        case 8 -> regions.add(Region.UZBEKISTAN);
                        case 9 -> regions.add(Region.GEORGIA);
                        case 10 -> regions.add(Region.OTHER);
                    }
                }
            }

            request.setRegions(regions);

            if (dialogChain.get(100).getText().equals("y")) {
                request.setRemote(true);
            }

            request.setUser(userService.getUserById(dialogChain.get(0).getChatId()).orElseThrow(
                    UserNotFoundException::new));

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

        private int checkAndProceed0StepForAddDialog(Update update) throws TelegramApiException {
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

        private boolean check0StepForDeleteDialog(Update update) throws TelegramApiException {
            long requestId;

            try {
                requestId = Long.parseLong(update.getMessage().getText());
            }

            catch (Exception e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Id should be number"));
                return false;
            }
            try {
                requestService.deleteRequestById(requestId);
                return true;
            } catch (UnknownRequestException e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "There is no request with id: " + requestId));

                return false;
            }
        }

        private boolean check0StepForInitDialog(Update update) throws TelegramApiException {
            try {
                Integer.parseInt(update.getMessage().getText());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private int checkAndProceed10StepForAddDialog(Update update) throws TelegramApiException {
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

        private int checkAndProceed11StepForAddDialog(Update update) throws TelegramApiException {
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
                        """
                                Do you consider special region? Choose several using comma as delimiter
                                1. Doesn't matter
                                2. Russia
                                3. Ukraine
                                4. Azerbaijan
                                5. Belarus
                                6. Kazakhstan
                                7. Kyrgyzstan
                                8. Uzbekistan
                                9. Georgia
                                10. Other region
                               """));
                return 12;
            }
        }

        private int checkAndProceed12StepForAddDialog(Update update) throws TelegramApiException {
            try {
                List<Integer> ints = Arrays.stream(update.getMessage().getText().trim().split(","))
                        .map(String::trim)
                        .filter(x -> x.length() > 0)
                        .map(Integer::parseInt)
                        .filter(x -> (x > 0) && (x < 11))
                        .distinct()
                        .toList();

                if (ints.size() == 0) throw new UnmetRequirementsException();

                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "Do you consider only remote work? y/n"));
                return 100;
            } catch (Exception e) {
                getBot().execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        "You mast provide only numbers between 1-10, delimited with comma"));
                return -1;
            }
        }

        private int checkAndProceed20StepForAddDialog(Update update) throws TelegramApiException {
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

        private int checkAndProceed21StepForAddDialog(Update update) throws TelegramApiException {
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

        private boolean check100StepForAddDialog(Update update) throws TelegramApiException {
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
        if (registry.getLastState(update.getMessage().getChatId()).dialog() != null) return;
        try {
            execute(new SendMessage(
                    String.valueOf(update.getMessage().getChatId()),
                    """
                            It is not registered command. You can use following commands:
                            You can use /init command to init scraping if you already have requests
                            If you don't have any request or want to add new use /add command
                            To show all registered requests use /show command
                            To delete one of requests use /delete command
                            To stop all cycled processes use /stop command
                            """
            ));
        } catch (TelegramApiException e) {
            LOGGER.error("Error occurred: " + e);
        }
    }
}