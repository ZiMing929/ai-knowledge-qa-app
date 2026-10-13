<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLogo from './AppLogo.vue'
import IconBase from './IconBase.vue'
import MessageBubble from './MessageBubble.vue'

const props = defineProps({
  variant: { type: String, required: true },
  title: { type: String, required: true },
  subtitle: { type: String, required: true },
  chatId: { type: String, default: '' },
  welcome: { type: String, required: true },
  suggestions: { type: Array, default: () => [] },
  sendRequest: { type: Function, required: true },
})

const router = useRouter()
const message = ref('')
const sending = ref(false)
const connectionState = ref('ready')
const errorMessage = ref('')
const scrollRegion = ref(null)
const inputRef = ref(null)
let controller = null

const now = () => {
  const date = new Date()
  return {
    time: new Intl.DateTimeFormat('zh-CN', { hour: '2-digit', minute: '2-digit', hour12: false }).format(date),
    isoTime: date.toISOString(),
  }
}

const messages = ref([
  { id: crypto.randomUUID(), role: 'assistant', content: props.welcome, ...now() },
])

const scrollToBottom = async () => {
  await nextTick()
  if (scrollRegion.value) {
    scrollRegion.value.scrollTo({ top: scrollRegion.value.scrollHeight, behavior: 'smooth' })
  }
}

const send = async (preset) => {
  const content = (typeof preset === 'string' ? preset : message.value).trim()
  if (!content || sending.value) return

  message.value = ''
  errorMessage.value = ''
  sending.value = true
  connectionState.value = 'connecting'
  const userMessage = { id: crypto.randomUUID(), role: 'user', content, ...now() }
  const assistantMessage = {
    id: crypto.randomUUID(), role: 'assistant', content: '', streaming: true, ...now(),
  }
  messages.value.push(userMessage, assistantMessage)
  const activeReply = () => messages.value.find((item) => item.id === assistantMessage.id)
  await scrollToBottom()

  controller = new AbortController()
  try {
    await props.sendRequest({
      message: content,
      signal: controller.signal,
      onOpen: () => (connectionState.value = 'streaming'),
      onChunk: (chunk) => {
        activeReply().content += chunk
        scrollToBottom()
      },
    })
    if (!activeReply().content) activeReply().content = '服务已响应，但没有返回可显示的内容。'
    connectionState.value = 'ready'
  } catch (error) {
    if (error.name === 'AbortError') {
      activeReply().content ||= '已停止生成。'
      connectionState.value = 'ready'
    } else {
      activeReply().content ||= '这次连接没有成功，请检查后端服务后重试。'
      errorMessage.value = `${error.message}。请确认 Spring Boot 已在 8123 端口启动。`
      connectionState.value = 'error'
    }
  } finally {
    activeReply().streaming = false
    sending.value = false
    controller = null
    await scrollToBottom()
    inputRef.value?.focus()
  }
}

const stop = () => controller?.abort()
const newConversation = () => window.location.reload()

onMounted(() => inputRef.value?.focus())
onBeforeUnmount(() => controller?.abort())
</script>

<template>
  <div class="chat-page" :class="`theme-${variant}`">
    <header class="chat-header">
      <AppLogo class="chat-header__brand" />
      <button class="back-button" type="button" aria-label="返回应用中心" @click="router.push('/')">
        <IconBase name="back" /> <span>应用中心</span>
      </button>
      <div class="chat-header__identity">
        <span class="chat-header__icon" aria-hidden="true">
          <IconBase :name="variant === 'love' ? 'heart' : 'spark'" :size="21" />
        </span>
        <div>
          <strong>{{ title }}</strong>
          <small>{{ subtitle }}</small>
        </div>
      </div>
      <div class="chat-header__session">
        <span class="connection-pill" :class="`is-${connectionState}`">
          <i />{{ connectionState === 'streaming' ? '正在生成' : connectionState === 'connecting' ? '连接中' : connectionState === 'error' ? '连接异常' : '在线' }}
        </span>
        <button class="icon-button" type="button" aria-label="开始新会话" title="开始新会话" @click="newConversation">
          <IconBase name="refresh" />
        </button>
      </div>
    </header>

    <main id="main-content" class="chat-main">
      <div class="chat-stage">
        <div class="chat-stage__rail" aria-hidden="true">
          <span>LIVE</span><i /><span>{{ variant === 'love' ? 'EMPATHY' : 'AGENT' }}</span>
        </div>
        <section ref="scrollRegion" class="messages" aria-label="聊天记录" aria-live="polite">
          <div class="messages__intro">
            <span>SESSION INITIALIZED</span>
            <p v-if="chatId">会话 ID · {{ chatId }}</p>
            <p v-else>独立任务会话 · 实时工具调用</p>
          </div>
          <MessageBubble
            v-for="item in messages"
            :key="item.id"
            :message="item"
            :accent="variant"
          />
        </section>
      </div>

      <footer class="composer-area">
        <div v-if="messages.length === 1" class="suggestions" aria-label="快捷问题">
          <button v-for="suggestion in suggestions" :key="suggestion" type="button" @click="send(suggestion)">
            {{ suggestion }}
          </button>
        </div>
        <p v-if="errorMessage" id="chat-error" class="chat-error" role="alert">{{ errorMessage }}</p>
        <form class="composer" @submit.prevent="send">
          <label class="sr-only" for="chat-message">输入消息</label>
          <textarea
            id="chat-message"
            ref="inputRef"
            v-model="message"
            rows="1"
            maxlength="4000"
            placeholder="输入你的问题…"
            :disabled="sending"
            :aria-describedby="errorMessage ? 'chat-error composer-hint' : 'composer-hint'"
            @keydown.enter.exact.prevent="send"
          />
          <button v-if="sending" class="send-button send-button--stop" type="button" aria-label="停止生成" @click="stop">
            <IconBase name="stop" /> <span>停止</span>
          </button>
          <button v-else class="send-button" type="submit" aria-label="发送消息" :disabled="!message.trim()">
            <span>发送</span><IconBase name="send" />
          </button>
        </form>
        <p id="composer-hint" class="composer-hint">Enter 发送 · Shift + Enter 换行 · AI 内容仅供参考</p>
      </footer>
    </main>
  </div>
</template>
