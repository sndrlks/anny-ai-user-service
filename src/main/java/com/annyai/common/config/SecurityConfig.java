package com.annyai.common.config;

import com.annyai.auth.filter.JwtAuthenticationFilter;
import com.annyai.auth.handler.SecurityExceptionHandlerFactory;
import com.annyai.common.Routes;
import org.springframework.http.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter,
                                                   SecurityExceptionHandlerFactory securityExceptionHandlerFactory) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints: login, refresh, register
                        .requestMatchers(HttpMethod.POST, Routes.Auth.BASE + Routes.Auth.LOGIN).permitAll()
                        .requestMatchers(HttpMethod.POST, Routes.Auth.BASE + Routes.Auth.REFRESH).permitAll()
                        .requestMatchers(HttpMethod.POST, Routes.User.BASE + Routes.User.REGISTER).permitAll()
                        // Logout requires authenticated user with appropriate role
                        .requestMatchers(HttpMethod.POST, Routes.Auth.BASE + Routes.Auth.LOGOUT).hasAnyRole("USER", "ADMIN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .accessDeniedHandler(securityExceptionHandlerFactory.accessDeniedHandler())
                        .authenticationEntryPoint(securityExceptionHandlerFactory.authenticationEntryPoint())
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}