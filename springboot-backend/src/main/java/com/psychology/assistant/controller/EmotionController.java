package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.model.dto.EmotionDiarySaveRequest;
import com.psychology.assistant.service.EmotionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {

    @Resource
    private EmotionService emotionService;

    @GetMapping
    public ApiResponse<Object> list(@RequestParam(defaultValue = "") String userName,
                                    @RequestParam(defaultValue = "") String emotion,
                                    @RequestParam(defaultValue = "") String riskLevel,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(
            emotionService.getLogs(userName, emotion, riskLevel, page, pageSize)
        );
    }

    @PostMapping
    public ApiResponse<Object> save(@Validated @RequestBody EmotionDiarySaveRequest request) {
        return ApiResponse.success("保存成功", emotionService.saveDiary(request));
    }
}
