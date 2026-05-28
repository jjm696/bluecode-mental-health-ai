package com.psychology.assistant.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {

    @Select("SELECT " +
        "(SELECT COUNT(*) FROM users) AS totalUsers, " +
        "(SELECT COUNT(*) FROM knowledge_articles) AS totalArticles, " +
        "(SELECT COUNT(*) FROM consultation_sessions) AS totalConsultations, " +
        "(SELECT COUNT(*) FROM emotion_logs) AS totalEmotionLogs")
    Map<String, Object> selectOverview();

    @Select("SELECT DATE_FORMAT(created_at, '%m-%d') AS date, ROUND(AVG(mood_score)) AS avgMoodScore, COUNT(*) AS recordCount " +
        "FROM emotion_logs GROUP BY DATE(created_at) ORDER BY DATE(created_at) DESC LIMIT 7")
    List<Map<String, Object>> selectEmotionTrend();

    @Select("SELECT risk_level AS level, COUNT(*) AS count FROM emotion_logs GROUP BY risk_level ORDER BY risk_level ASC")
    List<Map<String, Object>> selectRiskDistribution();
}
