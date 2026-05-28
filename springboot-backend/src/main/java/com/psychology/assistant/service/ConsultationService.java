package com.psychology.assistant.service;

import com.psychology.assistant.common.PageResult;
import com.psychology.assistant.common.exception.BusinessException;
import com.psychology.assistant.mapper.ConsultationMapper;
import com.psychology.assistant.model.dto.ChatSendRequest;
import com.psychology.assistant.model.entity.ConsultationMessageRow;
import com.psychology.assistant.model.entity.ConsultationSessionRow;
import com.psychology.assistant.model.vo.ChatSendResponse;
import com.psychology.assistant.model.vo.ConsultationDetailVO;
import com.psychology.assistant.model.vo.UserSessionListItemVO;
import com.psychology.assistant.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultationService {

    @Resource
    private ConsultationMapper consultationMapper;

    @Resource
    private AiChatService aiChatService;

    public PageResult<ConsultationSessionRow> getSessions(String userName, String riskLevel, String dominantEmotion, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = consultationMapper.countSessions(userName, riskLevel, dominantEmotion);
        List<ConsultationSessionRow> list = consultationMapper.selectSessions(userName, riskLevel, dominantEmotion, pageSize, offset);
        return new PageResult<ConsultationSessionRow>(list, total, page, pageSize);
    }

    public ConsultationDetailVO getDetail(Long id) {
        ConsultationSessionRow row = consultationMapper.selectSessionDetail(id);
        if (row == null) {
            throw new BusinessException(404, "咨询记录不存在");
        }

        ConsultationDetailVO detailVO = new ConsultationDetailVO();
        detailVO.setId(row.getId());
        detailVO.setSessionTitle(row.getSessionTitle());
        detailVO.setUserName(row.getUserName());
        detailVO.setSummary(row.getSummary());
        detailVO.setPreview(row.getPreview());
        detailVO.setDominantEmotion(row.getDominantEmotion());
        detailVO.setRiskLevel(row.getRiskLevel());
        detailVO.setMessageCount(row.getMessageCount());
        detailVO.setUpdatedAt(row.getUpdatedAt());
        detailVO.setMessages(consultationMapper.selectMessages(id));
        return detailVO;
    }

    public List<UserSessionListItemVO> getUserSessions() {
        Long userId = AuthContext.getUserId();
        List<ConsultationSessionRow> rows = consultationMapper.selectUserSessions(userId);
        List<UserSessionListItemVO> result = new ArrayList<UserSessionListItemVO>();
        for (ConsultationSessionRow row : rows) {
            UserSessionListItemVO itemVO = new UserSessionListItemVO();
            itemVO.setId(row.getId());
            itemVO.setTitle(row.getSessionTitle());
            itemVO.setPreview(row.getPreview());
            itemVO.setTime(row.getUpdatedAt());
            itemVO.setMessageCount(row.getMessageCount());
            result.add(itemVO);
        }
        return result;
    }

    public ConsultationDetailVO getUserSessionDetail(Long id) {
        Long userId = AuthContext.getUserId();
        ConsultationSessionRow row = consultationMapper.selectUserSessionDetail(id, userId);
        if (row == null) {
            throw new BusinessException(404, "会话不存在");
        }

        ConsultationDetailVO detailVO = new ConsultationDetailVO();
        detailVO.setId(row.getId());
        detailVO.setSessionTitle(row.getSessionTitle());
        detailVO.setUserName(row.getUserName());
        detailVO.setSummary(row.getSummary());
        detailVO.setPreview(row.getPreview());
        detailVO.setDominantEmotion(row.getDominantEmotion());
        detailVO.setRiskLevel(row.getRiskLevel());
        detailVO.setMessageCount(row.getMessageCount());
        detailVO.setUpdatedAt(row.getUpdatedAt());
        detailVO.setMessages(consultationMapper.selectMessages(id));
        return detailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUserSession(Long id) {
        Long userId = AuthContext.getUserId();
        int affectedRows = consultationMapper.deleteUserSession(id, userId);
        if (affectedRows == 0) {
            throw new BusinessException(404, "会话不存在");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatSendResponse sendChat(ChatSendRequest request) {
        Long userId = AuthContext.getUserId();
        Long sessionId = request.getSessionId();
        ConsultationSessionRow sessionRow;

        if (sessionId == null) {
            ConsultationMapper.SessionInsertRecord insertRecord = new ConsultationMapper.SessionInsertRecord();
            insertRecord.setUserId(userId);
            insertRecord.setSessionTitle(buildSessionTitle(request.getContent()));
            insertRecord.setSummary("新的心理咨询会话");
            insertRecord.setDominantEmotion("待分析");
            insertRecord.setRiskLevel(0);
            insertRecord.setMessageCount(0);
            consultationMapper.insertSession(insertRecord);
            sessionId = insertRecord.getId();
        }

        ConsultationMapper.MessageInsertRecord userMessage = new ConsultationMapper.MessageInsertRecord();
        userMessage.setSessionId(sessionId);
        userMessage.setSenderRole("user");
        userMessage.setContent(request.getContent());
        consultationMapper.insertMessage(userMessage);

        List<ConsultationMessageRow> history = consultationMapper.selectMessages(sessionId);
        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        messages.add(systemPrompt(request.getContent()));
        for (ConsultationMessageRow item : history) {
            Map<String, String> message = new HashMap<String, String>();
            message.put("role", "user".equals(item.getRole()) ? "user" : "assistant");
            message.put("content", item.getContent());
            messages.add(message);
        }

        String aiReply = aiChatService.generateReply(messages);

        ConsultationMapper.MessageInsertRecord aiMessage = new ConsultationMapper.MessageInsertRecord();
        aiMessage.setSessionId(sessionId);
        aiMessage.setSenderRole("ai");
        aiMessage.setContent(aiReply);
        consultationMapper.insertMessage(aiMessage);

        ConsultationMapper.SessionUpdateRecord updateRecord = new ConsultationMapper.SessionUpdateRecord();
        updateRecord.setId(sessionId);
        String detectedScene = detectScene(request.getContent());
        String dominantEmotion = guessEmotion(request.getContent());
        int riskLevel = guessRiskLevel(request.getContent());
        updateRecord.setSummary(buildSessionSummary(request.getContent(), aiReply, detectedScene, dominantEmotion, riskLevel));
        updateRecord.setDominantEmotion(dominantEmotion);
        updateRecord.setRiskLevel(riskLevel);
        updateRecord.setMessageIncrement(2);
        consultationMapper.updateSessionAfterMessage(updateRecord);

        sessionRow = consultationMapper.selectUserSessionDetail(sessionId, userId);

        ChatSendResponse response = new ChatSendResponse();
        response.setSessionId(sessionId);
        response.setSessionTitle(sessionRow.getSessionTitle());
        response.setUserMessage(request.getContent());
        response.setAiMessage(aiReply);
        response.setDominantEmotion(sessionRow.getDominantEmotion());
        response.setRiskLevel(sessionRow.getRiskLevel());
        response.setSummary(sessionRow.getSummary());
        return response;
    }

    private Map<String, String> systemPrompt(String userInput) {
        String scene = detectScene(userInput);
        boolean highRisk = isHighRisk(userInput);

        Map<String, String> prompt = new HashMap<String, String>();
        prompt.put("role", "system");
        prompt.put("content",
            "你是一位中文心理健康陪伴助手，名字叫“暖光”。" +
            "你的目标不是直接给结论，而是帮助用户更安全、更自然地表达情绪，逐步看清自己的感受与压力来源。" +
            "请严格遵守以下规则：" +
            "1. 回复语气必须温和、耐心、不过度积极，不要像客服，不要机械安慰。" +
            "2. 当用户表达“累、烦、焦虑、难受、委屈、崩溃、睡不着、压力大”等感受时，优先做共情和情绪命名，例如帮助用户识别疲惫、紧绷、无力、自责、孤独等状态。" +
            "3. 每次回复尽量包含这三步中的两步：先接住情绪，再简短反映用户处境，最后给一个温和的问题或一个很小的引导动作。" +
            "4. 优先使用开放式引导，帮助用户继续说，比如“这种累更像身体上的，还是心里一直绷着的那种？”" +
            "5. 如果用户只说一句“我好累”“压力好大”，不要直接讲大道理，要先帮助他展开：累从什么时候开始、最压人的部分是什么、有没有具体触发事件。" +
            "6. 可以给非常轻量的支持建议，例如呼吸、暂停、自我观察、写下一句话，但不要一次给太多步骤。" +
            "7. 不要进行医疗诊断，不要声称自己是医生，不要夸大疗效。" +
            "8. 如果用户出现明显的自伤、自杀、绝望、活不下去等高风险表达，要立即提醒寻求现实中的紧急帮助和可信任的人支持。" +
            "9. 回复长度控制在80到180字之间，避免过长说教。" +
            "10. 回复中尽量少用模板化套话，多结合用户刚刚说的原句。" +
            buildSceneGuidance(scene) +
            buildRiskGuidance(highRisk) +
            "示例风格：" +
            "用户：我今天好累。 " +
            "你：听起来你不是单纯的困，更像整个人都被压住了。这样的累是从今天某件事开始的，还是已经持续了一阵子？如果你愿意，可以先说说今天最让你心里发沉的一刻。"
        );
        return prompt;
    }

    private String detectScene(String content) {
        String text = content == null ? "" : content;
        if (containsAny(text, "活不下去", "不想活", "想消失", "崩溃", "绝望", "自杀", "伤害自己")) {
            return "HIGH_RISK";
        }
        if (containsAny(text, "累", "疲惫", "压力", "撑不住", "好忙", "压得喘不过气")) {
            return "FATIGUE";
        }
        if (containsAny(text, "焦虑", "紧张", "心慌", "失眠", "担心", "害怕")) {
            return "ANXIETY";
        }
        if (containsAny(text, "难过", "悲伤", "低落", "委屈", "没意义", "不开心")) {
            return "LOW_MOOD";
        }
        if (containsAny(text, "朋友", "家人", "对象", "吵架", "关系", "冷战")) {
            return "RELATIONSHIP";
        }
        return "GENERAL";
    }

    private String buildSceneGuidance(String scene) {
        if ("FATIGUE".equals(scene)) {
            return "当前用户更可能处于疲惫或压力过载状态。请优先帮助用户区分身体累和心理绷紧，先接住无力感，再引导他说出最压人的来源。";
        }
        if ("ANXIETY".equals(scene)) {
            return "当前用户更可能处于焦虑或紧张状态。请优先帮助用户识别担心的对象、最糟糕的预期和身体反应，再用温和问题帮助用户落到具体场景。";
        }
        if ("LOW_MOOD".equals(scene)) {
            return "当前用户更可能处于低落、委屈或悲伤状态。请优先接住情绪，不要催促积极化，先帮助用户说清楚受伤点和失落感来自哪里。";
        }
        if ("RELATIONSHIP".equals(scene)) {
            return "当前用户更可能处于人际关系困扰。请优先帮助用户区分事实、猜测和情绪反应，避免直接评判谁对谁错。";
        }
        if ("HIGH_RISK".equals(scene)) {
            return "当前用户可能出现高风险表达。请明显提高安全优先级，先表达重视，再鼓励立刻联系现实中的可信任亲友、家人或当地紧急援助资源。不要轻描淡写带过。";
        }
        return "当前用户处于一般情绪表达场景。请用温和、开放式问题帮助其继续表达。";
    }

    private String buildRiskGuidance(boolean highRisk) {
        if (!highRisk) {
            return "";
        }
        return "如果用户存在明显危险信号，请在回复中明确建议：立即联系家人、朋友、老师、同事或当地心理危机干预热线，必要时前往医院或拨打紧急求助电话。";
    }

    private String buildSessionTitle(String content) {
        String text = content == null ? "新的咨询会话" : content.trim();
        if (text.isEmpty()) {
            return "新的咨询会话";
        }
        return text.length() > 12 ? text.substring(0, 12) + "..." : text;
    }

    private String buildSessionSummary(String userInput, String aiReply, String scene, String dominantEmotion, int riskLevel) {
        String sceneText = toSceneText(scene);
        String riskText = toRiskText(riskLevel);
        String suggestion = extractSuggestion(aiReply);
        return "场景：" + sceneText
            + "；主要情绪：" + dominantEmotion
            + "；风险等级：" + riskText
            + "；建议摘要：" + suggestion;
    }

    private String guessEmotion(String content) {
        String text = content == null ? "" : content;
        if (containsAny(text, "焦虑", "紧张", "心慌", "担心", "害怕")) return "焦虑";
        if (containsAny(text, "难过", "悲伤", "低落", "委屈")) return "悲伤";
        if (containsAny(text, "累", "疲惫", "撑不住", "压力")) return "压力";
        if (containsAny(text, "吵架", "朋友", "家人", "对象", "冷战")) return "沮丧";
        return "平静";
    }

    private int guessRiskLevel(String content) {
        String text = content == null ? "" : content;
        if (containsAny(text, "活不下去", "不想活", "想消失", "自杀", "伤害自己")) return 3;
        if (text.contains("崩溃") || text.contains("绝望")) return 2;
        if (text.contains("焦虑") || text.contains("失眠")) return 1;
        return 0;
    }

    private boolean isHighRisk(String content) {
        return guessRiskLevel(content) >= 2;
    }

    private String toSceneText(String scene) {
        if ("FATIGUE".equals(scene)) return "疲惫压力";
        if ("ANXIETY".equals(scene)) return "焦虑紧张";
        if ("LOW_MOOD".equals(scene)) return "低落悲伤";
        if ("RELATIONSHIP".equals(scene)) return "关系冲突";
        if ("HIGH_RISK".equals(scene)) return "高风险求助";
        return "一般倾诉";
    }

    private String toRiskText(int riskLevel) {
        if (riskLevel == 3) return "危机";
        if (riskLevel == 2) return "预警";
        if (riskLevel == 1) return "关注";
        return "正常";
    }

    private String extractSuggestion(String aiReply) {
        if (aiReply == null || aiReply.trim().isEmpty()) {
            return "建议继续表达当前最明显的感受与触发事件。";
        }
        String text = aiReply.replace("\n", " ").trim();
        return text.length() > 70 ? text.substring(0, 70) + "..." : text;
    }

    private boolean containsAny(String text, String... words) {
        if (text == null) {
            return false;
        }
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
