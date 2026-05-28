<template>
  <div class="emotionDiary-container">
    <div class="header-section">
      <div class="header-content">
        <img :src="smileIcon" alt="smile" class="header-icon" />
        <div>
          <h1>情绪日记</h1>
          <p>记录今天的感受、事件与身体状态，把模糊的情绪慢慢说清楚。</p>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="diary-card">
        <h2 class="title">1. 你现在最接近哪种情绪？</h2>
        <div class="emotion-grid">
          <div
            v-for="item in emotions"
            :key="item.name"
            class="emotion-card"
            :class="{ selected: selectedEmotion === item.name }"
            @click="selectedEmotion = item.name"
          >
            <img :src="item.icon" :alt="item.name" />
            <div class="emotion-name">{{ item.name }}</div>
          </div>
        </div>
      </div>

      <div class="diary-card detail-form">
        <h2 class="title">2. 写下一点今天的状态</h2>
        <div class="section">
          <p>今天发生了什么？有没有哪一刻让你明显感到紧绷、低落、平静或被安慰？</p>
          <el-input
            v-model="content"
            type="textarea"
            :rows="7"
            placeholder="例如：今天下午开会前很焦虑，担心自己说不好，回家以后依然有点心累。"
          />
        </div>

        <div class="life-indicators">
          <div class="indicator-group">
            <div class="form-label">睡眠质量</div>
            <el-slider v-model="sleepScore" :max="10" />
          </div>
          <div class="indicator-group">
            <div class="form-label">压力指数</div>
            <el-slider v-model="stressScore" :max="10" />
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="success" size="large" :loading="saving" @click="saveDiary">保存日记</el-button>
          <span class="save-tip">登录后可把情绪记录真正保存到数据库</span>
        </div>
      </div>

      <div class="diary-card" v-if="saved">
        <h2 class="title">3. AI 观察摘要</h2>
        <div class="summary-box">
          <p>当前识别情绪：<strong>{{ selectedEmotion }}</strong></p>
          <p>睡眠评分：<strong>{{ sleepScore }}/10</strong>，压力评分：<strong>{{ stressScore }}/10</strong></p>
          <p>
            建议：如果你愿意，可以在情绪最明显的那一刻再补一句“我当时脑子里第一反应是什么”，这会更有助于后续分析。
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { emotionImageMap } from '@/utils/assets'
import { saveEmotionDiary } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const smileIcon = new URL('@/assets/images/smile.png', import.meta.url).href
const userStore = useUserStore()
const router = useRouter()

const emotions = [
  { name: '开心', icon: emotionImageMap.开心 },
  { name: '平静', icon: emotionImageMap.平静 },
  { name: '困惑', icon: emotionImageMap.困惑 },
  { name: '兴奋', icon: emotionImageMap.兴奋 },
  { name: '悲伤', icon: emotionImageMap.悲伤 },
  { name: '焦虑', icon: emotionImageMap.焦虑 },
  { name: '疲惫', icon: emotionImageMap.疲惫 },
]

const selectedEmotion = ref('平静')
const content = ref('')
const sleepScore = ref(6)
const stressScore = ref(5)
const saved = ref(false)
const saving = ref(false)

const saveDiary = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('先写下一点今天的状态')
    return
  }

  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再保存情绪日记')
    router.push('/login')
    return
  }

  saving.value = true
  try {
    await saveEmotionDiary({
      emotion: selectedEmotion.value,
      content: content.value,
      sleepScore: sleepScore.value,
      stressScore: stressScore.value,
    })
    saved.value = true
    ElMessage.success('情绪日记已保存到数据库')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.emotionDiary-container {
  background: linear-gradient(135deg, #fafbfc 0%, #f7f9fc 50%, #f2f6fa 100%);
  min-height: calc(100vh - 140px);
}

.header-section {
  background: linear-gradient(135deg, #7ed321 0%, #f5a623 100%);
  color: white;
  padding: 48px 20px;
}

.header-content {
  max-width: 980px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 52px;
  height: 52px;
}

.content {
  margin: 0 auto;
  width: min(980px, calc(100% - 24px));
  padding: 24px 0 36px;
}

.diary-card {
  margin-bottom: 20px;
  background: white;
  border-radius: 18px;
  padding: 22px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.title {
  margin-bottom: 18px;
  font-size: 26px;
  font-weight: 600;
  color: #374151;
}

.section p {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 15px;
}

.emotion-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.emotion-card {
  width: 118px;
  padding: 15px 10px;
  border: 2px solid #e5e7eb;
  border-radius: 16px;
  text-align: center;
  cursor: pointer;
  background: #f9fafb;
}

.emotion-card img {
  width: 52px;
  height: 52px;
  object-fit: contain;
}

.emotion-name {
  margin-top: 10px;
  color: #374151;
}

.emotion-card.selected {
  border-color: #7ed321;
  background: #f0fdf4;
  transform: translateY(-3px);
}

.life-indicators {
  display: flex;
  gap: 20px;
  margin-top: 18px;
}

.indicator-group {
  flex: 1;
}

.form-label {
  margin: 10px 0;
  color: #374151;
  font-weight: 600;
}

.action-buttons {
  margin-top: 28px;
  display: flex;
  align-items: center;
  gap: 14px;
}

.save-tip {
  color: #6b7280;
  font-size: 13px;
}

.summary-box {
  padding: 16px;
  border-radius: 16px;
  background: #f8fafc;
  color: #475569;
  line-height: 1.8;
}

.summary-box p {
  margin: 0 0 10px;
}

@media (max-width: 768px) {
  .life-indicators,
  .action-buttons {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
