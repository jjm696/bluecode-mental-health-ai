<template>
  <div class="navbar">
    <div class="flex-box">
      <el-button circle @click="handleCollapse">
        <el-icon><Expand /></el-icon>
      </el-button>
      <p class="page-title">{{ pageTitle }}</p>
    </div>
    <div class="flex-box user-box">
      <el-dropdown @command="handleCommand">
        <div class="flex-box dropdown-trigger">
          <el-avatar :src="userAvatar" />
          <p class="user-name">{{ userName }}</p>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()
const avatarUrl = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const pageTitle = computed(() => route.meta.title || '心理健康 AI 助手')
const userName = computed(() => adminStore.profile.nickname || adminStore.profile.username || 'admin')
const userAvatar = computed(() => adminStore.profile.avatar || avatarUrl)

const handleCollapse = () => {
  adminStore.toggleCollapse()
}

const handleCommand = (command) => {
  if (command === 'logout') {
    adminStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-bottom: 1px solid #e5e7eb;
}

.flex-box {
  display: flex;
  align-items: center;
}

.page-title {
  margin-left: 16px;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.dropdown-trigger {
  cursor: pointer;
}

.user-box {
  gap: 8px;
}

.user-name {
  margin: 0 8px;
  font-weight: 700;
  color: #374151;
}
</style>
