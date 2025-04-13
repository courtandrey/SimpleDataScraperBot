package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.bot.StateRegistry;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.ERROR_RESPONSE;
import static com.github.courtandrey.simpledatascraperbot.bot.step.StepResponseConstants.TERMINAL_RESPONSE;

@RequiredArgsConstructor
public class StepLinkingFunction implements Function<StepResponse, StepResponse> {
    private final StateRegistry.Dialog dialog;
    private final Update update;
    private final Consumer<Update> finalAction;
    private final StateRegistry registry;
    private final AbsSender absSender;

    @Override
    public StepResponse apply(StepResponse response) {
        if (response.getId() != ERROR_RESPONSE) {
            dialog.setNextStep(response.getId());
        }

        if (response.getId() == TERMINAL_RESPONSE) {
            finalAction.accept(update);

            registry.forget(update.getMessage().getChatId());
        }

        if (response.getName() != null) {
            try {
                absSender.execute(new SendMessage(
                        String.valueOf(update.getMessage().getChatId()),
                        response.getName()));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }
}
