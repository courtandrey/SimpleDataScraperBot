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
        private int nextStep = 0;
        private final DialogType type;
    }

    public enum DialogType {
        ADD_REQUEST
    }

    public record State(Message message, Dialog dialog) {}

    public void register(Message message, Long chatId) {
        if (commands.get(chatId) != null) {
            State previousState = commands.get(chatId).getLast();
            if (previousState.dialog != null && !message.isCommand()) {
                commands.get(chatId).add(new State(
                        message,
                        new Dialog(previousState.dialog.getNextStep(), DialogType.ADD_REQUEST)
                ));
            } else {
                commands.get(chatId).add(new State(
                        message,
                        message.getText().equals("/add") ? new Dialog(0, DialogType.ADD_REQUEST) : null
                ));
            }
        } else {
            LinkedList<State> states = new LinkedList<>();
            states.add(new State(
                    message,
                    message.getText().equals("/add") ? new Dialog(0, DialogType.ADD_REQUEST) : null
            ));
            commands.put(chatId, states);
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
