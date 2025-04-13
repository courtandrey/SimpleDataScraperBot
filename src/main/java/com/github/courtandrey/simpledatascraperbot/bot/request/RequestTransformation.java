package com.github.courtandrey.simpledatascraperbot.bot.request;

import com.github.courtandrey.simpledatascraperbot.entity.request.Request;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.function.Function;

public interface RequestTransformation<T extends Request> extends Function<Map<Integer, Message>, T> {
}
