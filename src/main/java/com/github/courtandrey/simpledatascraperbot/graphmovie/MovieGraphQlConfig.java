package com.github.courtandrey.simpledatascraperbot.graphmovie;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.scalars.ExtendedScalars;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class MovieGraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer dateScalarConfigurer() {
        return wiring -> wiring.scalar(ExtendedScalars.Date);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.graphql.introspection.enabled", havingValue = "false")
    public Instrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(12);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.graphql.introspection.enabled", havingValue = "false")
    public Instrumentation maxQueryComplexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(250);
    }
}
