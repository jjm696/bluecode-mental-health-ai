<template>
  <div class="dashboard-container">
    <PageHead
      title="数据分析"
      description="查看平台用户情绪、咨询活跃度与知识库使用情况的概览数据。"
    />

    <el-row :gutter="20" class="stats-row">
      <el-col v-for="item in statCards" :key="item.key" :xs="24" :sm="12" :xl="6">
        <el-card shadow="hover" class="stat-card">
          <div class="card-content">
            <div class="avatar" :class="item.key">
              <img :src="item.icon" :alt="item.title" class="avatar-image" />
            </div>
            <div class="info">
              <div class="title">{{ item.title }}</div>
              <div class="value">{{ item.value }}</div>
              <div class="subtitle-title">{{ item.subtitle }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>近 7 天情绪趋势</span>
              <el-tag type="success" round>持续回暖</el-tag>
            </div>
          </template>
          <div class="trend-list">
            <div v-for="item in overviewData.emotionTrend" :key="item.date" class="trend-item">
              <div>
                <div class="trend-date">{{ item.date }}</div>
                <div class="trend-meta">记录数 {{ item.recordCount }}</div>
              </div>
              <div class="trend-score">
                <el-progress
                  :percentage="item.avgMoodScore"
                  :color="getEmotionScoreColor(item.avgMoodScore)"
                  :stroke-width="12"
                  :show-text="false"
                />
                <span>{{ item.avgMoodScore }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>风险关注分布</span>
            </div>
          </template>
          <div class="risk-list">
            <div v-for="item in overviewData.riskDistribution" :key="item.level" class="risk-item">
              <div class="risk-left">
                <el-tag :type="getRiskLevelTagType(item.level)" round>
                  {{ getRiskLevelText(item.level) }}
                </el-tag>
                <span>{{ item.count }} 人</span>
              </div>
              <el-progress :percentage="item.percent" :show-text="false" :stroke-width="10" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>咨询活动统计</span>
            </div>
          </template>
          <div class="activity-grid">
            <div v-for="item in overviewData.consultationStats" :key="item.label" class="activity-card">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
              <p>{{ item.hint }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>高频情绪标签</span>
            </div>
          </template>
          <div class="tag-cloud">
            <el-tag
              v-for="item in overviewData.topEmotions"
              :key="item.name"
              :type="getEmotionTagType(item.name)"
              effect="light"
              round
            >
              {{ item.name }} · {{ item.count }}
            </el-tag>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import PageHead from '@/components/PageHead.vue'
import { dashboardMetrics } from '@/mock/adminData'
import { fetchDashboardOverview } from '@/api/admin'
import { getEmotionScoreColor, getEmotionTagType, getRiskLevelTagType, getRiskLevelText } from '@/utils/psychology'
import { dashboardIconMap } from '@/utils/assets'

const overviewData = ref(dashboardMetrics)

const buildStatCards = () => [
  {
    key: 'users',
    title: '累计用户',
    value: overviewData.value.overview.totalUsers,
    subtitle: '注册用户总量',
    icon: dashboardIconMap.users,
  },
  {
    key: 'like',
    title: '知识文章',
    value: overviewData.value.overview.totalArticles,
    subtitle: '知识库内容总量',
    icon: dashboardIconMap.like,
  },
  {
    key: 'comments',
    title: '咨询会话',
    value: overviewData.value.overview.totalConsultations,
    subtitle: '累计 AI 咨询会话',
    icon: dashboardIconMap.comments,
  },
  {
    key: 'smile',
    title: '情绪日志',
    value: overviewData.value.overview.totalEmotionLogs,
    subtitle: '累计情绪记录',
    icon: dashboardIconMap.smile,
  },
]

const statCards = ref(buildStatCards())
let refreshTimer = null

const normalizeRiskDistribution = (list = []) => {
  const total = list.reduce((sum, item) => sum + Number(item.count || 0), 0) || 1
  return list.map((item) => ({
    ...item,
    percent: Math.round((Number(item.count || 0) / total) * 100),
  }))
}

onMounted(async () => {
  try {
    const data = await fetchDashboardOverview()
    overviewData.value = {
      ...dashboardMetrics,
      ...data,
      riskDistribution: normalizeRiskDistribution(data.riskDistribution),
    }
    statCards.value = buildStatCards()
  } catch (error) {
    overviewData.value = {
      ...dashboardMetrics,
      riskDistribution: normalizeRiskDistribution(dashboardMetrics.riskDistribution),
    }
    statCards.value = buildStatCards()
  }

  refreshTimer = window.setInterval(async () => {
    try {
      const data = await fetchDashboardOverview()
      overviewData.value = {
        ...dashboardMetrics,
        ...data,
        riskDistribution: normalizeRiskDistribution(data.riskDistribution),
      }
      statCards.value = buildStatCards()
    } catch (error) {
      // keep current data on polling failure
    }
  }, 15000)
})

onBeforeUnmount(() => {
  if (refreshTimer) {
    window.clearInterval(refreshTimer)
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-row {
  margin-bottom: 0;
}

.stat-card,
.panel-card {
  border-radius: 18px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 700;
  color: #1f2937;
}

.card-content {
  display: flex;
  align-items: center;
}

.avatar {
  width: 60px;
  height: 60px;
  margin-right: 14px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;

  &.users {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  &.like {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  &.comments {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  &.smile {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}

.avatar-image {
  width: 30px;
  height: 30px;
  object-fit: contain;
}

.info .title {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 4px;
}

.info .value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.info .subtitle-title {
  font-size: 12px;
  color: #95a5a6;
}

.trend-list,
.risk-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.trend-item,
.risk-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.trend-date {
  font-weight: 600;
  color: #1f2937;
}

.trend-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.trend-score {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 220px;
}

.trend-score :deep(.el-progress) {
  flex: 1;
}

.risk-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 120px;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.activity-card {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(135deg, #f8fafc, #eff6ff);
}

.activity-card span,
.activity-card p {
  color: #6b7280;
}

.activity-card strong {
  display: block;
  margin: 8px 0 6px;
  font-size: 28px;
  color: #1f2937;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

@media (max-width: 1200px) {
  .trend-score {
    min-width: 180px;
  }
}

@media (max-width: 768px) {
  .activity-grid {
    grid-template-columns: 1fr;
  }

  .trend-item,
  .risk-item {
    align-items: flex-start;
    flex-direction: column;
  }

  .trend-score,
  .risk-left {
    min-width: 100%;
  }
}
</style>
