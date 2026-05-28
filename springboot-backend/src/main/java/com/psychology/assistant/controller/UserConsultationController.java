package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.model.dto.ChatSendRequest;
import com.psychology.assistant.service.ConsultationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user/consultations")
public class UserConsultationController {

    @Resource
    private ConsultationService consultationService;

    @GetMapping
    public ApiResponse<Object> sessions() {
        return ApiResponse.success(consultationService.getUserSessions());
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> detail(@PathVariable Long id) {
        return ApiResponse.success(consultationService.getUserSessionDetail(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        consultationService.deleteUserSession(id);
        return ApiResponse.success("删除成功", true);
    }

    @PostMapping("/send")
    public ApiResponse<Object> send(@Validated @RequestBody ChatSendRequest request) {
        return ApiResponse.success(consultationService.sendChat(request));
    }
}
