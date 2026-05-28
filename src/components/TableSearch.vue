<template>
  <div class="table-search">
    <el-form :model="formData" label-width="92px">
      <el-row :gutter="16">
        <el-col
          v-for="item in normalizedItems"
          :key="item.prop"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-form-item :label="item.label" :prop="item.prop">
            <el-input
              v-if="item.comp === 'input'"
              v-model="formData[item.prop]"
              clearable
              :placeholder="item.placeholder"
            />

            <el-select
              v-else-if="item.comp === 'select'"
              v-model="formData[item.prop]"
              clearable
              :placeholder="item.placeholder"
              style="width: 100%"
            >
              <el-option
                v-for="opt in item.options || []"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>

            <el-date-picker
              v-else-if="item.comp === 'date'"
              v-model="formData[item.prop]"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="item.placeholder"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <div class="actions">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { computed, reactive, watch } from 'vue'

const props = defineProps({
  formItem: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['search', 'reset'])

const normalizedItems = computed(() => props.formItem || [])

const createInitialState = () =>
  normalizedItems.value.reduce((result, item) => {
    result[item.prop] = item.defaultValue ?? ''
    return result
  }, {})

const formData = reactive(createInitialState())

watch(
  normalizedItems,
  (items) => {
    const nextState = items.reduce((result, item) => {
      result[item.prop] = formData[item.prop] ?? item.defaultValue ?? ''
      return result
    }, {})

    Object.keys(formData).forEach((key) => {
      if (!(key in nextState)) {
        delete formData[key]
      }
    })

    Object.assign(formData, nextState)
  },
  { immediate: true },
)

const handleSearch = () => {
  emit('search', { ...formData })
}

const handleReset = () => {
  Object.assign(formData, createInitialState())
  emit('reset', { ...formData })
  emit('search', { ...formData })
}
</script>

<style lang="scss" scoped>
.table-search {
  margin-bottom: 20px;
  padding: 18px 20px 4px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
}

.actions {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}
</style>
