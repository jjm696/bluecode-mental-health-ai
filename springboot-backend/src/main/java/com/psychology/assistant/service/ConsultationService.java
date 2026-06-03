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
        messages.add(systemPrompt(request.getContent(), history.size()));
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
        updateRecord.setSummary(buildSessionSummary(aiReply, detectedScene, dominantEmotion, riskLevel));
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

    private Map<String, String> systemPrompt(String userInput, int historySize) {
        String scene = detectScene(userInput);
        boolean highRisk = isHighRisk(userInput);
        String stage = resolveConversationStage(historySize, highRisk);

        Map<String, String> prompt = new HashMap<String, String>();
        prompt.put("role", "system");
        prompt.put("content",
            "你是中文心理健康陪伴助手，名字叫“暖光”。你的定位是陪伴、倾听、澄清和轻量支持，不做医疗诊断，不承诺治疗效果。\n" +
            "核心目标：让用户更安全、更自然地表达情绪，逐步看清压力来源，并在合适时给出一个小而可执行的建议。\n\n" +
            "对话总原则：\n" +
            "1. 先接住情绪，再反映处境，最后只给一个开放问题或一个很小的行动建议。\n" +
            "2. 不要一上来讲道理、列清单、催促积极，也不要使用客服式套话。\n" +
            "3. 每次回复尽量结合用户刚说过的原句，让用户感到被具体听见。\n" +
            "4. 如果用户只说“好累”“压力大”“不知道怎么办”，优先帮助展开：从什么时候开始、最压人的部分是什么、身体和心里哪个更明显。\n" +
            "5. 建议要轻量，例如停下来喝水、做三轮慢呼吸、写下一句最真实的感受、把任务拆成下一小步；不要一次给太多步骤。\n" +
            "6. 回复控制在 80 到 180 字之间，语气温和、稳定、不过度煽情。\n" +
            "7. 避免诊断词和绝对判断，例如“你就是抑郁症”“一定会好起来”。\n\n" +
            buildStageGuidance(stage) +
            buildSceneGuidance(scene) +
            buildRiskGuidance(highRisk) +
            "推荐回复结构：共情一句 + 具体反映一句 + 一个温和问题或一个微行动。\n" +
            "示例：听起来你不是单纯困，而是整个人都被压住了一些。先不用急着把问题解决掉，我们可以先看清它：这种累更像身体透支，还是心里一直绷着的那种累？"
        );
        return prompt;
    }

    private String resolveConversationStage(int historySize, boolean highRisk) {
        if (highRisk) {
            return "RISK_ALERT";
        }
        if (historySize <= 2) {
            return "EXPRESSING";
        }
        if (historySize <= 6) {
            return "CLARIFYING";
        }
        return "SUPPORTING";
    }

    private String buildStageGuidance(String stage) {
        if ("RISK_ALERT".equals(stage)) {
            return "当前阶段：安全优先。请明确表达重视，建议用户立刻联系现实中的可信任的人，并在有即时危险时寻求当地紧急帮助。\n";
        }
        if ("EXPRESSING".equals(stage)) {
            return "当前阶段：刚开始倾诉。请少给建议，重点是接住情绪、降低表达门槛，让用户愿意继续说。\n";
        }
        if ("CLARIFYING".equals(stage)) {
            return "当前阶段：澄清情绪来源。请帮助用户区分事件、感受、想法和身体反应，问题要具体但不审问。\n";
        }
        return "当前阶段：轻量支持。可以给一个可执行的小建议，并询问这个建议对用户来说是否可行。\n";
    }

    private String detectScene(String content) {
        String text = content == null ? "" : content;
        if (containsAny(text, "活不下去", "不想活", "想消失", "崩溃", "绝望", "自杀", "自残", "伤害自己")) {
            return "HIGH_RISK";
        }
        if (containsAny(text, "累", "疲惫", "压力", "撑不住", "好忙", "压得喘不过气", "透支")) {
            return "FATIGUE";
        }
        if (containsAny(text, "焦虑", "紧张", "心慌", "失眠", "担心", "害怕", "恐惧")) {
            return "ANXIETY";
        }
        if (containsAny(text, "难过", "悲伤", "低落", "委屈", "没意义", "不开心", "空虚")) {
            return "LOW_MOOD";
        }
        if (containsAny(text, "朋友", "家人", "对象", "恋爱", "吵架", "关系", "冷战", "同学", "同事")) {
            return "RELATIONSHIP";
        }
        return "GENERAL";
    }

    private String buildSceneGuidance(String scene) {
        if ("FATIGUE".equals(scene)) {
            return "场景策略：用户可能处于疲惫或压力过载。先帮助区分身体累和心理绷紧，再引导他说出最压人的来源。\n";
        }
        if ("ANXIETY".equals(scene)) {
            return "场景策略：用户可能处于焦虑或紧张。先帮助定位正在担心什么、最糟预期是什么、身体有什么反应，再给稳定感。\n";
        }
        if ("LOW_MOOD".equals(scene)) {
            return "场景策略：用户可能处于低落、委屈或悲伤。不要催促积极，先承认难受是真实的，再询问最近最刺痛他的部分。\n";
        }
        if ("RELATIONSHIP".equals(scene)) {
            return "场景策略：用户可能处于关系困扰。帮助区分事实、猜测和情绪反应，不直接评判谁对谁错。\n";
        }
        if ("HIGH_RISK".equals(scene)) {
            return "场景策略：用户可能出现高风险表达。安全优先，回复必须包含现实支持和紧急求助提醒。\n";
        }
        return "场景策略：一般倾诉。使用开放问题帮助用户继续表达，避免过早建议。\n";
    }

    private String buildRiskGuidance(boolean highRisk) {
        if (!highRisk) {
            return "";
        }
        return "高风险规则：如果用户表达自伤、自杀、活不下去或强烈绝望，请建议他立刻联系家人、朋友、老师、同事等可信任的人；如果有即时危险，请马上拨打当地紧急电话或前往医院急诊。不要只用安慰带过。\n";
    }

    private String buildSessionTitle(String content) {
        String text = content == null ? "新的咨询会话" : content.trim();
        if (text.isEmpty()) {
            return "新的咨询会话";
        }
        return text.length() > 12 ? text.substring(0, 12) + "..." : text;
    }

    private String buildSessionSummary(String aiReply, String scene, String dominantEmotion, int riskLevel) {
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
        if (containsAny(text, "焦虑", "紧张", "心慌", "担心", "害怕", "恐惧")) return "焦虑";
        if (containsAny(text, "难过", "悲伤", "低落", "委屈", "空虚")) return "悲伤";
        if (containsAny(text, "累", "疲惫", "撑不住", "压力", "透支")) return "压力";
        if (containsAny(text, "吵架", "朋友", "家人", "对象", "冷战", "关系")) return "困扰";
        if (containsAny(text, "开心", "轻松", "平静", "好多了")) return "平静";
        return "平静";
    }

    private int guessRiskLevel(String content) {
        String text = content == null ? "" : content;
        if (containsAny(text, "活不下去", "不想活", "想消失", "自杀", "自残", "伤害自己")) return 3;
        if (containsAny(text, "崩溃", "绝望", "撑不住了")) return 2;
        if (containsAny(text, "焦虑", "失眠", "压力", "低落", "难过")) return 1;
        return 0;
    }

    private boolean isHighRisk(String content) {
        return guessRiskLevel(content) >= 2;
    }

    private String toSceneText(String scene) {
        if ("FATIGUE".equals(scene)) return "疲惫压力";
        if ("ANXIETY".equals(scene)) return "焦虑紧张";
        if ("LOW_MOOD".equals(scene)) return "低落悲伤";
        if ("RELATIONSHIP".equals(scene)) return "关系困扰";
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
