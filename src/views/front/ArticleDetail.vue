<template>
  <div class="articleDetail-container">
    <div class="header-section">
      <div class="header-content">
        <img :src="bookIcon" alt="book" class="header-icon" />
        <div>
          <h1>{{ article?.title || '文章详情' }}</h1>
          <p>理解情绪、理解自己，建立稳定的自我支持系统。</p>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="detail-actions">
        <button class="back-button" @click="goBack">← 返回知识库</button>
      </div>
      <div v-if="loading" class="status-card">正在加载文章详情...</div>
      <div v-else-if="article" class="diary-card">
        <div class="sub-title">
          <el-tag class="category-tag" type="warning" round>{{ article.category }}</el-tag>
          <div class="flex-box">
            <div class="item">阅读 {{ article.readCount }}</div>
            <div class="item">更新于 {{ article.updatedAt }}</div>
          </div>
        </div>
        <h2 class="article-title">{{ article.title }}</h2>
        <div class="summary-content">{{ article.summary }}</div>
        <div class="content-wrapper" v-html="formatContent(article.content)"></div>
        <div class="tags-content">
          <div class="tags-title">主题标签</div>
          <div class="tags-list">
            <el-tag v-for="tag in article.tags || []" :key="tag" effect="plain" round>{{ tag }}</el-tag>
          </div>
        </div>
      </div>
      <el-empty v-else description="文章不存在或暂时无法访问" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchArticleDetail } from '@/api/admin'
import { knowledgeArticles } from '@/mock/adminData'
import { formatContent } from '@/utils/psychology'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const loading = ref(false)
const bookIcon = new URL('@/assets/images/book.png', import.meta.url).href

const goBack = () => {
  router.push('/knowledge')
}

onMounted(async () => {
  loading.value = true
  try {
    article.value = await fetchArticleDetail(route.params.id)
  } catch (error) {
    article.value = knowledgeArticles.find((item) => String(item.id) === String(route.params.id)) || null
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.articleDetail-container {
  background: linear-gradient(135deg, #fafbfc 0%, #f7f9fc 50%, #f2f6fa 100%);
  min-height: calc(100vh - 140px);
}

.header-section {
  background: linear-gradient(135deg, #f59e0b 0%, #8b5cf6 100%);
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
  width: min(980px, calc(100% - 32px));
  padding: 24px 0 36px;
}

.detail-actions {
  margin-bottom: 16px;
}

.back-button {
  border: none;
  background: transparent;
  color: #374151;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
}

.diary-card {
  background: white;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.status-card {
  padding: 28px 24px;
  border-radius: 18px;
  background: white;
  color: #6b7280;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.sub-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.flex-box {
  display: flex;
  align-items: center;
  gap: 18px;
  color: #6b7280;
  font-size: 14px;
}

.article-title {
  font-size: 32px;
  color: #111827;
  margin: 26px 0 14px;
}

.summary-content {
  background: rgba(126, 211, 33, 0.1);
  border-left: 4px solid #7ed321;
  padding: 12px 16px;
  border-radius: 0 8px 8px 0;
}

.content-wrapper {
  margin-top: 24px;
  font-size: 15px;
  color: #374151;
  line-height: 1.9;
}

.content-wrapper :deep(h3) {
  margin: 18px 0 10px;
  color: #111827;
}

.tags-content {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.tags-title {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
