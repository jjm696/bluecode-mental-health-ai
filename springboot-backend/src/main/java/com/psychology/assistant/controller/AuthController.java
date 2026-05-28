package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.model.dto.LoginRequest;
import com.psychology.assistant.model.dto.UserLoginRequest;
import com.psychology.assistant.model.dto.UserRegisterRequest;
import com.psychology.assistant.service.AuthService;
import com.psychology.assistant.service.UserAuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private UserAuthService userAuthService;

    @PostMapping("/admin-login")
    public ApiResponse<Object> adminLogin(@Validated @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.adminLogin(request));
    }

    @GetMapping("/profile")
    public ApiResponse<Object> profile() {
        return ApiResponse.success(authService.getProfile());
    }

    @PostMapping("/user-login")
    public ApiResponse<Object> userLogin(@Validated @RequestBody UserLoginRequest request) {
        return ApiResponse.success(userAuthService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<Object> register(@Validated @RequestBody UserRegisterRequest request) {
        return ApiResponse.success("注册成功", userAuthService.register(request));
    }
}
