package com.yupi.yuaiagent.agent;

import com.yupi.yuaiagent.agent.model.AgentState;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具调用智能体。
 *
 * think()：调用大模型，决定是否执行工具。
 * act()：执行大模型选中的工具。
 *
 * 本次修复：
 * 1. 不再把 NEXT_STEP_PROMPT 当成用户消息追加。
 * 2. 避免模型忽略真实用户任务。
 * 3. 没有工具调用时直接返回模型回答。
 * 4. 正确保存工具调用后的消息历史。
 */
@Slf4j
@Getter
@Setter
public class ToolCallAgent extends ReActAgent {

    // 智能体可以使用的工具
    private ToolCallback[] availableTools = new ToolCallback[0];

    // 大模型调用参数
    private ChatOptions chatOptions;

    // 手动工具调用管理器
    private ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();

    // 保存模型当前轮次的完整响应
    private ChatResponse toolCallChatResponse;

    // 保存当前轮次的 Prompt，供工具执行使用
    private Prompt lastThinkPrompt;

    // 保存模型生成的文字
    private String lastThinkResult = "";

    /**
     * 模型不需要调用工具时，返回真正的回答。
     */
    @Override
    protected String getNoActionResult() {
        return lastThinkResult == null || lastThinkResult.isBlank() ? "任务已结束，但模型没有返回文字内容。" : lastThinkResult;
    }

    /**
     * 思考阶段。
     *
     * 调用模型，判断是否需要使用工具。
     */
    @Override
    public boolean think() {

        // 【修改1】仅使用真实用户消息及已有工具执行历史
        // 不再添加 UserMessage(nextStepPrompt)，防止模型跑题
        List<Message> messages = new ArrayList<>(getMessageList());

        // 构造模型请求
        Prompt prompt = new Prompt(messages, chatOptions);

        // 保存本轮 Prompt，供 act() 使用
        this.lastThinkPrompt = prompt;

        try {
            log.info("{} 开始调用大模型，消息数量：{}", getName(), messages.size());

            // 调用大模型，判断是否需要执行工具
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();

            // 检查模型响应
            if (chatResponse == null || chatResponse.getResult() == null) {
                throw new IllegalStateException("大模型没有返回有效响应");
            }

            // 保存模型响应
            this.toolCallChatResponse = chatResponse;

            // 获取模型返回的助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

            if (assistantMessage == null) {
                throw new IllegalStateException("大模型返回的助手消息为空");
            }

            // 保存模型文字回答
            String result = assistantMessage.getText();
            this.lastThinkResult = result == null ? "" : result;

            // 获取模型选择的工具
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();

            log.info("{} 模型回答：{}", getName(), lastThinkResult);
            log.info("{} 本轮工具调用数量：{}", getName(), toolCallList.size());

            // 打印工具名称，方便调试
            String toolNames = toolCallList.stream().map(AssistantMessage.ToolCall::name).collect(Collectors.joining(", "));
            log.info("{} 选择的工具：{}", getName(), toolNames);

            // 【修改2】模型没有选择工具，说明本轮任务可以结束
            if (toolCallList.isEmpty()) {

                // 保存最终回答
                getMessageList().add(assistantMessage);

                // 更新状态，结束执行循环
                setState(AgentState.FINISHED);

                return false;
            }

            // 模型选择了工具，交给 act() 执行
            return true;

        } catch (Exception e) {
            log.error("{} 思考过程出现异常", getName(), e);
            throw new IllegalStateException("智能体思考失败", e);
        }
    }

    /**
     * 行动阶段。
     *
     * 执行大模型选择的工具。
     */
    @Override
    public String act() {

        // 检查是否存在工具调用
        if (toolCallChatResponse == null || !toolCallChatResponse.hasToolCalls()) {
            return "本轮没有工具调用。";
        }

        // 检查工具管理器
        if (toolCallingManager == null) {
            throw new IllegalStateException("ToolCallingManager 尚未初始化");
        }

        // 使用 think() 阶段保存的 Prompt
        Prompt prompt = lastThinkPrompt != null ? lastThinkPrompt : new Prompt(getMessageList(), chatOptions);

        // 执行模型选中的工具
        ToolExecutionResult executionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);

        // 获取工具执行后的完整历史消息
        List<Message> conversationHistory = executionResult.conversationHistory();

        if (conversationHistory == null || conversationHistory.isEmpty()) {
            throw new IllegalStateException("工具执行后没有返回消息历史");
        }

        // 【修改3】更新历史消息，供下一轮模型调用使用
        setMessageList(new ArrayList<>(conversationHistory));

        // 获取最后一条消息
        Message lastMessage = conversationHistory.get(conversationHistory.size() - 1);

        if (!(lastMessage instanceof ToolResponseMessage toolResponseMessage)) {
            throw new IllegalStateException("工具执行结果中缺少 ToolResponseMessage");
        }

        // 整理工具执行结果
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 执行结果：" + response.responseData())
                .collect(Collectors.joining("\n"));

        // 检查是否调用终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));

        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
        }

        log.info("{} 工具执行完成", getName());

        return results;
    }

    /**
     * 清理临时数据。
     */
    @Override
    protected void cleanup() {
        super.cleanup();
        this.toolCallChatResponse = null;
        this.lastThinkPrompt = null;
        this.lastThinkResult = "";
    }
}
