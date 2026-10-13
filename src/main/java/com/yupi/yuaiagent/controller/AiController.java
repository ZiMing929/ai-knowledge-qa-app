package com.yupi.yuaiagent.controller;

import com.yupi.yuaiagent.agent.YuManus;
import com.yupi.yuaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * AI 接口控制器。
 *
 * 1. 恋爱大师：调用 LoveApp。
 * 2. 超级智能体：调用 YuManus。
 *
 * Spring Boot 配置了 context-path: /api，
 * 因此实际接口路径需要加上 /api 前缀。
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    // 注入 AI 恋爱大师应用
    @Resource
    private LoveApp loveApp;

    // 注入项目中已经注册的所有 AI 工具
    @Resource
    private ToolCallback[] allTools;

    // 注入阿里云百炼大模型
    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * AI 恋爱大师：流式聊天。
     *
     * 实际接口：
     * GET /api/ai/love_app/chat/sse?message=你好&chatId=123
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(@RequestParam String message, @RequestParam String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * YuManus 超级智能体：SSE 流式输出。
     *
     * 实际接口：
     * GET /api/ai/manus/chat?message=你好
     *
     * 每次请求创建新的 YuManus，避免多个任务共用运行状态。
     */
    @GetMapping(value = "/manus/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter doChatWithManus(@RequestParam String message) {
        YuManus yuManus = new YuManus(allTools, dashscopeChatModel);
        return yuManus.runStream(message);
    }
}
