package com.github.courtandrey.simpledatascraperbot.aspect;

import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Aspect
@Component
public class CommandAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandAspect.class);
    @Autowired
    private UserService userService;
    @Before("execution(* com.github.courtandrey.simpledatascraperbot.bot.command.*.processMessage(..))")
    public void registerUserAdvice(JoinPoint joinPoint) {
        Message message = (Message) joinPoint.getArgs()[1];
        try {
            userService.addIfEmpty(message.getFrom());
        } catch (TelegramApiException e) {LOGGER.error("Couldn't save user");}
    }

    @Before("execution(* com.github.courtandrey.simpledatascraperbot.bot.command.*.processMessage(..))")
    public void loggerUserAdvice(JoinPoint joinPoint) {
        Message message = (Message) joinPoint.getArgs()[1];
        LOGGER.info(joinPoint.getTarget().getClass().getSimpleName() + " is called by user identified by " + message.getChatId());
    }

}
