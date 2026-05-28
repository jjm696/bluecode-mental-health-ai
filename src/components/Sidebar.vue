<template>
  <el-aside class="sidebar" :width="isCollapse ? '72px' : '264px'">
    <div class="brand">
      <el-image :src="iconUrl" alt="logo" fit="contain" class="brand-logo" />
      <div v-show="!isCollapse" class="info-card">
        <h1 class="brand-title">心理健康 AI 助手</h1>
        <p class="brand-subtitle">管理后台</p>
      </div>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      :collapse-transition="false"
      class="menu-style"
      @select="selectMenu"
    >
      <el-menu-item
        v-for="item in backendMenus"
        :key="item.name"
        :index="`/back/${item.path}`"
      >
        <el-icon><component :is="item.meta.icon" /></el-icon>
        <span>{{ item.meta.title }}</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { backendMenus } from '@/router'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()
const iconUrl = new URL('@/assets/images/机器人.png', import.meta.url).href

const isCollapse = computed(() => adminStore.isCollapse)
const activeMenu = computed(() => route.path)

const selectMenu = (index) => {
  router.push(index)
}
</script>

<style lang="scss" scoped>
.sidebar {
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
}

.brand {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 88px;
  padding: 16px 12px;
  background-color: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.brand-logo {
  width: 48px;
  height: 48px;
  margin-right: 10px;
}

.info-card {
  overflow: hidden;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.brand-subtitle {
  margin-top: 4px;
  font-size: 14px;
  color: #6b7280;
}

.menu-style {
  height: calc(100% - 88px);
  border-right: none;
}
</style>
