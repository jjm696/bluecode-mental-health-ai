<template>
  <div class="home-container">
    <section class="hero-section">
      <div class="hero-inner">
        <div class="text">
          <h1 class="title">一次温暖的对话</h1>
          <h2 class="sub-title">化孤独为慰藉</h2>
          <p class="intro">每个深夜，每个焦虑的时刻，我们都在这里。不必独自承受，让心与心的连接温暖您的每一天</p>
          <div class="hero-actions">
            <RouterLink to="/consultation">
              <el-button size="large">开始倾诉，获得陪伴</el-button>
            </RouterLink>
            <RouterLink to="/emotion-diary">
              <el-button plain size="large">记录心情，释放情绪</el-button>
            </RouterLink>
          </div>
        </div>

        <div class="robot">
          <img :src="robotIcon" alt="robot" class="robot-image" />
        </div>
      </div>
    </section>

    <section class="feature-section">
      <div class="section-header">
        <span>产品能力</span>
        <h2>从用户自助到后台复核的一体化流程</h2>
      </div>
      <div class="feature-grid">
        <article v-for="item in features" :key="item.title" class="feature-card">
          <img :src="item.icon" :alt="item.title" class="feature-icon" />
          <h3>{{ item.title }}</h3>
          <p>{{ item.text }}</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const robotIcon = new URL('@/assets/images/机器人.png', import.meta.url).href
const smileIcon = new URL('@/assets/images/smile.png', import.meta.url).href
const bookIcon = new URL('@/assets/images/book.png', import.meta.url).href
const commentIcon = new URL('@/assets/images/comments.png', import.meta.url).href
const likeIcon = new URL('@/assets/images/like.png', import.meta.url).href

const features = [
  { title: 'AI 咨询会话', text: '通过连续对话帮助用户表达压力、焦虑与低落情绪。', icon: commentIcon },
  { title: '情绪日记记录', text: '引导用户记录睡眠、压力、情绪强度与当日事件。', icon: smileIcon },
  { title: '知识库支持', text: '结合心理健康内容做辅助解释与建议延展。', icon: bookIcon },
  { title: '后台运营复核', text: '把用户行为沉淀为结构化数据，方便后续人工干预。', icon: likeIcon },
]
</script>

<style lang="scss" scoped>
.home-container {
  padding-bottom: 48px;
  background: linear-gradient(180deg, rgba(74, 156, 140, 0.94) 0%, rgba(94, 169, 154, 0.92) 38%, #f7fbfa 38%, #f7fbfa 100%);
}

.hero-section {
  padding: 0 20px 24px;
}

.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 36px;
  min-height: calc(100vh - 220px);
}

.text {
  width: min(640px, 100%);
}

.title {
  margin: 0 0 10px;
  font-size: 66px;
  line-height: 1.08;
  color: white;
}

.sub-title {
  margin: 0 0 20px;
  font-size: 54px;
  color: #f7d74f;
}

.intro {
  font-size: 17px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.92);
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-top: 28px;
}

.hero-actions :deep(.el-button) {
  height: 48px;
  border-radius: 14px;
  padding: 0 22px;
}

.robot {
  width: 360px;
  height: 360px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.04));
  border: 2px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.22);
}

.robot-image {
  width: 190px;
  height: 190px;
  object-fit: contain;
}

.feature-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px 0;
}

.section-header span {
  color: #ea580c;
  font-weight: 700;
  font-size: 13px;
}

.section-header h2 {
  margin: 10px 0 0;
  font-size: 32px;
  color: #1f2937;
}

.feature-grid {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.feature-card {
  padding: 24px 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 12px 36px rgba(76, 92, 116, 0.08);
}

.feature-icon {
  width: 44px;
  height: 44px;
}

.feature-card h3 {
  margin: 16px 0 10px;
  color: #1f2937;
}

.feature-card p {
  margin: 0;
  color: #64748b;
  line-height: 1.7;
}

@media (max-width: 960px) {
  .hero-inner {
    flex-direction: column;
    min-height: auto;
  }

  .title {
    font-size: 42px;
  }

  .feature-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    flex-direction: column;
  }
}
</style>
