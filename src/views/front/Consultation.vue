<template>
  <div class="consultation-container">
    <aside class="sidebar">
      <div class="ai-assistant-info">
        <div class="breathing-circle">
          <img :src="robotIcon" alt="robot" class="assistant-avatar" />
        </div>
        <h3 class="assistant-name">暖光 AI 心理助手</h3>
        <div class="online-status">
          <span class="status-dot"></span>
          在线陪伴中
        </div>
      </div>

      <div class="session-history">
        <div class="history-header">
          <h3 class="section-title">最近会话</h3>
          <el-button size="small" type="warning" plain @click="createNewSession">新建会话</el-button>
        </div>
        <div v-if="loadingSessions" class="status-text">正在加载会话...</div>
        <div v-else-if="sessions.length" class="session-list">
          <div
            v-for="item in sessions"
            :key="item.id"
            class="session-item"
            :class="{ active: item.id === activeSessionId }"
          >
            <div class="session-info" @click="selectSession(item.id)">
              <div class="session-title">{{ item.title }}</div>
              <div class="session-preview">{{ item.preview }}</div>
              <div class="session-stats">
                <span>{{ item.time }}</span>
                <span>{{ item.messageCount }} 条消息</span>
              </div>
            </div>
            <button class="delete-btn" @click="removeSession(item.id)">删除</button>
          </div>
        </div>
        <div v-else class="status-text">还没有会话，开始说第一句话吧。</div>
      </div>

      <div class="emotion-garden">
        <div class="garden-header">
          <div class="garden-title">今日情绪建议</div>
        </div>
        <div class="emotion-info">
          <div class="emotion-name">陪伴</div>
          <div class="emotion-score">AI</div>
        </div>
        <div class="warm-tips">
          <div class="warm-suggestion">
            <div class="suggestion-content">
              <div class="suggestion-title">暖心提醒</div>
              <div class="suggestion-text">如果你暂时不知道从哪里说起，可以先描述“今天最让你有感觉的一件事”。</div>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <section class="chat-main">
      <div class="chat-header">
        <div class="header-left">
          <div class="chat-avatar">
            <img :src="robotIcon" alt="robot" class="chat-avatar-image" />
          </div>
          <div class="chat-info">
            <h2>{{ activeSessionTitle }}</h2>
            <p>一个可以慢慢说、不会催促你的 AI 对话空间</p>
          </div>
        </div>
      </div>

      <div ref="messagesContainer" class="chat-messages">
        <div v-if="!messages.length" class="empty-chat">
          从一句“我今天有点累”开始，也完全可以。
        </div>
        <div
          v-for="message in messages"
          :key="message.id"
          class="message-item"
          :class="message.role === 'ai' ? 'ai-message' : 'user-message'"
        >
          <div class="message-avatar">
            <img :src="message.role === 'ai' ? robotIcon : userAvatar" alt="avatar" class="message-avatar-image" />
          </div>
          <div class="message-content">
            <div class="message-bubble">
              {{ message.content }}
            </div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>
        <div v-if="sending" class="message-item ai-message">
          <div class="message-avatar">
            <img :src="robotIcon" alt="avatar" class="message-avatar-image" />
          </div>
          <div class="message-content">
            <div class="message-bubble typing-bubble">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <div class="input-container">
          <el-input
            v-model="draft"
            type="textarea"
            :rows="3"
            placeholder="把你现在最想说的一句话写下来。"
          />
          <div class="input-footer">
            <span>支持真实落库；未配置模型 Key 时使用本地兜底回复</span>
            <span>{{ draft.length }}/300</span>
          </div>
        </div>
        <el-button class="send-btn" type="warning" :loading="sending" @click="sendMessage">发送</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  deleteUserConsultationSession,
  fetchUserConsultationDetail,
  fetchUserConsultationSessions,
  sendUserConsultationMessage,
} from '@/api/admin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const robotIcon = new URL('@/assets/images/robot-fill.png', import.meta.url).href
const userAvatar = new URL('@/assets/images/user.jpg', import.meta.url).href

const sessions = ref([])
const activeSessionId = ref(null)
const activeSessionTitle = ref('新的咨询会话')
const messages = ref([])
const draft = ref('')
const loadingSessions = ref(false)
const sending = ref(false)
const messagesContainer = ref(null)

const ensureLogin = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再使用 AI 咨询')
    router.push('/login')
    return false
  }
  return true
}

const loadSessions = async () => {
  if (!ensureLogin()) return
  loadingSessions.value = true
  try {
    sessions.value = await fetchUserConsultationSessions()
    if (sessions.value.length && activeSessionId.value) {
      const stillExists = sessions.value.some((item) => item.id === activeSessionId.value)
      if (stillExists) {
        await selectSession(activeSessionId.value)
      } else {
        activeSessionId.value = null
        activeSessionTitle.value = '新的咨询会话'
        messages.value = []
      }
    } else if (sessions.value.length && !activeSessionId.value) {
      await selectSession(sessions.value[0].id)
    }
  } catch (error) {
    ElMessage.error(error.message || '会话加载失败')
  } finally {
    loadingSessions.value = false
  }
}

const selectSession = async (id) => {
  activeSessionId.value = id
  try {
    const detail = await fetchUserConsultationDetail(id)
    activeSessionTitle.value = detail.sessionTitle
    messages.value = detail.messages || []
    await scrollToBottom()
  } catch (error) {
    ElMessage.error(error.message || '会话详情获取失败')
  }
}

const createNewSession = () => {
  activeSessionId.value = null
  activeSessionTitle.value = '新的咨询会话'
  messages.value = []
  draft.value = ''
}

const removeSession = async (id) => {
  try {
    await deleteUserConsultationSession(id)
    ElMessage.success('会话已删除')
    if (activeSessionId.value === id) {
      createNewSession()
    }
    await loadSessions()
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

const sendMessage = async () => {
  if (!ensureLogin()) return
  if (!draft.value.trim()) return

  const pendingText = draft.value.trim()
  sending.value = true
  messages.value.push({
    id: Date.now(),
    role: 'user',
    content: pendingText,
    time: '刚刚',
  })
  draft.value = ''
  await scrollToBottom()

  try {
    const response = await sendUserConsultationMessage({
      sessionId: activeSessionId.value,
      content: pendingText,
    })

    activeSessionId.value = response.sessionId
    activeSessionTitle.value = response.sessionTitle
    await loadSessions()
    await selectSession(activeSessionId.value)
  } catch (error) {
    ElMessage.error(error.message || '发送失败')
    await loadSessions()
    if (activeSessionId.value) {
      await selectSession(activeSessionId.value)
    } else {
      createNewSession()
    }
  } finally {
    sending.value = false
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

onMounted(async () => {
  await loadSessions()
})
</script>

<style lang="scss" scoped>
.consultation-container {
  margin: 0 auto;
  width: min(1200px, calc(100% - 24px));
  display: flex;
  gap: 20px;
  padding: 24px 0 32px;
}

.sidebar {
  width: 320px;
}

.ai-assistant-info,
.session-history,
.emotion-garden {
  margin-bottom: 20px;
  border-radius: 20px;
  padding: 18px;
  box-shadow: 0 10px 32px rgba(0, 0, 0, 0.06);
}

.ai-assistant-info {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.92), rgba(255, 252, 248, 0.98));
}

.breathing-circle {
  width: 72px;
  height: 72px;
  margin: 0 auto 14px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%);
}

.assistant-avatar {
  width: 40px;
  height: 40px;
}

.assistant-name {
  margin: 0 0 8px;
  text-align: center;
  color: #9a3412;
}

.online-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #059669;
  font-size: 12px;
  font-weight: 700;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #059669;
}

.session-history {
  background: white;
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
  color: #333;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.session-item {
  padding: 12px;
  border-radius: 14px;
  border: 2px solid transparent;
  transition: 0.2s ease;
}

.session-item:hover,
.session-item.active {
  background: #f8f9ff;
  border-color: #dbeafe;
}

.session-info {
  cursor: pointer;
}

.delete-btn {
  margin-top: 8px;
  border: none;
  background: transparent;
  color: #ef4444;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
}

.session-title {
  font-weight: 600;
  color: #333;
}

.session-preview,
.session-stats,
.status-text {
  margin-top: 6px;
  font-size: 12px;
  color: #666;
}

.session-stats {
  display: flex;
  gap: 12px;
}

.emotion-garden {
  background: linear-gradient(135deg, #fef9e7 0%, #fcf4e6 50%, #f6f0e8 100%);
}

.emotion-info {
  margin: 0 auto 16px;
  width: 86px;
  height: 86px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #fff;
}

.emotion-name {
  font-size: 16px;
  font-weight: 700;
}

.emotion-score {
  font-size: 15px;
  font-weight: 700;
}

.warm-suggestion {
  background: rgba(255, 255, 255, 0.88);
  border-radius: 16px;
  padding: 14px;
}

.suggestion-title {
  font-size: 14px;
  font-weight: 700;
  color: #8b7355;
  margin-bottom: 6px;
}

.suggestion-text {
  color: #6b5b47;
  line-height: 1.6;
  font-size: 13px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.96) 0%, rgba(255, 252, 250, 0.98) 100%);
  border: 1px solid rgba(251, 146, 60, 0.1);
  box-shadow: 0 12px 40px rgba(251, 146, 60, 0.08), 0 4px 16px rgba(0, 0, 0, 0.04);
}

.chat-header {
  padding: 20px 24px;
  color: white;
  background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%);
}

.header-left {
  display: flex;
  align-items: center;
}

.chat-avatar {
  width: 50px;
  height: 50px;
  margin-right: 16px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-avatar-image {
  width: 28px;
  height: 28px;
}

.chat-info h2,
.chat-info p {
  margin: 0;
}

.chat-info p {
  margin-top: 6px;
  font-size: 14px;
  opacity: 0.92;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
}

.empty-chat {
  color: #6b7280;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.message-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.message-avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  max-width: 72%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(251, 146, 60, 0.1);
  white-space: pre-wrap;
}

.typing-bubble {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
  animation: typingPulse 1.2s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.3s;
}

.message-time {
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid rgba(251, 146, 60, 0.1);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.5), rgba(255, 252, 248, 0.7));
}

.input-container {
  flex: 1;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  color: #78716c;
  font-size: 12px;
}

.send-btn {
  width: 72px;
  height: 72px;
  border-radius: 18px;
}

@keyframes typingPulse {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

@media (max-width: 960px) {
  .consultation-container {
    flex-direction: column;
  }

  .sidebar {
    width: auto;
  }
}

@media (max-width: 640px) {
  .consultation-container {
    width: 100%;
    gap: 10px;
    padding: 10px 10px 18px;
  }

  .ai-assistant-info,
  .emotion-garden {
    display: none;
  }

  .session-history {
    margin-bottom: 0;
    padding: 14px;
    border-radius: 16px;
  }

  .history-header {
    margin-bottom: 10px;
  }

  .section-title {
    font-size: 17px;
  }

  .session-list {
    max-height: 154px;
    overflow-y: auto;
    padding-right: 2px;
  }

  .session-item {
    padding: 10px;
    border-radius: 12px;
  }

  .session-preview {
    display: -webkit-box;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
  }

  .chat-main {
    min-height: 68vh;
    border-radius: 18px;
  }

  .chat-header {
    padding: 14px 16px;
  }

  .chat-avatar {
    width: 42px;
    height: 42px;
    margin-right: 10px;
  }

  .chat-info h2 {
    font-size: 19px;
  }

  .chat-info p {
    font-size: 12px;
    line-height: 1.5;
  }

  .chat-messages {
    min-height: 360px;
    padding: 16px 12px;
    gap: 14px;
  }

  .message-item {
    gap: 8px;
  }

  .message-avatar {
    width: 30px;
    height: 30px;
  }

  .message-content {
    max-width: 84%;
  }

  .message-bubble {
    padding: 10px 12px;
    border-radius: 14px;
    line-height: 1.65;
  }

  .chat-input {
    position: sticky;
    bottom: 0;
    flex-direction: column;
    gap: 10px;
    padding: 12px;
    background: rgba(255, 252, 248, 0.96);
  }

  .input-footer {
    gap: 8px;
    align-items: flex-start;
    line-height: 1.45;
  }

  .send-btn {
    width: 100%;
    height: 46px;
    border-radius: 14px;
  }
}
</style>
