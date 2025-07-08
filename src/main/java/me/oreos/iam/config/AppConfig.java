package me.oreos.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.oreos.iam.services.TokenProvider;
import me.oreos.iam.services.impl.JwtService;

@Configuration
public class AppConfig {

    @Bean
    TokenProvider tokenProvider() {
        return new JwtService();
    }
}
