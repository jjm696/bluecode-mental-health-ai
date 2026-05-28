package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Object> health() {
        return ApiResponse.success(Collections.singletonMap("service", "springboot-psychology-server"));
    }
}
