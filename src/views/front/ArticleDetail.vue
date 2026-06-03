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

@media (max-width: 640px) {
  .header-section {
    padding: 28px 14px;
  }

  .header-content {
    align-items: flex-start;
    gap: 12px;
  }

  .header-icon {
    width: 42px;
    height: 42px;
    flex-shrink: 0;
  }

  .header-content h1 {
    margin: 0;
    font-size: 25px;
    line-height: 1.35;
  }

  .header-content p {
    margin: 6px 0 0;
    font-size: 14px;
    line-height: 1.65;
  }

  .content {
    width: calc(100% - 20px);
    padding: 14px 0 26px;
  }

  .detail-actions {
    margin-bottom: 12px;
  }

  .back-button {
    font-size: 14px;
  }

  .diary-card,
  .status-card {
    padding: 16px;
    border-radius: 16px;
  }

  .sub-title {
    align-items: flex-start;
    flex-direction: column;
  }

  .flex-box {
    align-items: flex-start;
    flex-direction: column;
    gap: 4px;
    font-size: 13px;
  }

  .article-title {
    margin: 18px 0 12px;
    font-size: 24px;
    line-height: 1.35;
  }

  .summary-content {
    padding: 12px;
    font-size: 14px;
    line-height: 1.7;
  }

  .content-wrapper {
    margin-top: 18px;
    font-size: 14px;
    line-height: 1.85;
  }
}

@media (max-width: 640px) {
  .articleDetail-container {
    background:
      radial-gradient(circle at 86% 0%, rgba(245, 158, 11, 0.14), transparent 30%),
      linear-gradient(180deg, #fff7ed 0%, #f8fafc 44%, #ffffff 100%);
  }

  .header-section {
    margin: 10px 10px 0;
    border-radius: 26px;
    background: linear-gradient(135deg, #f59e0b 0%, #0f766e 100%);
    box-shadow: 0 18px 40px rgba(245, 158, 11, 0.16);
  }

  .diary-card,
  .status-card {
    border: 1px solid rgba(226, 232, 240, 0.72);
    background: rgba(255, 255, 255, 0.96);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  }

  .back-button {
    display: inline-flex;
    padding: 9px 12px;
    border-radius: 999px;
    background: #ecfdf5;
    color: #0f766e;
  }

  .content-wrapper {
    color: #334155;
  }
}

@media (max-width: 640px) {
  .articleDetail-container {
    background:
      radial-gradient(circle at 88% 0%, rgba(245, 158, 11, 0.14), transparent 30%),
      linear-gradient(180deg, #fffaf0 0%, #ffffff 100%);
  }

  .header-section {
    margin: 10px 10px 0;
    border-radius: 26px;
    background: linear-gradient(135deg, #f59e0b 0%, #0f766e 100%);
    box-shadow: 0 16px 36px rgba(245, 158, 11, 0.16);
  }

  .diary-card,
  .status-card {
    border: 1px solid rgba(226, 232, 240, 0.72);
    background: rgba(255, 255, 255, 0.96);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
  }

  .back-button {
    padding: 10px 12px;
    border-radius: 999px;
    background: white;
    box-shadow: 0 8px 18px rgba(15, 23, 42, 0.06);
  }
}

@media (max-width: 640px) {
  .articleDetail-container {
    background:
      radial-gradient(circle at 86% 0%, rgba(245, 158, 11, 0.14), transparent 30%),
      linear-gradient(180deg, #fffaf0 0%, #f8fafc 46%, #ffffff 100%);
  }

  .header-section {
    margin: 10px 10px 0;
    border-radius: 26px;
    background: linear-gradient(135deg, #f59e0b 0%, #0f766e 100%);
    box-shadow: 0 16px 36px rgba(245, 158, 11, 0.16);
  }

  .diary-card,
  .status-card {
    border: 1px solid rgba(226, 232, 240, 0.76);
    background: rgba(255, 255, 255, 0.96);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
  }

  .summary-content {
    background: #ecfdf5;
    border-left-color: #0f766e;
  }
}

@media (max-width: 640px) {
  .articleDetail-container {
    background:
      radial-gradient(circle at 85% 0%, rgba(245, 158, 11, 0.14), transparent 30%),
      linear-gradient(180deg, #fffaf0 0%, #f8fafc 48%, #ffffff 100%);
  }

  .header-section {
    margin: 10px 10px 0;
    border-radius: 26px;
    background: linear-gradient(135deg, #f59e0b 0%, #0f766e 100%);
    box-shadow: 0 16px 36px rgba(245, 158, 11, 0.16);
  }

  .diary-card,
  .status-card {
    border: 1px solid rgba(226, 232, 240, 0.76);
    background: rgba(255, 255, 255, 0.96);
    box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
  }

  .back-button {
    padding: 8px 0;
  }
}
</style>
