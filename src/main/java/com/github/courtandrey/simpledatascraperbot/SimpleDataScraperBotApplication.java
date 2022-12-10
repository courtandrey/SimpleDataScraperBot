package com.github.courtandrey.simpledatascraperbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class SimpleDataScraperBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleDataScraperBotApplication.class, args);
    }
}