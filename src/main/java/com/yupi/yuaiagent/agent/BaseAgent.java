package com.yupi.yuaiagent.agent;

import com.yupi.yuaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 智能体基础类。
 *
 * 负责：
 * 1. 智能体状态管理。
 * 2. 消息历史管理。
 * 3. 最大执行步数控制。
 * 4. SSE 流式推送执行过程。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 智能体名称
    private String name;

    // 系统提示词
    private String systemPrompt;

    // 下一步提示词
    private String nextStepPrompt;

    // 智能体状态
    private AgentState state = AgentState.IDLE;

    // 最大执行步数
    private int maxSteps = 10;

    // 当前执行步数
    private int currentStep = 0;

    // 大模型客户端
    private ChatClient chatClient;

    // 消息历史
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行智能体并通过 SSE 返回结果。
     *
     * 注意：
     * 这里是按 ReAct 步骤推送，
     * 并不是大模型逐字流式输出。
     */
    public SseEmitter runStream(String userPrompt) {

        // 创建 SSE 连接，5 分钟超时
        SseEmitter emitter = new SseEmitter(300000L);

        // 检查用户输入
        if (userPrompt == null || userPrompt.isBlank()) {
            try {
                emitter.send("错误：请输入有效的问题。");
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // 异步执行智能体，避免阻塞 HTTP 请求线程
        CompletableFuture.runAsync(() -> {

            try {
                // 检查当前智能体状态
                if (state != AgentState.IDLE) {
                    emitter.send("错误：智能体当前不是空闲状态。");
                    emitter.complete();
                    return;
                }

                // 更新运行状态
                state = AgentState.RUNNING;

                // 清空旧消息，当前每次请求使用新的 YuManus 实例
                messageList.clear();

                // 添加用户问题
                messageList.add(new UserMessage(userPrompt));

                // 重置步骤
                currentStep = 0;

                // 发送开始通知
                emitter.send("智能体已收到任务，正在分析问题...");

                // 执行 ReAct 循环
                for (int i = 0; i < maxSteps && state == AgentState.RUNNING; i++) {

                    int stepNumber = i + 1;
                    currentStep = stepNumber;

                    log.info("{} 正在执行第 {}/{} 步", name, stepNumber, maxSteps);

                    // 在模型调用之前发送进度，避免前端没有反馈
                    emitter.send("正在执行第 " + stepNumber + "/" + maxSteps + " 步，请稍候...");

                    // 执行当前步骤
                    String stepResult = step();

                    // 发送步骤结果
                    emitter.send("第 " + stepNumber + " 步：" + stepResult);
                }

                // 达到最大执行步数
                if (state == AgentState.RUNNING && currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    emitter.send("智能体已达到最大执行步数：" + maxSteps + "，本次任务停止。");
                }

                // 结束 SSE
                emitter.complete();

            } catch (Exception e) {

                state = AgentState.ERROR;
                log.error("{} 执行失败", name, e);

                try {
                    emitter.send("智能体执行失败，请查看后端控制台日志。");
                    emitter.complete();
                } catch (Exception sendException) {
                    emitter.completeWithError(sendException);
                }

            } finally {
                // 清理状态和历史消息
                cleanup();
            }
        });

        // 超时回调
        emitter.onTimeout(() -> {
            log.warn("{} SSE 连接超时", name);
            emitter.complete();
        });

        // 完成回调
        emitter.onCompletion(() -> log.info("{} SSE 连接已结束", name));

        return emitter;
    }

    /**
     * 子类实现具体的 ReAct 步骤。
     */
    public abstract String step();

    /**
     * 清理本次任务。
     */
    protected void cleanup() {
        this.state = AgentState.IDLE;
        this.currentStep = 0;
        this.messageList.clear();
        log.info("{} 智能体状态已清理", name);
    }
}
