import { createRouter, createWebHistory } from 'vue-router'
import BackendLayout from '@/components/BackendLayout.vue'
import FrontendLayout from '@/components/FrontendLayout.vue'
import { useAdminStore } from '@/stores/admin'
import { useUserStore } from '@/stores/user'

const backendChildren = [
  {
    path: 'dashboard',
    name: 'dashboard',
    component: () => import('@/views/dashboard.vue'),
    meta: { title: '数据分析', icon: 'PieChart' },
  },
  {
    path: 'knowledge',
    name: 'knowledge',
    component: () => import('@/views/knowledge.vue'),
    meta: { title: '知识文章', icon: 'ChatLineSquare' },
  },
  {
    path: 'consultations',
    name: 'consultations',
    component: () => import('@/views/consultations.vue'),
    meta: { title: '咨询记录', icon: 'Message' },
  },
  {
    path: 'emotional',
    name: 'emotional',
    component: () => import('@/views/emotional.vue'),
    meta: { title: '情绪日志', icon: 'User' },
  },
]

const frontendChildren = [
  {
    path: '',
    name: 'home',
    component: () => import('@/views/front/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: 'knowledge',
    name: 'front-knowledge',
    component: () => import('@/views/front/KnowledgeCenter.vue'),
    meta: { title: '知识库' },
  },
  {
    path: 'knowledge/:id',
    name: 'front-article-detail',
    component: () => import('@/views/front/ArticleDetail.vue'),
    meta: { title: '文章详情' },
  },
  {
    path: 'consultation',
    name: 'front-consultation',
    component: () => import('@/views/front/Consultation.vue'),
    meta: { title: 'AI 咨询' },
  },
  {
    path: 'emotion-diary',
    name: 'front-emotion-diary',
    component: () => import('@/views/front/EmotionDiary.vue'),
    meta: { title: '情绪日记' },
  },
  {
    path: 'my-records',
    name: 'front-my-records',
    component: () => import('@/views/front/MyRecords.vue'),
    meta: { title: '我的记录' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: FrontendLayout,
      children: frontendChildren,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/register.vue'),
    },
    {
      path: '/admin-login',
      name: 'admin-login',
      component: () => import('@/views/admin-login.vue'),
    },
    {
      path: '/back',
      component: BackendLayout,
      redirect: '/back/dashboard',
      meta: { requiresAuth: true },
      children: backendChildren,
    },
  ],
})

router.beforeEach(async (to) => {
  const adminStore = useAdminStore()
  const userStore = useUserStore()

  const authPages = ['/login', '/register', '/admin-login']
  if (authPages.includes(to.path)) {
    if (adminStore.isLoggedIn) {
      return '/back/dashboard'
    }
    if (userStore.isLoggedIn) {
      return '/'
    }
  }

  if (to.meta.requiresAuth) {
    if (!adminStore.isLoggedIn) {
      return '/admin-login'
    }

    if (!adminStore.profile.username) {
      try {
        await adminStore.syncProfile()
      } catch (error) {
        adminStore.logout()
        return '/admin-login'
      }
    }
  }

  return true
})

export const backendMenus = backendChildren
export const frontendMenus = frontendChildren.filter((item) => item.name !== 'front-article-detail')

export default router
