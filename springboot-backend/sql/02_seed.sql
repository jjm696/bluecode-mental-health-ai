USE `ai-vue`;

INSERT INTO admins (username, password_hash, nickname, avatar)
VALUES
('admin', '$2b$10$Xg4xDqR8X0Vj6mJf2b4J9uM.Q2uGg3YQjD8k0i4aH9m8fKf7Qm8Ky', '系统管理员', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png')
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO users (username, password_hash, nickname, phone, gender, last_login_at)
VALUES
('linran', '$2b$10$demoHashValue001', '林然', '13800000001', 1, '2026-05-23 09:40:00'),
('zhoutang', '$2b$10$demoHashValue002', '周棠', '13800000002', 2, '2026-05-22 20:12:00'),
('chenyu', '$2b$10$demoHashValue003', '陈语', '13800000003', 0, '2026-05-21 08:38:00')
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO article_categories (id, name, sort_no)
VALUES
(1, '心理健康基础', 1),
(2, '情绪管理', 2),
(3, '睡眠修复', 3),
(4, '自我成长', 4)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO article_tags (id, name)
VALUES
(1, '焦虑'),
(2, '呼吸训练'),
(3, '睡眠'),
(4, '自我成长'),
(5, '压力')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO knowledge_articles (id, category_id, title, summary, content, read_count, status, created_by)
VALUES
(1, 2, '当焦虑突然袭来时，你可以先做这 3 步', '帮助用户在短时间内降低焦虑唤醒水平。', '### 第一步：暂停自我指责\n先允许自己处在焦虑里，而不是立刻否定自己。', 3214, 1, 1),
(2, 3, '睡前总想很多？建立温和的夜间过渡仪式', '通过固定仪式降低睡前反刍。', '### 减少屏幕刺激\n睡前 30 分钟离开高刺激内容。', 1986, 1, 1),
(3, 1, '如何识别高压下的情绪透支信号', '识别持续疲惫、易怒、失眠等前兆。', '### 常见信号\n持续疲惫、注意力下降、对小事过度敏感。', 1450, 0, 1)
ON DUPLICATE KEY UPDATE title = VALUES(title);

INSERT INTO article_tag_relations (article_id, tag_id)
VALUES
(1, 1), (1, 2), (2, 3), (3, 5)
ON DUPLICATE KEY UPDATE article_id = VALUES(article_id);

INSERT INTO consultation_sessions (id, user_id, session_title, summary, dominant_emotion, risk_level, message_count, last_message_at)
VALUES
(1, 1, '最近总担心工作做不好', '建议先稳定睡眠节律，再拆解高压任务。', '焦虑', 1, 12, '2026-05-23 09:42:00'),
(2, 2, '和朋友闹翻后很难受', '先区分事实与推测，再考虑沟通。', '沮丧', 1, 8, '2026-05-22 20:10:00'),
(3, 3, '连续几天没有动力起床', '建议人工复核生活功能受损情况。', '压力', 2, 16, '2026-05-21 08:37:00')
ON DUPLICATE KEY UPDATE session_title = VALUES(session_title);

INSERT INTO consultation_messages (session_id, sender_role, content, sent_at)
VALUES
(1, 'user', '我最近一上班就很紧张，总觉得会出错。', '2026-05-23 09:21:00'),
(1, 'ai', '你已经注意到这种紧张感了。它通常在什么场景最强烈？', '2026-05-23 09:22:00'),
(2, 'user', '我和朋友吵架之后一直很难受。', '2026-05-22 19:48:00'),
(3, 'user', '这几天醒来就很累，不想起床。', '2026-05-21 08:12:00');

INSERT INTO emotion_logs (id, user_id, emotion, mood_score, sleep_quality, stress_level, content, risk_level, created_at)
VALUES
(1, 1, '焦虑', 68, '一般', '较高', '今天一想到项目汇报就胸口发紧，早上出门前反复检查资料。', 1, '2026-05-23 08:10:00'),
(2, 2, '悲伤', 52, '良好', '中等', '和朋友冷战后一直在想是不是自己说错了什么。', 1, '2026-05-22 21:16:00'),
(3, 3, '平静', 32, '较差', '高', '表面上很平静，但其实什么都不想做。', 2, '2026-05-21 07:45:00')
ON DUPLICATE KEY UPDATE emotion = VALUES(emotion);

INSERT INTO emotion_ai_analyses (emotion_log_id, keywords, suggestion, risk_notice, improvements, analysis_model)
VALUES
(1, JSON_ARRAY('汇报压力', '自我怀疑', '身体紧绷'), '先通过呼吸和任务拆解降低唤醒水平。', '当前为关注级别，若连续数日睡眠受影响，建议人工复核。', JSON_ARRAY('今晚提前30分钟停止工作', '列出汇报重点', '记录一次顺利完成的经历'), 'gpt-4.1-mini'),
(2, JSON_ARRAY('人际冲突', '反复回想', '委屈感'), '先承认情绪受伤，再把事实与推测分开。', '暂无高危信号，但建议关注持续社交回避。', JSON_ARRAY('写下事实经过', '暂缓深夜争辩', '选择平和时机表达需求'), 'gpt-4.1-mini'),
(3, JSON_ARRAY('低动力', '食欲下降', '持续疲惫'), '建议尽快安排人工回访。', '已达到预警级别，若持续加重需尽快升级处理。', JSON_ARRAY('联系可信任的人', '完成一个最小行动任务', '记录睡眠和进食情况'), 'gpt-4.1-mini')
ON DUPLICATE KEY UPDATE suggestion = VALUES(suggestion);
