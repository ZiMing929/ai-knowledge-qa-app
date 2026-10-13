package com.yupi.yuaiagent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;


/**
 * 自定义 Re2 Advisor
 * 通过让模型重新阅读一次问题，辅助提升推理效果      -----当然每一个adviosr要执行一个特定的任务，要分工明确一点。
 */

public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

    /**
     * 在真正请求大模型之前，修改用户 Prompt
     */
    private ChatClientRequest before(ChatClientRequest request) {

        // 获取用户原始问题
        String originalQuestion = request.prompt()
                .getUserMessage()
                .getText();

        // 构造 Re-Reading 提示词
        String reReadingText = originalQuestion
                + "\n\nRead the question again: "
                + originalQuestion;

        // 用新的文本增强原来的用户消息
        Prompt newPrompt = request.prompt()
                .augmentUserMessage(reReadingText);

        // 在原 request 基础上替换 Prompt
        return request.mutate()
                .prompt(newPrompt)
                .build();
    }


    /**
     * 普通调用
     */
    @Override
    public ChatClientResponse adviseCall(   //接口的实现
            ChatClientRequest request,
            CallAdvisorChain chain) {

        ChatClientRequest newRequest = this.before(request);

        return chain.nextCall(newRequest);
    }


    /**
     * 流式调用
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(   //接口的实现
            ChatClientRequest request,
            StreamAdvisorChain chain) {

        ChatClientRequest newRequest = this.before(request);

        return chain.nextStream(newRequest);
    }


    /**
     * Advisor 执行顺序，    ----执行的顺序要注意！getorder方法
     */
    @Override
    public int getOrder() {
        return 0;
    }


    /**
     * Advisor 名称
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();

    }
}