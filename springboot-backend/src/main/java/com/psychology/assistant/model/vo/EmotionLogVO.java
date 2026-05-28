package com.psychology.assistant.model.vo;

import com.psychology.assistant.model.entity.EmotionLogRow;

public class EmotionLogVO extends EmotionLogRow {

    private EmotionAnalysisVO aiAnalysis;

    public EmotionAnalysisVO getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(EmotionAnalysisVO aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }
}
