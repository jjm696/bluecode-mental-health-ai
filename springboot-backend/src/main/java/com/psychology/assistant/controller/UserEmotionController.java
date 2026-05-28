package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.service.EmotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user/emotions")
public class UserEmotionController {

    @Resource
    private EmotionService emotionService;

    @GetMapping
    public ApiResponse<Object> list() {
        return ApiResponse.success(emotionService.getCurrentUserLogs());
    }
}
