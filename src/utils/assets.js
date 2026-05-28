export const dashboardIconMap = {
  users: new URL('@/assets/images/users.png', import.meta.url).href,
  like: new URL('@/assets/images/like.png', import.meta.url).href,
  comments: new URL('@/assets/images/comments.png', import.meta.url).href,
  smile: new URL('@/assets/images/smile.png', import.meta.url).href,
}

export const emotionImageMap = {
  开心: new URL('@/assets/images/开心.png', import.meta.url).href,
  快乐: new URL('@/assets/images/开心.png', import.meta.url).href,
  平静: new URL('@/assets/images/平静.png', import.meta.url).href,
  困惑: new URL('@/assets/images/困惑.png', import.meta.url).href,
  兴奋: new URL('@/assets/images/兴奋.png', import.meta.url).href,
  惊讶: new URL('@/assets/images/惊讶.png', import.meta.url).href,
  悲伤: new URL('@/assets/images/悲伤.png', import.meta.url).href,
  焦虑: new URL('@/assets/images/焦虑.png', import.meta.url).href,
  疲惫: new URL('@/assets/images/疲惫.png', import.meta.url).href,
}

export const getEmotionImage = (emotion) =>
  emotionImageMap[emotion] || new URL('@/assets/images/机器人.png', import.meta.url).href
