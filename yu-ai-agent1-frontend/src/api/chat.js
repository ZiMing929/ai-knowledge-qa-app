import { apiClient } from './http.js'

/**
 * 浏览器版 Axios 的 XHR 适配器不会把响应体作为 ReadableStream 暴露出来。
 * 因此这里使用 Axios 统一生成 URL，再用 Fetch Reader 逐块解析 SSE。
 */
export async function streamChat({ path, params, signal, onOpen, onChunk }) {
  const url = apiClient.getUri({ url: path, params })
  const response = await fetch(url, {
    method: 'GET',
    headers: { Accept: 'text/event-stream' },
    signal,
  })

  if (!response.ok) {
    throw new Error(`服务响应异常（HTTP ${response.status}）`)
  }
  if (!response.body) {
    throw new Error('浏览器无法读取流式响应')
  }

  onOpen?.()
  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  const emitFrame = (frame) => {
    const data = frame
      .split(/\r?\n/)
      .filter((line) => line.startsWith('data:'))
      // Spring WebFlux 对 Flux<String> 输出为 `data:${chunk}`；冒号后的空格可能就是模型 token。
      // 不能按通用 SSE 示例无条件去掉首个空格，否则 `public class` 会被拼成 `publicclass`。
      .map((line) => line.slice(5))
      .join('\n')

    if (!data || data === '[DONE]') return

    let cleanData = data
    if (data.startsWith('"') && data.endsWith('"')) {
      try {
        cleanData = JSON.parse(data)
      } catch {
        // 非 JSON 字符串保持原样。
      }
    }
    onChunk(cleanData)
  }

  while (true) {
    const { value, done } = await reader.read()
    buffer += decoder.decode(value || new Uint8Array(), { stream: !done })

    const frames = buffer.split(/\r?\n\r?\n/)
    buffer = frames.pop() || ''
    frames.forEach(emitFrame)

    if (done) {
      if (buffer.trim()) {
        if (buffer.includes('data:')) emitFrame(buffer)
        else onChunk(buffer)
      }
      break
    }
  }
}

export const streamLoveChat = (options) =>
  streamChat({ path: '/ai/love_app/chat/sse', ...options })

export const streamManusChat = (options) =>
  streamChat({ path: '/ai/manus/chat', ...options })
