<template>
  <div class="records-page">
    <div class="records-shell">
      <div class="records-header">
        <div>
          <span class="eyebrow">My Records</span>
          <h1>我的记录</h1>
          <p>集中查看你的情绪日记和 AI 咨询历史，感受自己的变化轨迹。</p>
        </div>
      </div>

      <div class="records-grid">
        <section class="records-panel">
          <div class="panel-head">
            <h2>情绪日记</h2>
            <RouterLink to="/emotion-diary">继续记录</RouterLink>
          </div>
          <div v-if="loading" class="loading-text">正在加载记录...</div>
          <div v-else-if="emotionLogs.length" class="record-list">
            <article v-for="item in emotionLogs" :key="item.id" class="record-card">
              <div class="record-top">
                <strong>{{ item.emotion }}</strong>
                <span>{{ item.createdAt }}</span>
              </div>
              <p>{{ item.content }}</p>
              <div class="tag-row">
                <el-tag size="small" effect="plain">睡眠 {{ item.sleepQuality }}</el-tag>
                <el-tag size="small" effect="plain">压力 {{ item.stressLevel }}</el-tag>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂无情绪日记" :image-size="100" />
        </section>

        <section class="records-panel">
          <div class="panel-head">
            <h2>咨询历史</h2>
            <RouterLink to="/consultation">继续咨询</RouterLink>
          </div>
          <div v-if="loading" class="loading-text">正在加载会话...</div>
          <div v-else-if="sessions.length" class="record-list">
            <article v-for="item in sessions" :key="item.id" class="record-card">
              <div class="record-top">
                <strong>{{ item.title }}</strong>
                <span>{{ item.time }}</span>
              </div>
              <p>{{ item.preview }}</p>
              <div class="tag-row">
                <el-tag size="small" effect="plain">{{ item.messageCount }} 条消息</el-tag>
              </div>
            </article>
          </div>
          <el-empty v-else description="暂无咨询历史" :image-size="100" />
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { fetchCurrentUserEmotions, fetchUserConsultationSessions } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const emotionLogs = ref([])
const sessions = ref([])
let refreshTimer = null

const loadData = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后查看个人记录')
    router.push('/login')
    return
  }

  loading.value = true
  try {
    const [emotionData, sessionData] = await Promise.all([
      fetchCurrentUserEmotions(),
      fetchUserConsultationSessions(),
    ])
    emotionLogs.value = emotionData || []
    sessions.value = sessionData || []
  } catch (error) {
    ElMessage.error(error.message || '记录加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadData()
  refreshTimer = window.setInterval(() => {
    loadData()
  }, 10000)
})

onBeforeUnmount(() => {
  if (refreshTimer) {
    window.clearInterval(refreshTimer)
  }
})
</script>

<style lang="scss" scoped>
.records-page {
  min-height: calc(100vh - 140px);
  padding: 32px 0 40px;
}

.records-shell {
  width: min(1200px, calc(100% - 24px));
  margin: 0 auto;
}

.records-header {
  margin-bottom: 24px;
}

.eyebrow {
  color: #ea580c;
  font-size: 13px;
  font-weight: 700;
}

.records-header h1 {
  margin: 10px 0 8px;
  font-size: 40px;
  color: #1f2937;
}

.records-header p {
  margin: 0;
  color: #64748b;
  line-height: 1.8;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.records-panel {
  padding: 22px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.84);
  box-shadow: 0 14px 44px rgba(79, 95, 122, 0.08);
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0;
  color: #243042;
}

.panel-head a {
  color: #ea580c;
  font-weight: 700;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.record-card {
  padding: 16px;
  border-radius: 18px;
  background: #f8fafc;
}

.record-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.record-top strong {
  color: #1f2937;
}

.record-top span {
  color: #94a3b8;
  font-size: 12px;
}

.record-card p {
  margin: 12px 0;
  color: #475569;
  line-height: 1.7;
}

.tag-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.loading-text {
  color: #6b7280;
}

@media (max-width: 960px) {
  .records-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .records-page {
    padding: 20px 0 28px;
  }

  .records-shell {
    width: calc(100% - 20px);
  }

  .records-header {
    margin-bottom: 18px;
  }

  .records-header h1 {
    font-size: 30px;
  }

  .records-header p {
    font-size: 14px;
    line-height: 1.7;
  }

  .records-grid {
    gap: 14px;
  }

  .records-panel {
    padding: 16px;
    border-radius: 18px;
  }

  .panel-head {
    margin-bottom: 14px;
  }

  .panel-head h2 {
    font-size: 20px;
  }

  .record-list {
    gap: 10px;
  }

  .record-card {
    padding: 14px;
    border-radius: 14px;
  }

  .record-top {
    align-items: flex-start;
    flex-direction: column;
    gap: 4px;
  }

  .record-card p {
    margin: 10px 0;
    font-size: 14px;
    line-height: 1.65;
  }
}

@media (max-width: 640px) {
  .records-page {
    background:
      radial-gradient(circle at 12% 0%, rgba(20, 184, 166, 0.16), transparent 30%),
      linear-gradient(180deg, #f8fafc 0%, #f0fdfa 100%);
  }

  .records-header {
    padding: 18px;
    border-radius: 24px;
    background: linear-gradient(135deg, rgba(15, 118, 110, 0.94), rgba(20, 184, 166, 0.76));
    color: white;
    box-shadow: 0 18px 38px rgba(15, 118, 110, 0.18);
  }

  .records-header h1,
  .records-header p,
  .eyebrow {
    color: white;
  }

  .records-header p {
    opacity: 0.9;
  }

  .records-panel {
    border: 1px solid rgba(255, 255, 255, 0.86);
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
  }

  .record-card {
    background: #f8fafc;
    border: 1px solid rgba(226, 232, 240, 0.7);
  }
}

@media (max-width: 640px) {
  .records-page {
    background:
      radial-gradient(circle at 88% 0%, rgba(20, 184, 166, 0.14), transparent 30%),
      linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  }

  .records-header {
    padding: 16px;
    border-radius: 24px;
    background: linear-gradient(135deg, rgba(15, 118, 110, 0.94), rgba(20, 184, 166, 0.78));
    box-shadow: 0 16px 36px rgba(15, 118, 110, 0.16);
  }

  .records-header h1,
  .records-header p,
  .eyebrow {
    color: white;
  }

  .records-header p {
    opacity: 0.9;
  }

  .records-panel {
    border: 1px solid rgba(226, 232, 240, 0.72);
    background: rgba(255, 255, 255, 0.94);
  }

  .record-card {
    background: linear-gradient(180deg, #ffffff, #f8fafc);
    box-shadow: 0 8px 22px rgba(15, 23, 42, 0.05);
  }
}

@media (max-width: 640px) {
  .records-page {
    background:
      radial-gradient(circle at 90% 4%, rgba(14, 165, 233, 0.12), transparent 32%),
      linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  }

  .records-header {
    padding: 18px;
    border-radius: 24px;
    background: linear-gradient(135deg, rgba(15, 118, 110, 0.94), rgba(20, 184, 166, 0.72));
    box-shadow: 0 16px 36px rgba(15, 118, 110, 0.16);
  }

  .records-header h1,
  .records-header p,
  .eyebrow {
    color: white;
  }

  .records-header p {
    opacity: 0.9;
  }

  .records-panel {
    border: 1px solid rgba(226, 232, 240, 0.76);
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
  }

  .record-card {
    background: linear-gradient(180deg, #ffffff, #f8fafc);
    border: 1px solid #eef2f7;
  }
}
</style>
