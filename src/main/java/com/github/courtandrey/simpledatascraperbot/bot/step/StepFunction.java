package com.github.courtandrey.simpledatascraperbot.bot.step;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Function;

public interface StepFunction extends Function<Update, StepResponse> {

}
