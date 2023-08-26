package com.sage.sage.microservices.azure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HttpTriggerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpTriggerDemoApplication.class, args);
    }

    @Bean
    public Function<String, String> uppercase() {
        return payload -> payload.toUpperCase();
    }

    @Bean
    public Function<String, String> reverse() {
        return payload -> new StringBuilder(payload).reverse().toString();
    }
}


