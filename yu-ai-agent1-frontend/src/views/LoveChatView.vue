<script setup>
import ChatShell from '../components/ChatShell.vue'
import { streamLoveChat } from '../api/chat.js'

const chatId = crypto.randomUUID()
const sendRequest = ({ message, ...options }) =>
  streamLoveChat({ params: { message, chatId }, ...options })
</script>

<template>
  <ChatShell
    variant="love"
    title="AI 恋爱大师"
    subtitle="关系与沟通顾问"
    :chat-id="chatId"
    welcome="你好，我是你的 AI 恋爱大师。无论是暧昧、沟通、冲突，还是一段关系中的不确定，都可以告诉我。我会认真听完，再陪你一起理清。"
    :suggestions="['怎么判断对方是否喜欢我？', '帮我分析一段聊天记录', '吵架后应该怎么开口？']"
    :send-request="sendRequest"
  />
</template>
