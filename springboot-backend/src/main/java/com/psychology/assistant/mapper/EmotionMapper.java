package com.psychology.assistant.mapper;

import com.psychology.assistant.model.entity.EmotionLogRow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmotionMapper {

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM emotion_logs e ",
        "INNER JOIN users u ON e.user_id = u.id ",
        "WHERE (#{userName} = '' OR u.nickname LIKE CONCAT('%', #{userName}, '%')) ",
        "AND (#{emotion} = '' OR e.emotion = #{emotion}) ",
        "AND (#{riskLevel} = '' OR e.risk_level = #{riskLevel})",
        "</script>"
    })
    long countLogs(@Param("userName") String userName,
                   @Param("emotion") String emotion,
                   @Param("riskLevel") String riskLevel);

    @Select({
        "<script>",
        "SELECT e.id, u.nickname AS userName, e.emotion, e.mood_score AS moodScore, e.risk_level AS riskLevel, ",
        "e.sleep_quality AS sleepQuality, e.stress_level AS stressLevel, e.content, ",
        "DATE_FORMAT(e.created_at, '%Y-%m-%d %H:%i') AS createdAt, a.keywords, a.suggestion, ",
        "a.risk_notice AS riskNotice, a.improvements ",
        "FROM emotion_logs e ",
        "INNER JOIN users u ON e.user_id = u.id ",
        "LEFT JOIN emotion_ai_analyses a ON e.id = a.emotion_log_id ",
        "WHERE (#{userName} = '' OR u.nickname LIKE CONCAT('%', #{userName}, '%')) ",
        "AND (#{emotion} = '' OR e.emotion = #{emotion}) ",
        "AND (#{riskLevel} = '' OR e.risk_level = #{riskLevel}) ",
        "ORDER BY e.created_at DESC LIMIT #{pageSize} OFFSET #{offset}",
        "</script>"
    })
    List<EmotionLogRow> selectLogs(@Param("userName") String userName,
                                   @Param("emotion") String emotion,
                                   @Param("riskLevel") String riskLevel,
                                   @Param("pageSize") int pageSize,
                                   @Param("offset") int offset);

    @Select("SELECT e.id, u.nickname AS userName, e.emotion, e.mood_score AS moodScore, e.risk_level AS riskLevel, " +
        "e.sleep_quality AS sleepQuality, e.stress_level AS stressLevel, e.content, " +
        "DATE_FORMAT(e.created_at, '%Y-%m-%d %H:%i') AS createdAt, a.keywords, a.suggestion, " +
        "a.risk_notice AS riskNotice, a.improvements " +
        "FROM emotion_logs e " +
        "INNER JOIN users u ON e.user_id = u.id " +
        "LEFT JOIN emotion_ai_analyses a ON e.id = a.emotion_log_id " +
        "WHERE e.user_id = #{userId} ORDER BY e.created_at DESC")
    List<EmotionLogRow> selectLogsByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO emotion_logs(user_id, emotion, mood_score, sleep_quality, stress_level, content, risk_level) " +
        "VALUES(#{userId}, #{emotion}, #{moodScore}, #{sleepQuality}, #{stressLevel}, #{content}, #{riskLevel})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertEmotionLog(EmotionInsertRecord record);

    @Insert("INSERT INTO emotion_ai_analyses(emotion_log_id, keywords, suggestion, risk_notice, improvements, analysis_model) " +
        "VALUES(#{emotionLogId}, #{keywords}, #{suggestion}, #{riskNotice}, #{improvements}, #{analysisModel})")
    int insertEmotionAnalysis(EmotionAnalysisInsertRecord record);

    class EmotionInsertRecord {
        private Long id;
        private Long userId;
        private String emotion;
        private Integer moodScore;
        private String sleepQuality;
        private String stressLevel;
        private String content;
        private Integer riskLevel;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmotion() { return emotion; }
        public void setEmotion(String emotion) { this.emotion = emotion; }
        public Integer getMoodScore() { return moodScore; }
        public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }
        public String getSleepQuality() { return sleepQuality; }
        public void setSleepQuality(String sleepQuality) { this.sleepQuality = sleepQuality; }
        public String getStressLevel() { return stressLevel; }
        public void setStressLevel(String stressLevel) { this.stressLevel = stressLevel; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getRiskLevel() { return riskLevel; }
        public void setRiskLevel(Integer riskLevel) { this.riskLevel = riskLevel; }
    }

    class EmotionAnalysisInsertRecord {
        private Long emotionLogId;
        private String keywords;
        private String suggestion;
        private String riskNotice;
        private String improvements;
        private String analysisModel;
        public Long getEmotionLogId() { return emotionLogId; }
        public void setEmotionLogId(Long emotionLogId) { this.emotionLogId = emotionLogId; }
        public String getKeywords() { return keywords; }
        public void setKeywords(String keywords) { this.keywords = keywords; }
        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
        public String getRiskNotice() { return riskNotice; }
        public void setRiskNotice(String riskNotice) { this.riskNotice = riskNotice; }
        public String getImprovements() { return improvements; }
        public void setImprovements(String improvements) { this.improvements = improvements; }
        public String getAnalysisModel() { return analysisModel; }
        public void setAnalysisModel(String analysisModel) { this.analysisModel = analysisModel; }
    }
}
