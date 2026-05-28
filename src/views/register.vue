<template>
  <div class="auth-layout">
    <div class="auth-left-section">
      <div class="auth-content">
        <h1 class="auth-title">加入心理AI助手</h1>
        <p class="auth-text">建立属于自己的心理支持空间，开始记录情绪、沉淀会话、获得持续陪伴。</p>
        <div class="auth-robot">
          <img :src="robotIcon" alt="robot" class="auth-robot-image" />
        </div>
      </div>
    </div>

    <div class="auth-right-section">
      <div class="auth-container">
        <RouterLink to="/" class="auth-back-home">←返回首页</RouterLink>
        <div class="auth-title-block">
          <h2>创建您的账户</h2>
          <p>填写基本信息，开始您的情绪陪伴之旅</p>
        </div>

        <el-form :model="form" class="auth-form-card" @submit.prevent="handleSubmit">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
          </el-form-item>
          <el-button class="auth-primary-btn" type="primary" :loading="loading" @click="handleSubmit">注册账户</el-button>
          <div class="auth-switch">
            <RouterLink class="auth-switch-link" to="/login">用户登录</RouterLink>
            <RouterLink class="auth-switch-link" to="/admin-login">管理员登录</RouterLink>
          </div>
          <div class="auth-footer">
            已有账户？
            <RouterLink to="/login">去登录</RouterLink>
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
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: '',
})

const robotIcon = new URL('@/assets/images/机器人.png', import.meta.url).href

const handleSubmit = async () => {
  if (!form.username || !form.nickname || !form.password || !form.confirmPassword) {
    ElMessage.warning('请完整填写注册信息')
    return
  }

  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功，已自动登录')
    router.push('/')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
</style>
