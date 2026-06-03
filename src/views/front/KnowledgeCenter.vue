<template>
  <div class="knowledge-container">
    <div class="header-section">
      <div class="header-content">
        <img :src="bookIcon" alt="book" class="header-icon" />
        <div>
          <h1>心理知识库</h1>
          <p>浏览心理健康基础、情绪管理、睡眠修复与自我成长内容。</p>
        </div>
      </div>
    </div>

    <div class="content">
      <aside class="recommend-section">
        <h3 class="section-title">推荐阅读</h3>
        <div v-if="loading" class="recommend-loading">正在加载推荐内容...</div>
        <div v-else-if="articles.length" class="recommend-list">
          <RouterLink
            v-for="item in articles.slice(0, 4)"
            :key="item.id"
            class="recommend-item"
            :to="`/knowledge/${item.id}`"
          >
            <strong>{{ item.title }}</strong>
            <div class="read-count">
              <span>阅读 {{ item.readCount }}</span>
              <span>{{ item.category }}</span>
            </div>
          </RouterLink>
        </div>
        <el-empty v-else description="暂无推荐文章" :image-size="88" />
      </aside>

      <div class="article-list">
        <div v-if="loading" class="front-loading">正在加载知识库内容...</div>
        <el-empty v-else-if="!articles.length" description="暂无知识文章" :image-size="120" />
        <article v-else v-for="item in articles" :key="item.id" class="article-item">
          <img :src="coverIcon" alt="cover" class="cover-image" />
          <div class="info">
            <div class="title">
              <RouterLink :to="`/knowledge/${item.id}`">{{ item.title }}</RouterLink>
              <el-tag size="small" type="warning" round>{{ item.category }}</el-tag>
            </div>
            <p class="summary">{{ item.summary }}</p>
            <div class="flex-box meta-row">
              <span>阅读 {{ item.readCount }}</span>
              <span>{{ item.updatedAt }}</span>
            </div>
            <div class="tag-list">
              <el-tag v-for="tag in item.tags || []" :key="tag" round effect="plain">{{ tag }}</el-tag>
            </div>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchArticles } from '@/api/admin'
import { knowledgeArticles } from '@/mock/adminData'

const articles = ref(knowledgeArticles)
const loading = ref(false)
const bookIcon = new URL('@/assets/images/book.png', import.meta.url).href
const coverIcon = new URL('@/assets/images/book.png', import.meta.url).href

onMounted(async () => {
  loading.value = true
  try {
    const data = await fetchArticles({ page: 1, pageSize: 20, status: '1' })
    articles.value = data.list
  } catch (error) {
    articles.value = knowledgeArticles.filter((item) => item.status === '已发布')
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.knowledge-container {
  background: linear-gradient(135deg, #fafbfc 0%, #f7f9fc 50%, #f2f6fa 100%);
  min-height: calc(100vh - 140px);
}

.header-section {
  background: linear-gradient(135deg, #f59e0b 0%, #8b5cf6 100%);
  color: white;
  padding: 48px 20px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 52px;
  height: 52px;
}

.header-content h1,
.header-content p {
  margin: 0;
}

.header-content p {
  margin-top: 8px;
  opacity: 0.92;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 20px;
  padding: 24px 20px 36px;
}

.recommend-section {
  width: 280px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 18px;
  height: fit-content;
}

.section-title {
  margin: 0 0 14px;
  font-size: 18px;
  color: #374151;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.recommend-loading,
.front-loading {
  padding: 18px 4px;
  color: #6b7280;
}

.recommend-item {
  border-left: 4px solid #f59e0b;
  padding-left: 10px;
  color: #1f2937;
}

.read-count {
  margin-top: 12px;
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 12px;
}

.article-list {
  flex: 1;
}

.article-item {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  padding: 18px;
  margin-bottom: 18px;
  display: flex;
}

.cover-image {
  width: 96px;
  height: 96px;
  border-radius: 16px;
  background: #fff7ed;
  object-fit: contain;
  padding: 18px;
}

.info {
  margin-left: 20px;
  flex: 1;
}

.title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title a {
  color: #1f2937;
  font-size: 20px;
  font-weight: 700;
}

.summary {
  margin: 12px 0;
  color: #6b7280;
  line-height: 1.7;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #9ca3af;
  font-size: 13px;
}

.tag-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 14px;
}

@media (max-width: 960px) {
  .content {
    flex-direction: column;
  }

  .recommend-section {
    width: auto;
  }

  .article-item {
    flex-direction: column;
  }

  .info {
    margin: 16px 0 0;
  }
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
    font-size: 26px;
  }

  .header-content p {
    font-size: 14px;
    line-height: 1.65;
  }

  .content {
    gap: 14px;
    padding: 14px 10px 26px;
  }

  .recommend-section {
    padding: 16px;
    border-radius: 14px;
  }

  .recommend-list {
    gap: 10px;
  }

  .recommend-item {
    display: block;
    padding: 10px 0 10px 12px;
  }

  .read-count {
    margin-top: 8px;
    flex-wrap: wrap;
  }

  .article-item {
    padding: 14px;
    margin-bottom: 12px;
    border-radius: 14px;
  }

  .cover-image {
    width: 72px;
    height: 72px;
    padding: 14px;
    border-radius: 14px;
  }

  .info {
    margin-top: 12px;
  }

  .title {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }

  .title a {
    font-size: 18px;
    line-height: 1.4;
  }

  .summary {
    margin: 10px 0;
    font-size: 14px;
    line-height: 1.65;
  }

  .meta-row {
    align-items: flex-start;
    flex-direction: column;
    gap: 4px;
  }
}
</style>
