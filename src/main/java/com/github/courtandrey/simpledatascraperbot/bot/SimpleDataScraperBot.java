package com.github.courtandrey.simpledatascraperbot.bot;

import com.github.courtandrey.simpledatascraperbot.bot.command.BaseCommand;
import com.github.courtandrey.simpledatascraperbot.bot.request.*;
import com.github.courtandrey.simpledatascraperbot.bot.step.StepLinkingFunction;
import com.github.courtandrey.simpledatascraperbot.bot.step.StepMappingFunction;
import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import com.github.courtandrey.simpledatascraperbot.entity.servicedata.User;
import com.github.courtandrey.simpledatascraperbot.exception.UnknownRequestException;
import com.github.courtandrey.simpledatascraperbot.exception.UserNotFoundException;
import com.github.courtandrey.simpledatascraperbot.process.CycledProcess;
import com.github.courtandrey.simpledatascraperbot.process.Process;
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
import java.util.function.Consumer;

import static com.github.courtandrey.simpledatascraperbot.bot.TextMessages.UNKNOWN_REQUEST;
import static com.github.courtandrey.simpledatascraperbot.bot.TextMessages.UNKNOWN_REQUEST_ADMIN;

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
            if (!registry.isSessionActive(u.getMessage().getChatId())) {
                userService.addIfEmpty(u.getMessage().getFrom());
            }
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
            proceedMappedSteps(update, StateRegistry.DialogType.ADD_REQUEST, upd -> {
                        Request request = wrapAddRequest(upd);

                        requestService.addRequestToUser(
                                upd.getMessage().getChatId(),
                                request
                        );
                    });
            return;
        }

        else  if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
            == StateRegistry.DialogType.DELETE_REQUEST) {
            proceedDeleteRequestDialog(update);
            return;
        }

        else  if (registry.getLastState(update.getMessage().getChatId()).dialog().getType()
                == StateRegistry.DialogType.STOP_ADMIN) {
            proceedMappedSteps(update, StateRegistry.DialogType.STOP_ADMIN,
                    upd -> {
                        Long key = Long.parseLong(upd.getMessage().getText());
                        processManager.getProcesses().get(key).forEach(Process::kill);
                    });
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

    private void proceedMappedSteps(Update update, StateRegistry.DialogType dialogType, Consumer<Update> finalAction) {
        StateRegistry.Dialog dialog = registry.getLastState(update.getMessage().getChatId()).dialog();
        new StepMappingFunction().apply(dialog.getStep(), dialogType)
                .andThen(new StepLinkingFunction(
                        dialog, update, finalAction,
                        registry, getBot()
                )).apply(update);
    }

    private Request wrapAddRequest(Update update) {
        RequestWrapper wrapper = new RequestWrapper();

        return wrapper.wrapRequest(update.getMessage().getChatId());
    }

    private class RequestWrapper {

        private Request wrapRequest(Long chatId) {
            Map<Integer, Message> lastDialogChain = retrieveLastDialogChain(chatId);
            int requestInt = Integer.parseInt(lastDialogChain.get(0).getText());
            RequestTransformation<? extends Request> transformation = getRequestTransformation(requestInt);

            return transformation.andThen(request -> {
                request.setUser(userService.getUserById(lastDialogChain.get(0).getChatId()).orElseThrow(
                        UserNotFoundException::new));
                return request;
            }).apply(lastDialogChain);
        }

        private static RequestTransformation<? extends Request> getRequestTransformation(int requestInt) {
            RequestTransformation<? extends Request> transformation;

            switch (requestInt) {
                case 1 -> transformation = new HHVacancyRequestFunction();

                case 2 -> transformation = new HabrCareerRequestFunction();

                case 3 -> transformation = new NLHousingRequestFunction();

                case 4 -> transformation = new LatvijasPastsRequestFunction();

                case 5 -> transformation = new ImdbRequestFunction();

                default -> throw new UnsupportedOperationException("Unknown request type");
            }

            return transformation;
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

    private class DialogKeeper {
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
                requestService.deleteRequestById(requestId, update.getMessage().getChatId());
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
    }

    private SimpleDataScraperBot getBot() {
        return this;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (registry.getChain(update.getMessage().getChatId()) == null ||
                registry.getLastState(update.getMessage().getChatId()).dialog() != null) return;
        try {
            execute(new SendMessage(
                    String.valueOf(update.getMessage().getChatId()),
                    userService.getUserById(update.getMessage().getChatId()).filter(User::isAdmin)
                            .map(user -> UNKNOWN_REQUEST_ADMIN).orElse(UNKNOWN_REQUEST)
            ));
        } catch (TelegramApiException e) {
            LOGGER.error("Error occurred", e);
        }
    }
}