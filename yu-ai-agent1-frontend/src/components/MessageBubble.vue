<script setup>
import { computed, ref } from 'vue'
import MarkdownIt from 'markdown-it'
import IconBase from './IconBase.vue'

const markdown = new MarkdownIt({
  html: false,
  breaks: true,
  linkify: true,
  typographer: true,
})

const defaultLinkOpen = markdown.renderer.rules.link_open
  || ((tokens, index, options, env, self) => self.renderToken(tokens, index, options))

markdown.renderer.rules.link_open = (tokens, index, options, env, self) => {
  tokens[index].attrSet('target', '_blank')
  tokens[index].attrSet('rel', 'noopener noreferrer')
  return defaultLinkOpen(tokens, index, options, env, self)
}

markdown.renderer.rules.table_open = () =>
  '<div class="markdown-table-wrap" tabindex="0" role="region" aria-label="AI 回答表格，可横向滚动"><table>'
markdown.renderer.rules.table_close = () => '</table></div>'

const normalizeAiMarkdown = (source) => {
  let insideFence = false
  return source
    .split('\n')
    .map((line) => {
      if (/^\s*(```|~~~)/.test(line)) {
        insideFence = !insideFence
        return line
      }
      if (insideFence) return line

      return line
        // 兼容模型偶尔输出的 `##标题`。
        .replace(/^(#{1,6})([^\s#])/, '$1 $2')
        // 兼容结束标记前多出空格的 `**加粗 **`。
        .replace(/\*\*([^*]+?)\s+\*\*/g, '**$1**')
    })
    .join('\n')
}

const props = defineProps({
  message: { type: Object, required: true },
  accent: { type: String, default: 'love' },
})
const copied = ref(false)
const renderedContent = computed(() => markdown.render(normalizeAiMarkdown(props.message.content || '')))

const copyMessage = async () => {
  await navigator.clipboard.writeText(props.message.content)
  copied.value = true
  window.setTimeout(() => (copied.value = false), 1600)
}
</script>

<template>
  <article class="message" :class="[`message--${message.role}`, `message--${accent}`]">
    <div class="message__avatar" aria-hidden="true">
      <IconBase v-if="message.role === 'assistant'" :name="accent === 'love' ? 'heart' : 'spark'" :size="19" />
      <span v-else>YOU</span>
    </div>
    <div class="message__body">
      <div class="message__meta">
        <span>{{ message.role === 'assistant' ? (accent === 'love' ? '恋爱大师' : '超级智能体') : '你' }}</span>
        <time :datetime="message.isoTime">{{ message.time }}</time>
      </div>
      <div class="message__bubble">
        <div
          v-if="message.role === 'assistant'"
          class="markdown-body"
          v-html="renderedContent"
        />
        <p v-else>{{ message.content }}</p>
        <span v-if="message.streaming" class="stream-caret" aria-label="正在生成" />
        <button
          v-if="message.role === 'assistant' && !message.streaming && message.content"
          class="message__copy"
          type="button"
          :aria-label="copied ? '已复制回答' : '复制回答'"
          @click="copyMessage"
        >
          <IconBase name="copy" :size="15" />
          {{ copied ? '已复制' : '复制' }}
        </button>
      </div>
    </div>
  </article>
</template>
