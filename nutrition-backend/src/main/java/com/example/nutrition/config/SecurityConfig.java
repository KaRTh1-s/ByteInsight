package com.example.nutrition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF for Postman testing
            .authorizeHttpRequests()
            .requestMatchers("/api/auth/**", "/api/nutrition").permitAll() // allow login/register without auth
            .anyRequest().authenticated(); // everything else requires authentication

        return http.build();
    }
}
