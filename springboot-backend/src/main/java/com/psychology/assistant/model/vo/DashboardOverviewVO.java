package com.psychology.assistant.model.vo;

import java.util.List;
import java.util.Map;

public class DashboardOverviewVO {

    private Map<String, Object> overview;
    private List<Map<String, Object>> emotionTrend;
    private List<Map<String, Object>> riskDistribution;

    public Map<String, Object> getOverview() {
        return overview;
    }

    public void setOverview(Map<String, Object> overview) {
        this.overview = overview;
    }

    public List<Map<String, Object>> getEmotionTrend() {
        return emotionTrend;
    }

    public void setEmotionTrend(List<Map<String, Object>> emotionTrend) {
        this.emotionTrend = emotionTrend;
    }

    public List<Map<String, Object>> getRiskDistribution() {
        return riskDistribution;
    }

    public void setRiskDistribution(List<Map<String, Object>> riskDistribution) {
        this.riskDistribution = riskDistribution;
    }
}
