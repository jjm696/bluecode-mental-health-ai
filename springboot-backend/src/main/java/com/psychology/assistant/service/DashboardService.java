package com.psychology.assistant.service;

import com.psychology.assistant.mapper.DashboardMapper;
import com.psychology.assistant.model.vo.DashboardOverviewVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

@Service
public class DashboardService {

    @Resource
    private DashboardMapper dashboardMapper;

    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        vo.setOverview(dashboardMapper.selectOverview());
        vo.setEmotionTrend(dashboardMapper.selectEmotionTrend());
        vo.setRiskDistribution(dashboardMapper.selectRiskDistribution());
        return vo;
    }
}
