<script setup>
import ChatShell from '../components/ChatShell.vue'
import { streamManusChat } from '../api/chat.js'

const sendRequest = ({ message, ...options }) =>
  streamManusChat({ params: { message }, ...options })
</script>

<template>
  <ChatShell
    variant="manus"
    title="AI 超级智能体"
    subtitle="规划、推理与工具执行"
    welcome="你好，我是 AI 超级智能体。告诉我你想完成什么，我会拆解任务、选择工具，并把执行过程实时同步给你。"
    :suggestions="['帮我规划一次周末旅行', '调研一个技术方案', '把复杂任务拆成执行清单']"
    :send-request="sendRequest"
  />
</template>
