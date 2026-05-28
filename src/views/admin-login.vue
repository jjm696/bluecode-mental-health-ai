<template>
  <div class="auth-layout">
    <div class="auth-left-section">
      <div class="auth-content">
        <h1 class="auth-title">心理健康 AI 助手后台</h1>
        <p class="auth-text">统一管理知识文章、情绪日志、咨询记录与风险复核，让项目具备完整的业务闭环。</p>
      </div>
    </div>

    <div class="auth-right-section">
      <div class="auth-container">
        <RouterLink to="/" class="auth-back-home">←返回首页</RouterLink>
        <div class="auth-form-card">
        <div class="panel-head">
          <img :src="robotIcon" alt="robot" class="panel-logo" />
          <div>
            <h2>管理员登录</h2>
            <p>使用后台账号进入系统</p>
          </div>
        </div>

        <el-form :model="form" @submit.prevent="handleSubmit">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
          </el-form-item>
          <el-button class="auth-primary-btn" type="primary" :loading="loading" @click="handleSubmit">
            登录后台
          </el-button>
          <div class="auth-switch">
            <RouterLink class="auth-switch-link" to="/login">用户登录</RouterLink>
            <RouterLink class="auth-switch-link active" to="/admin-login">管理员登录</RouterLink>
          </div>
        </el-form>

        <div class="auth-demo-tip">
          演示账号：<strong>admin</strong><br />
          演示密码：<strong>admin123</strong>
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'
import '@/styles/auth-shell.css'

const router = useRouter()
const adminStore = useAdminStore()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123',
})

const robotIcon = new URL('@/assets/images/机器人.png', import.meta.url).href

const handleSubmit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    await adminStore.login(form)
    ElMessage.success('登录成功')
    router.push('/back/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
</style>
