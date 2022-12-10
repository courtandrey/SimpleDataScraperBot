package com.github.courtandrey.simpledatascraperbot.aspect;

import com.github.courtandrey.simpledatascraperbot.bot.StateRegistry;
import com.github.courtandrey.simpledatascraperbot.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Aspect
@Component
public class CommandAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandAspect.class);
    @Autowired
    private UserService userService;
    @Autowired
    private StateRegistry stateRegistry;

    @Before("execution(* com.github.courtandrey.simpledatascraperbot.bot.command.*.processMessage(..))")
    public void loggerCommandAdvice(JoinPoint joinPoint) {
        Message message = (Message) joinPoint.getArgs()[1];
        LOGGER.info(joinPoint.getTarget().getClass().getSimpleName() + " is called by user identified by " + message.getChatId());
    }

}
