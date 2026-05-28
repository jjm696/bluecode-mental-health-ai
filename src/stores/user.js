import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { userLogin, userRegister } from '@/api/admin'
import { clearSessionAuth, getSessionProfile, getSessionToken, setSessionAuth } from '@/utils/sessionAuth'
import { useAdminStore } from '@/stores/admin'

export const useUserStore = defineStore('user', () => {
  const token = ref(getSessionToken('user'))
  const profile = ref(getSessionProfile('user'))

  const isLoggedIn = computed(() => Boolean(token.value))

  const login = async (payload) => {
    const adminStore = useAdminStore()
    adminStore.logout()
    const data = await userLogin(payload)
    token.value = data.token
    profile.value = data.profile
    setSessionAuth('user', data.token, data.profile)
    return data
  }

  const register = async (payload) => {
    const adminStore = useAdminStore()
    adminStore.logout()
    const data = await userRegister(payload)
    token.value = data.token
    profile.value = data.profile
    setSessionAuth('user', data.token, data.profile)
    return data
  }

  const logout = () => {
    token.value = ''
    profile.value = { nickname: '', avatar: '', username: '' }
    clearSessionAuth('user')
  }

  return {
    token,
    profile,
    isLoggedIn,
    login,
    register,
    logout,
  }
})
