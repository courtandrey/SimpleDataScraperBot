package com.github.courtandrey.simpledatascraperbot.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Aspect
@Component
public class CommandAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandAspect.class);

    private final AuditLogService auditLogService;

    public CommandAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Before("execution(* com.github.courtandrey.simpledatascraperbot.bot.command.*.processMessage(..))")
    public void loggerCommandAdvice(JoinPoint joinPoint) {
        Message message = (Message) joinPoint.getArgs()[1];
        Long userId = message.getChatId();
        String commandClass = joinPoint.getTarget().getClass().getSimpleName();

        LOGGER.info("{} is called by user identified by {}", commandClass, userId);

        CommandName.fromClassSimpleName(commandClass).ifPresentOrElse(
                cmd -> auditLogService.record(userId, cmd),
                () -> LOGGER.warn("No CommandName mapping for class '{}' - audit row skipped", commandClass)
        );
    }
}
