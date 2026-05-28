<template>
  <div class="auth-layout">
    <div class="auth-left-section">
      <div class="auth-content">
        <h1 class="auth-title">心理AI助手</h1>
        <p class="auth-text">每个深夜、每个焦虑的时刻，我们都在这里。不必独自承受，让心与心的连接温暖您的每一天</p>
        <div class="auth-robot">
          <img :src="robotIcon" alt="robot" class="auth-robot-image" />
        </div>
      </div>
    </div>

    <div class="auth-right-section">
      <div class="auth-container">
        <RouterLink to="/" class="auth-back-home">←返回首页</RouterLink>
        <div class="auth-title-block">
          <h2>登录您的账户</h2>
          <p>请输入您的登录信息</p>
        </div>

        <el-form :model="form" class="auth-form-card" @submit.prevent="handleSubmit">
          <el-form-item label="用户名或邮箱">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button class="auth-primary-btn" type="primary" :loading="loading" @click="handleSubmit">登录账户</el-button>
          <div class="auth-switch">
            <RouterLink class="auth-switch-link active" to="/login">用户登录</RouterLink>
            <RouterLink class="auth-switch-link" to="/admin-login">管理员登录</RouterLink>
          </div>
          <div class="auth-footer">
            还没有账户？
            <RouterLink to="/register">去注册</RouterLink>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import '@/styles/auth-shell.css'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const robotIcon = new URL('@/assets/images/机器人.png', import.meta.url).href

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
</style>
