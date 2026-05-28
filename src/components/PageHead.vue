<template>
  <div class="page-head">
    <div>
      <p class="page-title">{{ title }}</p>
      <p v-if="description" class="page-description">{{ description }}</p>
    </div>
    <div class="action-btn">
      <slot name="buttons"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: '页面标题',
  },
  description: {
    type: String,
    default: '',
  },
})
</script>

<style lang="scss" scoped>
.page-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.page-description {
  margin-top: 6px;
  color: #6b7280;
  font-size: 14px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
