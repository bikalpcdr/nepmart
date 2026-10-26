package com.bikalpa.nepmart.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Provides a single {@link ObjectMapper} (Jackson 2) so the application and its
 * DTOs agree on one stack. Spring Boot 4.1.1 defaults to Jackson 3
 * ({@code tools.jackson}); without an explicit bean the injected
 * {@code com.fasterxml.jackson.databind.ObjectMapper} fields fail. */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }
}