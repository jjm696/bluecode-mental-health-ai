<template>
  <div>
    <PageHead
      title="知识文章"
      description="管理心理健康知识库内容，支持按标题、分类与状态筛选。"
    >
      <template #buttons>
        <el-button type="primary" @click="openCreateDialog">新增文章</el-button>
      </template>
    </PageHead>

    <TableSearch :form-item="formItem" @search="handleSearch" @reset="handleReset" />

    <el-card shadow="never" class="table-card">
      <el-table v-loading="tableLoading" :data="articleList" stripe empty-text="暂无文章数据">
        <el-table-column prop="title" label="文章标题" min-width="220" />
        <el-table-column prop="category" label="分类" width="140" />
        <el-table-column prop="tags" label="标签" min-width="220">
          <template #default="{ row }">
            <div class="tag-list">
              <el-tag v-for="tag in row.tags || []" :key="tag" effect="plain" round>
                {{ tag }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="readCount" label="阅读量" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '已发布' ? 'success' : 'warning'" round>
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openPreview(row)">预览</el-button>
            <el-button link type="success" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确认删除这篇文章？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-info">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :current-page="pagination.page"
          :page-sizes="[5, 10, 20]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" :title="activeArticle?.title || '文章预览'" width="720px">
      <template v-if="activeArticle">
        <div class="article-meta">
          <el-tag type="warning" round>{{ activeArticle.category }}</el-tag>
          <span>阅读量 {{ activeArticle.readCount }}</span>
          <span>更新时间 {{ activeArticle.updatedAt }}</span>
        </div>
        <p class="article-summary">{{ activeArticle.summary }}</p>
        <div class="tag-list article-tags">
          <el-tag v-for="tag in activeArticle.tags || []" :key="tag" effect="light" round>
            {{ tag }}
          </el-tag>
        </div>
        <div class="article-content" v-html="formatContent(activeArticle.content)"></div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="editorVisible"
      :title="editorMode === 'create' ? '新增文章' : '编辑文章'"
      width="760px"
      destroy-on-close
    >
      <el-form label-width="90px" :model="editorForm">
        <el-form-item label="文章标题">
          <el-input v-model="editorForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editorForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input
            v-model="editorForm.tagsText"
            placeholder="多个标签用英文逗号分隔，例如：焦虑, 呼吸训练"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editorForm.status">
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="阅读量">
          <el-input-number v-model="editorForm.readCount" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="editorForm.summary" type="textarea" :rows="3" placeholder="请输入文章摘要" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input
            v-model="editorForm.content"
            type="textarea"
            :rows="10"
            placeholder="请输入文章正文，支持简单 Markdown 风格三级标题"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitEditor">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHead from '@/components/PageHead.vue'
import TableSearch from '@/components/TableSearch.vue'
import { knowledgeArticles } from '@/mock/adminData'
import {
  createArticle,
  deleteArticle,
  fetchArticleCategories,
  fetchArticleDetail,
  fetchArticles,
  updateArticle,
} from '@/api/admin'
import { formatContent } from '@/utils/psychology'

const filters = ref({
  title: '',
  category: '',
  status: '',
})

const articleList = ref(knowledgeArticles)
const tableLoading = ref(false)
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: knowledgeArticles.length,
})
const previewVisible = ref(false)
const activeArticle = ref(null)
const categoryOptions = ref([
  { id: 1, name: '心理健康基础' },
  { id: 2, name: '情绪管理' },
  { id: 3, name: '睡眠修复' },
  { id: 4, name: '自我成长' },
])
const editorVisible = ref(false)
const editorMode = ref('create')
const editingId = ref(null)
const saving = ref(false)

const createEmptyEditorForm = () => ({
  title: '',
  categoryId: '',
  tagsText: '',
  status: 1,
  readCount: 0,
  summary: '',
  content: '',
})

const editorForm = reactive(createEmptyEditorForm())

const formItem = computed(() => [
  { comp: 'input', prop: 'title', label: '文章标题', placeholder: '请输入文章标题' },
  {
    comp: 'select',
    prop: 'category',
    label: '分类',
    placeholder: '请选择分类',
    options: categoryOptions.value.map((item) => ({
      label: item.name,
      value: item.name,
    })),
  },
  {
    comp: 'select',
    prop: 'status',
    label: '状态',
    placeholder: '请选择状态',
    options: [
      { label: '已发布', value: '已发布' },
      { label: '草稿', value: '草稿' },
    ],
  },
])

const parseTags = (text) =>
  text
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean)

const resetEditorForm = () => {
  Object.assign(editorForm, createEmptyEditorForm())
  editingId.value = null
}

const loadCategories = async () => {
  try {
    categoryOptions.value = await fetchArticleCategories()
  } catch (error) {
    categoryOptions.value = categoryOptions.value
  }
}

const loadArticles = async (formData = filters.value) => {
  tableLoading.value = true
  try {
    const data = await fetchArticles({
      title: formData.title || '',
      category: formData.category || '',
      status: formData.status === '已发布' ? '1' : formData.status === '草稿' ? '0' : '',
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    articleList.value = data.list
    pagination.total = data.total
  } catch (error) {
    articleList.value = knowledgeArticles
    pagination.total = knowledgeArticles.length
  } finally {
    tableLoading.value = false
  }
}

const handleSearch = async (formData) => {
  filters.value = formData
  pagination.page = 1
  await loadArticles(formData)
}

const handleReset = (formData) => {
  filters.value = formData
}

const openPreview = async (article) => {
  try {
    activeArticle.value = await fetchArticleDetail(article.id)
  } catch (error) {
    activeArticle.value = article
  }
  previewVisible.value = true
}

const openCreateDialog = () => {
  editorMode.value = 'create'
  resetEditorForm()
  editorVisible.value = true
}

const openEditDialog = async (article) => {
  editorMode.value = 'edit'
  resetEditorForm()

  try {
    const detail = await fetchArticleDetail(article.id)
    editingId.value = detail.id
    const matchedCategory = categoryOptions.value.find((item) => item.name === detail.category)
    Object.assign(editorForm, {
      title: detail.title,
      categoryId: matchedCategory?.id || '',
      tagsText: (detail.tags || []).join(', '),
      status: detail.status === '已发布' ? 1 : 0,
      readCount: Number(detail.readCount || 0),
      summary: detail.summary || '',
      content: detail.content || '',
    })
    editorVisible.value = true
  } catch (error) {
    ElMessage.error('文章详情获取失败')
  }
}

const submitEditor = async () => {
  if (!editorForm.title || !editorForm.categoryId) {
    ElMessage.warning('请先填写标题和分类')
    return
  }

  saving.value = true

  const payload = {
    title: editorForm.title,
    categoryId: editorForm.categoryId,
    tags: parseTags(editorForm.tagsText),
    status: editorForm.status,
    readCount: Number(editorForm.readCount || 0),
    summary: editorForm.summary,
    content: editorForm.content,
  }

  try {
    if (editorMode.value === 'create') {
      await createArticle(payload)
      ElMessage.success('文章创建成功')
    } else {
      await updateArticle(editingId.value, payload)
      ElMessage.success('文章更新成功')
    }

    editorVisible.value = false
    await loadArticles()
  } catch (error) {
    ElMessage.error(error.message || '保存失败，请确认后端已启动并登录')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (article) => {
  try {
    await deleteArticle(article.id)
    ElMessage.success('文章删除成功')
    if (articleList.value.length === 1 && pagination.page > 1) {
      pagination.page -= 1
    }
    await loadArticles()
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

const handlePageChange = async (page) => {
  pagination.page = page
  await loadArticles()
}

const handleSizeChange = async (pageSize) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  await loadArticles()
}

onMounted(async () => {
  await loadCategories()
  await loadArticles()
})
</script>

<style lang="scss" scoped>
.table-card {
  border-radius: 18px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.article-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  color: #6b7280;
  margin-bottom: 16px;
}

.article-summary {
  margin-bottom: 16px;
  padding: 14px 16px;
  border-radius: 14px;
  background: #fff7ed;
  color: #9a3412;
}

.article-tags {
  margin-bottom: 20px;
}

.article-content {
  line-height: 1.8;
  color: #374151;
}

.article-content :deep(h3) {
  margin: 18px 0 10px;
  color: #111827;
}

.article-content :deep(p) {
  margin: 0 0 10px;
}
</style>
