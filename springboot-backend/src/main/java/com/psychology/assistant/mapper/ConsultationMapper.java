package com.psychology.assistant.mapper;

import com.psychology.assistant.model.entity.ConsultationMessageRow;
import com.psychology.assistant.model.entity.ConsultationSessionRow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConsultationMapper {

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM consultation_sessions s ",
        "INNER JOIN users u ON s.user_id = u.id ",
        "WHERE (#{userName} = '' OR u.nickname LIKE CONCAT('%', #{userName}, '%')) ",
        "AND (#{riskLevel} = '' OR s.risk_level = #{riskLevel}) ",
        "AND (#{dominantEmotion} = '' OR s.dominant_emotion = #{dominantEmotion})",
        "</script>"
    })
    long countSessions(@Param("userName") String userName,
                       @Param("riskLevel") String riskLevel,
                       @Param("dominantEmotion") String dominantEmotion);

    @Select({
        "<script>",
        "SELECT s.id, s.session_title AS sessionTitle, u.nickname AS userName, s.summary, ",
        "s.summary AS preview, s.dominant_emotion AS dominantEmotion, s.risk_level AS riskLevel, ",
        "s.message_count AS messageCount, DATE_FORMAT(s.updated_at, '%Y-%m-%d %H:%i') AS updatedAt ",
        "FROM consultation_sessions s ",
        "INNER JOIN users u ON s.user_id = u.id ",
        "WHERE (#{userName} = '' OR u.nickname LIKE CONCAT('%', #{userName}, '%')) ",
        "AND (#{riskLevel} = '' OR s.risk_level = #{riskLevel}) ",
        "AND (#{dominantEmotion} = '' OR s.dominant_emotion = #{dominantEmotion}) ",
        "ORDER BY s.updated_at DESC LIMIT #{pageSize} OFFSET #{offset}",
        "</script>"
    })
    List<ConsultationSessionRow> selectSessions(@Param("userName") String userName,
                                                @Param("riskLevel") String riskLevel,
                                                @Param("dominantEmotion") String dominantEmotion,
                                                @Param("pageSize") int pageSize,
                                                @Param("offset") int offset);

    @Select("SELECT s.id, s.session_title AS sessionTitle, u.nickname AS userName, s.summary, s.summary AS preview, " +
        "s.dominant_emotion AS dominantEmotion, s.risk_level AS riskLevel, s.message_count AS messageCount, " +
        "DATE_FORMAT(s.updated_at, '%Y-%m-%d %H:%i') AS updatedAt " +
        "FROM consultation_sessions s INNER JOIN users u ON s.user_id = u.id WHERE s.id = #{id} LIMIT 1")
    ConsultationSessionRow selectSessionDetail(@Param("id") Long id);

    @Select("SELECT id, sender_role AS role, content, DATE_FORMAT(sent_at, '%H:%i') AS time " +
        "FROM consultation_messages WHERE session_id = #{sessionId} ORDER BY sent_at ASC")
    List<ConsultationMessageRow> selectMessages(@Param("sessionId") Long sessionId);

    @Select("SELECT s.id, s.session_title AS sessionTitle, u.nickname AS userName, s.summary, s.summary AS preview, " +
        "s.dominant_emotion AS dominantEmotion, s.risk_level AS riskLevel, s.message_count AS messageCount, " +
        "DATE_FORMAT(s.updated_at, '%Y-%m-%d %H:%i') AS updatedAt " +
        "FROM consultation_sessions s INNER JOIN users u ON s.user_id = u.id " +
        "WHERE s.user_id = #{userId} ORDER BY s.updated_at DESC")
    List<ConsultationSessionRow> selectUserSessions(@Param("userId") Long userId);

    @Select("SELECT s.id, s.session_title AS sessionTitle, u.nickname AS userName, s.summary, s.summary AS preview, " +
        "s.dominant_emotion AS dominantEmotion, s.risk_level AS riskLevel, s.message_count AS messageCount, " +
        "DATE_FORMAT(s.updated_at, '%Y-%m-%d %H:%i') AS updatedAt " +
        "FROM consultation_sessions s INNER JOIN users u ON s.user_id = u.id " +
        "WHERE s.id = #{id} AND s.user_id = #{userId} LIMIT 1")
    ConsultationSessionRow selectUserSessionDetail(@Param("id") Long id, @Param("userId") Long userId);

    @Insert("INSERT INTO consultation_sessions(user_id, session_title, summary, dominant_emotion, risk_level, message_count, last_message_at) " +
        "VALUES(#{userId}, #{sessionTitle}, #{summary}, #{dominantEmotion}, #{riskLevel}, #{messageCount}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertSession(SessionInsertRecord record);

    @Insert("INSERT INTO consultation_messages(session_id, sender_role, content, sent_at) VALUES(#{sessionId}, #{senderRole}, #{content}, NOW())")
    int insertMessage(MessageInsertRecord record);

    @Update("UPDATE consultation_sessions SET summary = #{summary}, dominant_emotion = #{dominantEmotion}, risk_level = #{riskLevel}, " +
        "message_count = message_count + #{messageIncrement}, last_message_at = NOW(), updated_at = NOW() WHERE id = #{id}")
    int updateSessionAfterMessage(SessionUpdateRecord record);

    @Delete("DELETE FROM consultation_sessions WHERE id = #{id} AND user_id = #{userId}")
    int deleteUserSession(@Param("id") Long id, @Param("userId") Long userId);

    class SessionInsertRecord {
        private Long id;
        private Long userId;
        private String sessionTitle;
        private String summary;
        private String dominantEmotion;
        private Integer riskLevel;
        private Integer messageCount;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getSessionTitle() { return sessionTitle; }
        public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getDominantEmotion() { return dominantEmotion; }
        public void setDominantEmotion(String dominantEmotion) { this.dominantEmotion = dominantEmotion; }
        public Integer getRiskLevel() { return riskLevel; }
        public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
        public Integer getMessageCount() { return messageCount; }
        public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
    }

    class MessageInsertRecord {
        private Long sessionId;
        private String senderRole;
        private String content;
        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
        public String getSenderRole() { return senderRole; }
        public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    class SessionUpdateRecord {
        private Long id;
        private String summary;
        private String dominantEmotion;
        private Integer riskLevel;
        private Integer messageIncrement;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getDominantEmotion() { return dominantEmotion; }
        public void setDominantEmotion(String dominantEmotion) { this.dominantEmotion = dominantEmotion; }
        public Integer getRiskLevel() { return riskLevel; }
        public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
        public Integer getMessageIncrement() { return messageIncrement; }
        public void setMessageIncrement(Integer messageIncrement) { this.messageIncrement = messageIncrement; }
    }
}
