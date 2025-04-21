package com.annyai.auth.controller;

import jakarta.validation.Valid;

import com.annyai.auth.dto.LoginRequest;
import com.annyai.auth.dto.LoginResponse;
import com.annyai.auth.dto.LogoutRequest;
import com.annyai.auth.dto.RefreshTokenRequest;
import com.annyai.auth.exception.UnauthorizedException;
import com.annyai.auth.jwt.JwtTokenProvider;
import com.annyai.auth.service.LoginService;
import com.annyai.auth.service.RefreshTokenService;
import com.annyai.common.Routes;
import com.annyai.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping(Routes.Auth.BASE)
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(LoginService loginService, RefreshTokenService refreshTokenService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(Routes.Auth.LOGIN)
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @PostMapping(Routes.Auth.LOGOUT)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(Routes.Auth.REFRESH)
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return refreshTokenService.getEmailFromRefreshToken(request.refreshToken())
                .map(email -> {
                    UserDetails user = userService.loadUserByUsername(email);
                    String newAccessToken = jwtTokenProvider.generateAccessToken(user);
                    String newRefreshToken = refreshTokenService.generateRefreshToken(email);
                    refreshTokenService.revokeRefreshToken(request.refreshToken());
                    return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshToken));
                })
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
    }
}