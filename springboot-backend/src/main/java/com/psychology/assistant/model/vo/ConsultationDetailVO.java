package com.psychology.assistant.model.vo;

import com.psychology.assistant.model.entity.ConsultationMessageRow;
import com.psychology.assistant.model.entity.ConsultationSessionRow;

import java.util.List;

public class ConsultationDetailVO extends ConsultationSessionRow {

    private List<ConsultationMessageRow> messages;

    public List<ConsultationMessageRow> getMessages() {
        return messages;
    }

    public void setMessages(List<ConsultationMessageRow> messages) {
        this.messages = messages;
    }
}
