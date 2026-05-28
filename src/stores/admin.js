import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { adminLogin, fetchAdminProfile } from '@/api/admin'
import { clearSessionAuth, getSessionProfile, getSessionToken, setSessionAuth } from '@/utils/sessionAuth'
import { useUserStore } from '@/stores/user'

export const useAdminStore = defineStore('admin', () => {
  const isCollapse = ref(false)
  const token = ref(getSessionToken('admin'))
  const profile = ref(getSessionProfile('admin'))

  const isLoggedIn = computed(() => Boolean(token.value))

  const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value
  }

  const login = async (payload) => {
    const userStore = useUserStore()
    userStore.logout()
    const data = await adminLogin(payload)
    token.value = data.token
    profile.value = data.profile
    setSessionAuth('admin', data.token, data.profile)
    return data
  }

  const logout = () => {
    token.value = ''
    profile.value = { nickname: '', avatar: '', username: '' }
    clearSessionAuth('admin')
  }

  const syncProfile = async () => {
    if (!token.value) return null

    const data = await fetchAdminProfile()
    profile.value = data
    setSessionAuth('admin', token.value, data)
    return data
  }

  return {
    isCollapse,
    token,
    profile,
    isLoggedIn,
    toggleCollapse,
    login,
    logout,
    syncProfile,
  }
})
