package com.annyai.auth.controller;

import jakarta.validation.Valid;

import com.annyai.auth.dto.LoginRequest;
import com.annyai.auth.dto.LoginResponse;
import com.annyai.auth.service.LoginService;
import com.annyai.common.Routes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Auth.BASE)
public class AuthController {

    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(Routes.Auth.LOGIN)
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = loginService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}