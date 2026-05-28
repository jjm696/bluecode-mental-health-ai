package com.psychology.assistant.service;

import com.psychology.assistant.common.PageResult;
import com.psychology.assistant.mapper.EmotionMapper;
import com.psychology.assistant.model.dto.EmotionDiarySaveRequest;
import com.psychology.assistant.model.entity.EmotionLogRow;
import com.psychology.assistant.model.vo.EmotionAnalysisVO;
import com.psychology.assistant.model.vo.EmotionLogVO;
import com.psychology.assistant.security.AuthContext;
import com.psychology.assistant.util.JsonArrayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EmotionService {

    @Resource
    private EmotionMapper emotionMapper;

    public PageResult<EmotionLogVO> getLogs(String userName, String emotion, String riskLevel, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = emotionMapper.countLogs(userName, emotion, riskLevel);
        List<EmotionLogRow> rows = emotionMapper.selectLogs(userName, emotion, riskLevel, pageSize, offset);
        List<EmotionLogVO> list = new ArrayList<EmotionLogVO>();

        for (EmotionLogRow row : rows) {
            EmotionLogVO vo = new EmotionLogVO();
            vo.setId(row.getId());
            vo.setUserName(row.getUserName());
            vo.setEmotion(row.getEmotion());
            vo.setMoodScore(row.getMoodScore());
            vo.setRiskLevel(row.getRiskLevel());
            vo.setSleepQuality(row.getSleepQuality());
            vo.setStressLevel(row.getStressLevel());
            vo.setContent(row.getContent());
            vo.setCreatedAt(row.getCreatedAt());

            EmotionAnalysisVO analysisVO = new EmotionAnalysisVO();
            analysisVO.setKeywords(JsonArrayUtil.parseJsonArray(row.getKeywords()));
            analysisVO.setSuggestion(row.getSuggestion());
            analysisVO.setRiskNotice(row.getRiskNotice());
            analysisVO.setImprovements(JsonArrayUtil.parseJsonArray(row.getImprovements()));
            vo.setAiAnalysis(analysisVO);
            list.add(vo);
        }

        return new PageResult<EmotionLogVO>(list, total, page, pageSize);
    }

    public List<EmotionLogVO> getCurrentUserLogs() {
        Long userId = AuthContext.getUserId();
        List<EmotionLogRow> rows = emotionMapper.selectLogsByUserId(userId);
        List<EmotionLogVO> list = new ArrayList<EmotionLogVO>();

        for (EmotionLogRow row : rows) {
            EmotionLogVO vo = new EmotionLogVO();
            vo.setId(row.getId());
            vo.setUserName(row.getUserName());
            vo.setEmotion(row.getEmotion());
            vo.setMoodScore(row.getMoodScore());
            vo.setRiskLevel(row.getRiskLevel());
            vo.setSleepQuality(row.getSleepQuality());
            vo.setStressLevel(row.getStressLevel());
            vo.setContent(row.getContent());
            vo.setCreatedAt(row.getCreatedAt());

            EmotionAnalysisVO analysisVO = new EmotionAnalysisVO();
            analysisVO.setKeywords(JsonArrayUtil.parseJsonArray(row.getKeywords()));
            analysisVO.setSuggestion(row.getSuggestion());
            analysisVO.setRiskNotice(row.getRiskNotice());
            analysisVO.setImprovements(JsonArrayUtil.parseJsonArray(row.getImprovements()));
            vo.setAiAnalysis(analysisVO);
            list.add(vo);
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveDiary(EmotionDiarySaveRequest request) {
        EmotionMapper.EmotionInsertRecord record = new EmotionMapper.EmotionInsertRecord();
        record.setUserId(AuthContext.getUserId());
        record.setEmotion(request.getEmotion());
        record.setMoodScore(calculateMoodScore(request.getSleepScore(), request.getStressScore()));
        record.setSleepQuality(toSleepText(request.getSleepScore()));
        record.setStressLevel(toStressText(request.getStressScore()));
        record.setContent(request.getContent());
        record.setRiskLevel(calculateRiskLevel(request.getStressScore()));
        emotionMapper.insertEmotionLog(record);

        EmotionMapper.EmotionAnalysisInsertRecord analysisRecord = new EmotionMapper.EmotionAnalysisInsertRecord();
        analysisRecord.setEmotionLogId(record.getId());
        analysisRecord.setKeywords(toJsonArray(Arrays.asList(request.getEmotion(), "情绪记录", "自我觉察")));
        analysisRecord.setSuggestion("建议继续记录让情绪最明显的触发事件，并尝试把感受与事件分开描述。");
        analysisRecord.setRiskNotice(request.getStressScore() >= 8 ? "当前压力评分较高，建议尽量尽快休息并考虑寻求支持。" : "当前未发现明显高危信号，建议保持持续记录。");
        analysisRecord.setImprovements(toJsonArray(Arrays.asList("今晚提前放下高刺激信息", "记录一次让你稍微放松的时刻", "如果愿意，可以继续和 AI 咨询页对话")));
        analysisRecord.setAnalysisModel("local-rule-engine");
        emotionMapper.insertEmotionAnalysis(analysisRecord);
        return record.getId();
    }

    private int calculateMoodScore(int sleepScore, int stressScore) {
        int moodScore = (sleepScore * 10 + (10 - stressScore) * 10) / 2;
        if (moodScore < 0) {
            return 0;
        }
        return Math.min(100, moodScore);
    }

    private int calculateRiskLevel(int stressScore) {
        if (stressScore >= 9) return 3;
        if (stressScore >= 7) return 2;
        if (stressScore >= 5) return 1;
        return 0;
    }

    private String toSleepText(int score) {
        if (score >= 8) return "良好";
        if (score >= 5) return "一般";
        return "较差";
    }

    private String toStressText(int score) {
        if (score >= 8) return "高";
        if (score >= 5) return "中等";
        return "较低";
    }

    private String toJsonArray(List<String> values) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("\"").append(values.get(i)).append("\"");
        }
        builder.append("]");
        return builder.toString();
    }
}
