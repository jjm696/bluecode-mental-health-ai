import { request } from './client'

export const adminLogin = (payload) =>
  request('/auth/admin-login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const userLogin = (payload) =>
  request('/auth/user-login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const userRegister = (payload) =>
  request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const fetchAdminProfile = () => request('/auth/profile')
export const fetchDashboardOverview = () => request('/dashboard/overview')
export const fetchArticleCategories = () => request('/articles/categories')
export const fetchArticles = (params = {}) =>
  request(`/articles?${new URLSearchParams(params).toString()}`)
export const fetchArticleDetail = (id) => request(`/articles/${id}`)
export const createArticle = (payload) =>
  request('/articles', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
export const updateArticle = (id, payload) =>
  request(`/articles/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
export const deleteArticle = (id) =>
  request(`/articles/${id}`, {
    method: 'DELETE',
  })
export const fetchConsultations = (params = {}) =>
  request(`/consultations?${new URLSearchParams(params).toString()}`)
export const fetchConsultationDetail = (id) => request(`/consultations/${id}`)
export const fetchEmotions = (params = {}) =>
  request(`/emotions?${new URLSearchParams(params).toString()}`)
export const saveEmotionDiary = (payload) =>
  request('/emotions', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
export const fetchUserConsultationSessions = () => request('/user/consultations')
export const fetchUserConsultationDetail = (id) => request(`/user/consultations/${id}`)
export const fetchCurrentUserEmotions = () => request('/user/emotions')
export const deleteUserConsultationSession = (id) =>
  request(`/user/consultations/${id}`, {
    method: 'DELETE',
  })
export const sendUserConsultationMessage = (payload) =>
  request('/user/consultations/send', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
