<template>
  <div>
    <PageHead
      title="情绪日志"
      description="查看用户情绪日记、风险分层和 AI 分析结果，支持快速人工复核。"
    />

    <TableSearch :form-item="formItem" @search="handleSearch" @reset="handleReset" />

    <el-card shadow="never" class="table-card">
      <el-table v-loading="tableLoading" :data="logList" stripe empty-text="暂无情绪日志">
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="emotion" label="当前情绪" width="120">
          <template #default="{ row }">
            <div class="emotion-cell">
              <img :src="getEmotionImage(row.emotion)" :alt="row.emotion" class="emotion-icon" />
              <el-tag :type="getEmotionTagType(row.emotion)" round>
                {{ row.emotion }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="moodScore" label="情绪评分" width="120">
          <template #default="{ row }">
            <span :style="{ color: getEmotionScoreColor(row.moodScore), fontWeight: 700 }">
              {{ row.moodScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getRiskLevelTagType(row.riskLevel)" round>
              {{ getRiskLevelText(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="keywords" label="AI 关键词" min-width="220">
          <template #default="{ row }">
            <div class="tag-list">
              <el-tag v-for="tag in row.aiAnalysis.keywords" :key="tag" effect="plain" round>
                {{ tag }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="记录时间" width="170" />
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

    <el-dialog v-model="detailVisible" title="日志详情" width="760px">
      <template v-if="activeLog">
        <div class="detail-content">
          <div class="detail-section">
            <h4><el-icon><Document /></el-icon>基础信息</h4>
            <div class="emotion-preview">
              <img :src="getEmotionImage(activeLog.emotion)" :alt="activeLog.emotion" class="emotion-preview-image" />
              <div>
                <div class="emotion-preview-name">{{ activeLog.emotion }}</div>
                <div class="emotion-preview-text">记录时刻的核心情绪状态</div>
              </div>
            </div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="用户">{{ activeLog.userName }}</el-descriptions-item>
              <el-descriptions-item label="情绪">{{ activeLog.emotion }}</el-descriptions-item>
              <el-descriptions-item label="情绪评分">{{ activeLog.moodScore }}</el-descriptions-item>
              <el-descriptions-item label="风险等级">
                {{ getRiskLevelText(activeLog.riskLevel) }}
              </el-descriptions-item>
              <el-descriptions-item label="睡眠质量">{{ activeLog.sleepQuality }}</el-descriptions-item>
              <el-descriptions-item label="压力指数">{{ activeLog.stressLevel }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="detail-section">
            <h4><el-icon><EditPen /></el-icon>日记内容</h4>
            <el-card shadow="never">{{ activeLog.content }}</el-card>
          </div>

          <div class="detail-section ai-analysis-result">
            <h4><el-icon><MagicStick /></el-icon>AI 分析</h4>
            <div class="ai-keywords-section">
              <h5><el-icon><CollectionTag /></el-icon>关键词</h5>
              <div class="keywords-container">
                <el-tag
                  v-for="tag in activeLog.aiAnalysis.keywords"
                  :key="tag"
                  class="keyword-tag"
                  round
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <div class="ai-suggestion-section">
              <h5><el-icon><ChatLineRound /></el-icon>建议摘要</h5>
              <div class="suggestion-content">{{ activeLog.aiAnalysis.suggestion }}</div>
            </div>

            <div class="ai-risk-section">
              <h5><el-icon><Warning /></el-icon>风险提示</h5>
              <div class="risk-content">{{ activeLog.aiAnalysis.riskNotice }}</div>
            </div>

            <div class="ai-improvements-section">
              <h5><el-icon><Opportunity /></el-icon>改善建议</h5>
              <ul class="improvement-list">
                <li v-for="item in activeLog.aiAnalysis.improvements" :key="item">{{ item }}</li>
              </ul>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import PageHead from '@/components/PageHead.vue'
import TableSearch from '@/components/TableSearch.vue'
import { fetchEmotions } from '@/api/admin'
import { emotionLogs } from '@/mock/adminData'
import {
  getEmotionScoreColor,
  getEmotionTagType,
  getRiskLevelTagType,
  getRiskLevelText,
} from '@/utils/psychology'
import { getEmotionImage } from '@/utils/assets'

const filters = ref({
  userName: '',
  emotion: '',
  riskLevel: '',
})

const logList = ref(emotionLogs)
const tableLoading = ref(false)
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: emotionLogs.length,
})
const detailVisible = ref(false)
const activeLog = ref(null)
let refreshTimer = null

const formItem = [
  { comp: 'input', prop: 'userName', label: '用户名', placeholder: '请输入用户名' },
  {
    comp: 'select',
    prop: 'emotion',
    label: '情绪状态',
    placeholder: '请选择情绪',
    options: [
      { label: '快乐', value: '快乐' },
      { label: '平静', value: '平静' },
      { label: '焦虑', value: '焦虑' },
      { label: '悲伤', value: '悲伤' },
    ],
  },
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
]

const loadLogs = async (formData = filters.value) => {
  tableLoading.value = true
  try {
    const data = await fetchEmotions({
      ...formData,
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    logList.value = data.list
    pagination.total = data.total
  } catch (error) {
    logList.value = emotionLogs
    pagination.total = emotionLogs.length
  } finally {
    tableLoading.value = false
  }
}

const handleSearch = async (formData) => {
  filters.value = formData
  pagination.page = 1
  await loadLogs(formData)
}

const handleReset = (formData) => {
  filters.value = formData
}

const openDetail = (log) => {
  activeLog.value = log
  detailVisible.value = true
}

const handlePageChange = async (page) => {
  pagination.page = page
  await loadLogs()
}

const handleSizeChange = async (pageSize) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  await loadLogs()
}

onMounted(async () => {
  await loadLogs()
  refreshTimer = window.setInterval(() => {
    loadLogs()
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

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.emotion-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.emotion-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.emotion-preview {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
  padding: 12px 14px;
  border-radius: 16px;
  background: #f8fafc;
}

.emotion-preview-image {
  width: 52px;
  height: 52px;
  object-fit: contain;
}

.emotion-preview-name {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.emotion-preview-text {
  margin-top: 4px;
  font-size: 13px;
  color: #6b7280;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  color: #303133;
  font-size: 16px;
}

.ai-keywords-section,
.ai-suggestion-section,
.ai-risk-section,
.ai-improvements-section {
  margin-top: 16px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.ai-keywords-section h5,
.ai-suggestion-section h5,
.ai-risk-section h5,
.ai-improvements-section h5 {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 8px;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
}

.keywords-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.keyword-tag {
  background-color: #e1f3d8;
  color: #67c23a;
  border-color: #b3d8a4;
}

.suggestion-content,
.risk-content {
  line-height: 1.6;
  color: #606266;
  background-color: white;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.improvement-list {
  margin: 0;
  padding-left: 20px;
}

.improvement-list li {
  margin-bottom: 4px;
  color: #606266;
  line-height: 1.5;
}
</style>
