package com.github.courtandrey.simpledatascraperbot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
public class StateRegistry {
    private final Map<Long, LinkedList<State>> commands = new HashMap<>();

    private static final Map<String,DialogType> dialogStrings = new HashMap<>();

    static {
        dialogStrings.put("/add", DialogType.ADD_REQUEST);
        dialogStrings.put("/init", DialogType.INIT_SCRAPING);
        dialogStrings.put("/delete", DialogType.DELETE_REQUEST);
    }

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
        return dialogStrings.containsKey(text.trim());
    }

    private Dialog getDialogFromCommand(String text) {
        if (dialogStrings.containsKey(text.trim())) {
            return new Dialog(0, dialogStrings.get(text.trim()));
        }

        throw new UnsupportedOperationException("This is not a dialog command");
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

    public boolean isSessionActive(Long chatId) {
        return commands.containsKey(chatId);
    }
}
