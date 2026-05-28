package com.psychology.assistant.model.dto;

import javax.validation.constraints.NotBlank;

public class ChatSendRequest {

    private Long sessionId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
