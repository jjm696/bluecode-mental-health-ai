<template>
  <div>
    <PageHead
      title="咨询记录"
      description="查看用户与 AI 心理助手的会话记录、风险判断和建议摘要。"
    />

    <TableSearch :form-item="formItem" @search="handleSearch" @reset="handleReset" />

    <el-card shadow="never" class="table-card">
      <el-table v-loading="tableLoading" :data="sessionList" stripe empty-text="暂无咨询记录">
        <el-table-column prop="sessionTitle" label="会话主题" min-width="240">
          <template #default="{ row }">
            <div>
              <div class="session-title">{{ row.sessionTitle }}</div>
              <div class="session-preview">{{ row.preview }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="dominantEmotion" label="主要情绪" width="120">
          <template #default="{ row }">
            <el-tag :type="getAiEmotionTagType(row.dominantEmotion)" round>
              {{ row.dominantEmotion }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelTagType(row.riskLevel)" round>
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageCount" label="消息数" width="90" />
        <el-table-column prop="updatedAt" label="最近互动" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-info">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :current-page="pagination.page"
          :page-sizes="[5, 10, 20]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="会话详情" size="680px">
      <template v-if="activeSession">
        <div class="session-detail">
          <div class="detail-header">
            <div class="detail-row">
              <span class="detail-label">用户</span>
              <span class="detail-value">{{ activeSession.userName }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">风险等级</span>
              <span class="detail-value">
                <el-tag :type="getRiskLevelTagType(activeSession.riskLevel)" round>
                  {{ getRiskLevelText(activeSession.riskLevel) }}
                </el-tag>
              </span>
            </div>
            <div class="detail-row">
              <span class="detail-label">AI 建议</span>
              <span class="detail-value">{{ activeSession.summary }}</span>
            </div>
          </div>

          <div class="messages-container">
            <div class="messages-header">
              <h4>消息记录</h4>
            </div>
            <div class="messages-list">
              <div
                v-for="message in activeSession.messages"
                :key="message.id"
                class="message-item"
                :class="message.role === 'user' ? 'user-message' : 'ai-message'"
              >
                <div class="message-header">
                  <span class="sender">
                    <el-icon><component :is="message.role === 'user' ? 'User' : 'Service'" /></el-icon>
                    {{ message.role === 'user' ? activeSession.userName : 'AI 助手' }}
                  </span>
                  <span class="time">{{ message.time }}</span>
                </div>
                <div class="message-content">{{ message.content }}</div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import PageHead from '@/components/PageHead.vue'
import TableSearch from '@/components/TableSearch.vue'
import { consultationSessions } from '@/mock/adminData'
import { fetchConsultationDetail, fetchConsultations } from '@/api/admin'
import { getAiEmotionTagType, getRiskLevelTagType, getRiskLevelText } from '@/utils/psychology'

const filters = ref({
  userName: '',
  riskLevel: '',
  dominantEmotion: '',
})

const sessionList = ref(consultationSessions)
const tableLoading = ref(false)
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: consultationSessions.length,
})
const detailVisible = ref(false)
const activeSession = ref(null)
let refreshTimer = null

const formItem = [
  { comp: 'input', prop: 'userName', label: '用户名', placeholder: '请输入用户名' },
  {
    comp: 'select',
    prop: 'riskLevel',
    label: '风险等级',
    placeholder: '请选择风险等级',
    options: [
      { label: '正常', value: '0' },
      { label: '关注', value: '1' },
      { label: '预警', value: '2' },
      { label: '危机', value: '3' },
    ],
  },
  {
    comp: 'select',
    prop: 'dominantEmotion',
    label: '主要情绪',
    placeholder: '请选择情绪',
    options: [
      { label: '平静', value: '平静' },
      { label: '焦虑', value: '焦虑' },
      { label: '沮丧', value: '沮丧' },
      { label: '压力', value: '压力' },
    ],
  },
]

const loadSessions = async (formData = filters.value) => {
  tableLoading.value = true
  try {
    const data = await fetchConsultations({
      ...formData,
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    sessionList.value = data.list
    pagination.total = data.total
  } catch (error) {
    sessionList.value = consultationSessions
    pagination.total = consultationSessions.length
  } finally {
    tableLoading.value = false
  }
}

const handleSearch = async (formData) => {
  filters.value = formData
  pagination.page = 1
  await loadSessions(formData)
}

const handleReset = (formData) => {
  filters.value = formData
}

const openDetail = async (session) => {
  try {
    activeSession.value = await fetchConsultationDetail(session.id)
  } catch (error) {
    activeSession.value = session
  }
  detailVisible.value = true
}

const handlePageChange = async (page) => {
  pagination.page = page
  await loadSessions()
}

const handleSizeChange = async (pageSize) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  await loadSessions()
}

onMounted(async () => {
  await loadSessions()
  refreshTimer = window.setInterval(() => {
    loadSessions()
  }, 10000)
})

onBeforeUnmount(() => {
  if (refreshTimer) {
    window.clearInterval(refreshTimer)
  }
})
</script>

<style lang="scss" scoped>
.table-card {
  border-radius: 18px;
}

.session-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.session-preview {
  font-size: 13px;
  color: #666;
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
}

.session-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  min-width: 80px;
  margin-right: 8px;
  font-weight: 500;
  color: #495057;
}

.detail-value {
  color: #333;
}

.messages-header h4 {
  margin: 0 0 16px;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.messages-list {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  background: #fff;
}

.message-item {
  margin-bottom: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.message-item:last-child {
  margin-bottom: 0;
}

.user-message {
  background: #e8f4fd;
}

.ai-message {
  background: #f0f9f0;
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.sender {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.message-content {
  margin-top: 8px;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  font-size: 14px;
}
</style>
