package com.github.courtandrey.simpledatascraperbot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
public class StateRegistry {
    private final Map<Long, LinkedList<State>> commands = new HashMap<>();

    @Getter
    @Setter
    @RequiredArgsConstructor
    static class Dialog{
        private final int step;
        private Integer nextStep = null;
        private final DialogType type;
    }

    public enum DialogType {
        ADD_REQUEST,
        DELETE_REQUEST,
        INIT_SCRAPING
    }

    public record State(Message message, Dialog dialog) {}

    public void register(Message message, Long chatId) {
        if (commands.get(chatId) != null) {
            State previousState = commands.get(chatId).getLast();

            if (previousState.dialog != null && !message.isCommand()) {
                commands.get(chatId).add(new State(
                        message,
                        previousState.dialog.nextStep != null ?
                                new Dialog(previousState.dialog.getNextStep(), previousState.dialog.type) :
                                previousState.dialog
                ));
            }

            else {
                commands.get(chatId).add(new State(
                        message,
                        isDialogCommand(message.getText()) ? getDialogFromCommand(message.getText()) : null
                ));
            }
        }

        else {
            LinkedList<State> states = new LinkedList<>();
            states.add(new State(
                    message,
                    isDialogCommand(message.getText()) ? getDialogFromCommand(message.getText()) : null
            ));
            commands.put(chatId, states);
        }
    }

    private boolean isDialogCommand(String text) {
        return text.trim().equals("/add") || text.trim().equals("/delete") || text.trim().equals("/init");
    }

    private Dialog getDialogFromCommand(String text) {
        switch (text.trim()) {
            case "/add" -> {
                return new Dialog(0, DialogType.ADD_REQUEST);
            }

            case "/delete" -> {
                return new Dialog(0, DialogType.DELETE_REQUEST);
            }

            case "/init" -> {
                return new Dialog(0, DialogType.INIT_SCRAPING);
            }

            default -> throw new UnsupportedOperationException("This is not a dialog command");
        }
    }

    public LinkedList<State> getChain(Long chatId) {
        return commands.get(chatId);
    }

    public void forget(Long chatId) {
        commands.put(chatId, null);
    }

    public State getLastState(Long chatId) {
        return commands.get(chatId).getLast();
    }
}
