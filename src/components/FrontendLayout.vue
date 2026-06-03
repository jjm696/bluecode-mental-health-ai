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
          <RouterLink v-if="userStore.isLoggedIn" class="text-link records-link" to="/my-records">我的记录</RouterLink>
        </div>
      </div>
    </header>

    <main class="page-body">
      <router-view />
    </main>

    <nav class="mobile-tabbar">
      <RouterLink class="mobile-tab" to="/">
        <span class="tab-icon">⌂</span>
        <span>首页</span>
      </RouterLink>
      <RouterLink class="mobile-tab" to="/knowledge">
        <span class="tab-icon">□</span>
        <span>知识</span>
      </RouterLink>
      <RouterLink class="mobile-tab primary" to="/consultation">
        <span class="tab-icon">✦</span>
        <span>咨询</span>
      </RouterLink>
      <RouterLink class="mobile-tab" to="/emotion-diary">
        <span class="tab-icon">◌</span>
        <span>日记</span>
      </RouterLink>
      <RouterLink class="mobile-tab" to="/my-records">
        <span class="tab-icon">≡</span>
        <span>记录</span>
      </RouterLink>
    </nav>

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

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
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

.mobile-tabbar {
  display: none;
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

@media (max-width: 640px) {
  .site-header {
    background: rgba(255, 255, 255, 0.94);
  }

  .navbar-container {
    height: 58px;
    padding: 0 14px;
    gap: 8px;
    flex-wrap: nowrap;
  }

  .brand-section {
    min-width: 0;
    flex: 1;
  }

  .brand-logo {
    width: 32px;
    height: 32px;
  }

  .brand-name {
    margin-left: 8px;
    font-size: 17px;
    white-space: nowrap;
  }

  .nav-section,
  .records-link {
    display: none;
  }

  .header-actions {
    gap: 8px;
  }

  .user-pill {
    max-width: 118px;
    padding: 4px 8px 4px 4px;
    gap: 6px;
    font-size: 13px;
  }

  .user-pill span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .button-link,
  .register-link {
    font-size: 13px;
  }

  .register-link {
    padding: 8px 12px;
  }

  .page-body {
    padding-bottom: 76px;
  }

  .footer-container {
    display: none;
  }

  .mobile-tabbar {
    position: fixed;
    left: 10px;
    right: 10px;
    bottom: 10px;
    z-index: 30;
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 4px;
    padding: 8px;
    border: 1px solid rgba(226, 232, 240, 0.82);
    border-radius: 22px;
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 18px 50px rgba(31, 41, 55, 0.18);
    backdrop-filter: blur(18px);
  }

  .mobile-tab {
    min-width: 0;
    height: 50px;
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    color: #64748b;
    font-size: 12px;
    font-weight: 700;
  }

  .tab-icon {
    width: 8px;
    height: 8px;
    border-radius: 999px;
    background: #cbd5e1;
    color: transparent;
    font-size: 0;
    line-height: 1;
  }

  .primary .tab-icon {
    width: 22px;
    height: 4px;
    background: rgba(255, 255, 255, 0.86);
  }

  .mobile-tab.router-link-active {
    color: #0f766e;
    background: #ecfdf5;
  }

  .mobile-tab.primary {
    color: white;
    background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%);
    box-shadow: 0 10px 24px rgba(245, 158, 11, 0.28);
  }

  .mobile-tab.primary.router-link-active {
    color: white;
  }
}
</style>
