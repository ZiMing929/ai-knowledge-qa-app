package com.yupi.yuaiagent.demo.invoke;

import dev.langchain4j.memory.ChatMemory;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型 ，比用SDK和Http简洁了太多，代码量很少，且很简洁和方便。
 */

@Component
public class SpringAiAiInvoke implements CommandLineRunner {     //实现一个接口

  @Resource
  private ChatModel dashscopeChatModel;      //这里就是定义一个dashscopeChatModel 的对象去调用大模型， chatmodel是固定写法，代表后面用哪个大模型的名称

  @Override
  public void run(String... args) throws Exception {

//    MessageChatMemoryAdvisor   chatmemory
//    ChatMemory  ---负责历史对话的存储

    AssistantMessage assistantMessage = dashscopeChatModel
            .call(new Prompt("你好，我是王子茗，一名上海海事大学的研二学生，我会唱跳rap和篮球，还有一个漂亮的女朋友！！"))
            .getResult()
            .getOutput();

    System.out.println(assistantMessage.getText());   //输出对应的内容到控制台当中

//     System.out.println("Spring Boot 启动成功！");





  }

}