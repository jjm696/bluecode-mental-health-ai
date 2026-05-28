<template>
  <div class="frontend-layout">
    <header class="site-header">
      <div class="navbar-container">
        <RouterLink class="brand-section" to="/">
          <img :src="robotIcon" alt="AI 心理助手" class="brand-logo" />
          <span class="brand-name">心理健康 AI 助手</span>
        </RouterLink>

        <nav class="nav-section">
          <RouterLink
            v-for="item in frontendMenus"
            :key="item.name"
            class="nav-link"
            :to="item.path ? `/${item.path}` : '/'"
          >
            {{ item.meta.title }}
          </RouterLink>
        </nav>

        <div class="header-actions">
          <template v-if="userStore.isLoggedIn">
            <div class="user-pill">
              <el-avatar :src="userAvatar" :size="34" />
              <span>{{ userName }}</span>
            </div>
            <button class="text-link button-link" @click="handleLogout">退出</button>
          </template>
          <template v-else>
            <RouterLink class="register-link" to="/login">登录</RouterLink>
          </template>
          <RouterLink v-if="userStore.isLoggedIn" class="text-link" to="/my-records">我的记录</RouterLink>
        </div>
      </div>
    </header>

    <main class="page-body">
      <router-view />
    </main>

    <footer class="footer-container">
      <div class="footer-bottom">
        心理健康 AI 助手 · Vue3 + Spring Boot + MySQL · 用户端与后台管理双端闭环
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { frontendMenus } from '@/router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const robotIcon = new URL('@/assets/images/机器人.png', import.meta.url).href
const userName = computed(() => userStore.profile.nickname || userStore.profile.username || '用户')
const userAvatar = computed(
  () => userStore.profile.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
)

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
.frontend-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #fffefb 0%, #f7fbfa 45%, #f6f8fc 100%);
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(16px);
  background: rgba(255, 255, 255, 0.84);
  border-bottom: 1px solid rgba(226, 232, 240, 0.9);
}

.navbar-container {
  max-width: 1200px;
  height: 78px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.brand-section {
  display: flex;
  align-items: center;
  color: inherit;
}

.brand-logo {
  width: 42px;
  height: 42px;
  object-fit: contain;
}

.brand-name {
  margin-left: 10px;
  font-size: 22px;
  font-weight: 700;
  color: #243042;
}

.nav-section {
  display: flex;
  align-items: center;
  gap: 36px;
}

.nav-link {
  color: #4b5563;
  font-size: 15px;
  font-weight: 600;
}

.nav-link.router-link-active {
  color: #ea580c;
}

.text-link {
  color: #374151;
  font-weight: 700;
}

.button-link {
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
}

.register-link {
  padding: 10px 16px;
  border-radius: 999px;
  background: #5f95e8;
  color: white;
  font-weight: 700;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  color: #243042;
  font-weight: 700;
}

.page-body {
  flex: 1;
}

.footer-container {
  margin-top: auto;
  background: #1f2937;
  color: white;
  padding: 18px 0;
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.86);
}

@media (max-width: 920px) {
  .navbar-container {
    height: auto;
    padding: 16px 20px;
    flex-wrap: wrap;
  }

  .nav-section {
    width: 100%;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
  }
}
</style>
