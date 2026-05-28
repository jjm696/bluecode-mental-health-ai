package com.psychology.assistant.model.vo;

import java.util.List;

public class EmotionAnalysisVO {

    private List<String> keywords;
    private String suggestion;
    private String riskNotice;
    private List<String> improvements;

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getRiskNotice() {
        return riskNotice;
    }

    public void setRiskNotice(String riskNotice) {
        this.riskNotice = riskNotice;
    }

    public List<String> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<String> improvements) {
        this.improvements = improvements;
    }
}
