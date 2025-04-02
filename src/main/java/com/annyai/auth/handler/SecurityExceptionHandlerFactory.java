package com.annyai.auth.handler;

import jakarta.servlet.http.HttpServletResponse;

import com.annyai.common.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityExceptionHandlerFactory {

    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(SecurityExceptionHandlerFactory.class);

    public SecurityExceptionHandlerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            log.warn("Access denied for path [{}]: {}", request.getRequestURI(), ex.getMessage());

            ApiErrorResponse error = new ApiErrorResponse(
                    HttpStatus.FORBIDDEN,
                    "Access Denied",
                    List.of(ex.getMessage())
            );

            writeError(response, HttpStatus.FORBIDDEN, error);
        };
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            log.warn("Unauthorized request to [{}]: {}", request.getRequestURI(), ex.getMessage());

            ApiErrorResponse error = new ApiErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized",
                    List.of(ex.getMessage())
            );

            writeError(response, HttpStatus.UNAUTHORIZED, error);
        };
    }

    private void writeError(HttpServletResponse response, HttpStatus status, ApiErrorResponse error) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }
}