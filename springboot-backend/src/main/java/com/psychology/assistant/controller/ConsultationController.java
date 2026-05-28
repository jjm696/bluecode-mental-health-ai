package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.service.ConsultationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    @Resource
    private ConsultationService consultationService;

    @GetMapping
    public ApiResponse<Object> list(@RequestParam(defaultValue = "") String userName,
                                    @RequestParam(defaultValue = "") String riskLevel,
                                    @RequestParam(defaultValue = "") String dominantEmotion,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResponse.success(
            consultationService.getSessions(userName, riskLevel, dominantEmotion, page, pageSize)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> detail(@PathVariable Long id) {
        return ApiResponse.success(consultationService.getDetail(id));
    }
}
