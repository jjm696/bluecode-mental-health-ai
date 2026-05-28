<template>
  <div class="backend-layout">
    <el-container class="main-container">
      <Sidebar />
      <el-container>
        <el-header>
            <Navbar />
        </el-header>
        <el-main class="main-content">
            <router-view class="content-container"></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup>
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
</script>
<style lang="scss" scoped>
.backend-layout {
  height: 100vh;
  background: #f3f6fb;
  .el-header {
    height: 74px !important;
    padding: 0 !important;
  }
  .main-container {
    height: 100%;
    .main-content {
      padding: 20px;
      background: #f3f6fb;
    }
    .content-container {
      min-height: calc(100vh - 114px);
      padding: 24px;
      background-color: #fff;
      border-radius: 24px;
    }
  }
}
</style>
