package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public ApiResponse<Object> overview() {
        return ApiResponse.success(dashboardService.getOverview());
    }
}
