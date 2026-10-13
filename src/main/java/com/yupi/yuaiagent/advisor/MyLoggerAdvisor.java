package com.yupi.yuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;


/**
 * 自定义日志 Advisor
 * 打印用户请求和 AI 回复    ------------1、本质上就是给 AI 对话加一个“拦截器”。-----也是我自己定义的一个拦截器，常规的官方文档里面的拦截器，输出的内容太多了，于是就去自己定义了一个拦截器。
 *                        ------------2、拦截器的本质：在真正调用 AI 之前和之后插入额外逻辑，插入我们自己想要的东西进去！
 *
 *                        ------------3、当然如果只处理一个ai回复，可以不写流式的处理，--最好是俩个都写，都去实现。
 */

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {    /*---这里是自定义的advisor，当然这里改成了calladvisor和streamadvisor*/

    /**
     * Advisor 名称，    CallAdvisor --处理普通的ai请求，然后StreamAdvisor ---处理流式的ai请求！，那类里面写了要去实现，如果没有加override去重写这个实现类的话，就会报错。
     */

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Advisor 执行顺序
     * 数字越小，优先级越高
     */

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 请求发送给 AI 之前执行
     */
    private void logRequest(ChatClientRequest request) {

        log.info("AI Request: {}", request.prompt().getUserMessage().getText());       //这里就是用到了PromptTemplate的方法，模板的运用，也就是占位符！

    }


    /**
     * AI 返回结果之后执行
     */
    private void logResponse(ChatClientResponse response) {

        if (response != null
                && response.chatResponse() != null
                && response.chatResponse().getResult() != null) {

            String text = response.chatResponse()
                    .getResult()      //取得模型生成的第一条结果！
                    .getOutput()
                    .getText();      //这三个东西是连在一起的，getresult、getoutput、gettext.---三个是在一起的。

            log.info("AI Response: {}", text);    //这里也是占位符的思想！
        }
    }


    /**
     * 普通非流式调用  ---普通的ai调用，带有stream的是流式调用！
     */

    @Override    //我是在重写接口要求的方法，override--重写接口实现类

    public ChatClientResponse adviseCall(   //CallAdvisor---对应的advisecall的实现！
            ChatClientRequest request,
            CallAdvisorChain chain) {

        // 1. AI 调用之前记录请求日志
        logRequest(request);

        // 2. 调用下一个 Advisor / 最终调用 AI 模型
        ChatClientResponse response = chain.nextCall(request);

        // 3. AI 返回之后记录响应日志
        logResponse(response);

        // 4. 返回结果
        return response;
    }


    /**
     * 流式调用  adviseStream---对应的StreamAdvisor的实现！
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(
            ChatClientRequest request,
            StreamAdvisorChain chain) {

        // 1. 记录用户请求
        logRequest(request);

        // 2. 获取 AI 流式响应
        Flux<ChatClientResponse> responses =
                chain.nextStream(request);

        // 3. 把流式响应聚合后打印完整 AI 回复
        return new ChatClientMessageAggregator()
                .aggregateChatClientResponse(
                        responses,
                        this::logResponse
                );
    }
}