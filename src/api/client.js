const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const getTokenByPath = (path) => {
  const userToken = sessionStorage.getItem('user_token') || ''
  const adminToken = sessionStorage.getItem('admin_token') || ''

  if (path.startsWith('/user')) {
    return userToken
  }

  if (path === '/emotions') {
    return userToken || adminToken
  }

  if (path.startsWith('/auth/user') || path === '/auth/register') {
    return userToken
  }

  if (path.startsWith('/auth/admin') || path === '/auth/profile') {
    return adminToken
  }

  return adminToken || userToken
}

export const request = async (path, options = {}) => {
  const token = getTokenByPath(path)

  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
    ...options,
  })

  const result = await response.json()

  if (!response.ok || result.code !== 0) {
    throw new Error(result.message || '请求失败')
  }

  return result.data
}
