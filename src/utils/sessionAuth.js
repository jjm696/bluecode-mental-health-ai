const STORAGE = window.sessionStorage

export const getSessionToken = (role) => STORAGE.getItem(`${role}_token`) || ''

export const getSessionProfile = (role) =>
  JSON.parse(STORAGE.getItem(`${role}_profile`) || '{"nickname":"","avatar":"","username":""}')

export const setSessionAuth = (role, token, profile) => {
  STORAGE.setItem(`${role}_token`, token)
  STORAGE.setItem(`${role}_profile`, JSON.stringify(profile))
}

export const clearSessionAuth = (role) => {
  STORAGE.removeItem(`${role}_token`)
  STORAGE.removeItem(`${role}_profile`)
}
