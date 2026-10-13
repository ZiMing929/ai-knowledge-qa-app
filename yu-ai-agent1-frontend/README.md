# Yu AI Nexus 前端

Vue 3 + Vite 的双 AI 应用前端，包含应用主页、AI 恋爱大师聊天页和 AI 超级智能体聊天页。

## 启动

```bash
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。开发环境会把 `/api` 代理到 `http://localhost:8123`，请先启动 Spring Boot 后端。

## 接口与流式策略

- 恋爱大师：`GET /api/ai/love_app/chat/sse?message=...&chatId=...`
- 超级智能体：`GET /api/ai/manus/chat?message=...`

两条接口都以 SSE 流读取，所有数据片段会追加到同一个 AI 气泡中。浏览器版 Axios 基于 XHR 时不能稳定暴露增量响应体，因此项目通过 Axios 实例统一处理基地址和参数序列化，通过 Fetch `ReadableStream` 负责 SSE 的逐块读取。

AI 回答使用 `markdown-it` 安全渲染（原始 HTML 默认关闭），支持标题、粗体、列表、引用、链接、表格、行内代码和代码块。表格与代码块在窄屏中会在气泡内部横向滚动，不会撑破页面。

## 跨域

本地开发使用 Vite 代理，因此不需要修改后端 CORS。若部署后前端与后端不同域，请在 Spring Boot 中允许前端域名：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

生产环境将 `.env.example` 复制为 `.env.production`，把 `VITE_API_BASE_URL` 改为真实后端地址；后端 `allowedOrigins` 也应同步改为前端正式域名，不建议使用 `*`。
