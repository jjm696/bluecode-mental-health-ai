package com.psychology.assistant.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EmotionDiarySaveRequest {

    @NotBlank(message = "情绪不能为空")
    private String emotion;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "睡眠评分不能为空")
    private Integer sleepScore;

    @NotNull(message = "压力评分不能为空")
    private Integer stressScore;

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSleepScore() {
        return sleepScore;
    }

    public void setSleepScore(Integer sleepScore) {
        this.sleepScore = sleepScore;
    }

    public Integer getStressScore() {
        return stressScore;
    }

    public void setStressScore(Integer stressScore) {
        this.stressScore = stressScore;
    }
}
