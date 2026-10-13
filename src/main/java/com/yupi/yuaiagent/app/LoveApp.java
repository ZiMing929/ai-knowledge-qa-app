package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.advisor.ReReadingAdvisor;
import com.yupi.yuaiagent.chatmemory.FileBasedChatMemory;
import com.yupi.yuaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.yupi.yuaiagent.rag.MyTokenTextSplitter;
import com.yupi.yuaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import reactor.core.publisher.Flux;
import java.util.List;



/**
 * 恋爱大师 AI 应用 （我加入了详细的注解，逐行代码的，便于我去理解。）
 */

@Component          //component是sping的注解 ，不需要自己去new，直接启动就行 ，这个注解就是引入spring的bean注入的意思
                    // 这个类你来创建、管理，以后别人需要它时，你负责提供。 Bean 本质上就是一个普通 Java 对象，只不过这个对象由 Spring 创建和管理。

//那为什么我们需要bean呢，因为一个项目会有很多的类和对象有依赖的关系，如果自己不断的去new的话，就会很乱，很麻烦，所以我们需要注解类，帮助我们做这些事情。
//sping 相当于一个系统框架，把我们所需要的内容全部导入进去了，就很方便！


@Slf4j              // lombook 的插件，自己去创建一个log,就不需要去自己new一个了。
public class LoveApp {      //这里是我们定义的类名称，也是一个构造方法！

    /**
     * ChatClient：
     * 对 ChatModel 的进一步封装，        ---client 比model更加的牛逼，功能也更加的强大。 然后后续的开发都会去围绕着这个client来做！
     * 支持 System Prompt、Advisor、多轮对话记忆等能力。
     */

    private final ChatClient chatClient;     //类的成员对象
    /**
     * 系统提示词：
     * 定义 AI 的角色、行为和回答方式。 这里system是系统的录入数据，用户是看不到，ai每次生成对话之前，都要根据这个角色去分析。message才是用户真正输入的数据信息。
     */

    private static final String SYSTEM_PROMPT =    //注入ai的系统提示词！用户无法看见
            """
            你是一名专业、温暖、理性的恋爱与情感关系顾问。

            你的主要职责：
            1. 帮助用户分析单身、恋爱、婚姻等不同阶段的情感问题。
            2. 单身用户：帮助分析社交、追求心仪对象、聊天沟通等问题。
            3. 恋爱用户：帮助分析沟通矛盾、习惯差异、信任和情绪问题。
            4. 已婚用户：帮助分析家庭责任、伴侣沟通、亲属关系等问题。
            5. 引导用户描述事情经过、对方反应以及自己的真实想法。
            6. 不武断替用户判断对方的真实想法，应区分事实与推测。
            7. 最终提供具体、尊重双方边界、可执行的建议。
            """;

    /**
     * 构造方法1。
     * Spring 会自动把 DashScope 对应的 ChatModel 注入进来。 ----这里的LoveApp就是一个注解类，就是一个构造器的注入。
     */
    public LoveApp(ChatModel dashscopeChatModel) {

        /*
         * 创建一个内存型对话记忆。
         *
         * MessageWindowChatMemory 会保存最近若干条消息，      //运行期间的对话窗口记忆，如果需要永久的保存，需要数据库类型的定义方式：ChatMemoryRepository
         * 默认最大窗口是 20 条。
         */

            // 初始化基于文件的对话记忆   ，就是 new 一个filebasedchatmemory，然后把数据值传给了chatmemory！
            String fileDir = System.getProperty("user.dir") + "/chat-memory";
            ChatMemory chatMemory = new FileBasedChatMemory(fileDir);          //不能同时定义两个相同的变量出来，不然会报错的！


//            ChatMemory chatMemory = MessageWindowChatMemory.builder()        下面注释掉了，不然就会出现报错的情况！
//                    .maxMessages(20)
//                    .build();

        /*
         * 创建 ChatClient。
         */
             this.chatClient = ChatClient.builder(dashscopeChatModel)

                // 给所有对话设置默认系统提示词
                .defaultSystem(SYSTEM_PROMPT)      //默认方法，在所有的对话里面都可以去使用的！


                     // .defaultTools()

                /*
                 * 添加聊天记忆 Advisor。
                 *
                 * Advisor 可以理解成 ChatClient 的“拦截器”：
                 * 在真正调用模型之前和之后插入额外逻辑。
                 *
                 * MessageChatMemoryAdvisor 的作用：
                 * 发送问题前读取历史聊天记录；
                 * 模型回答后把本轮对话保存到 ChatMemory。
                 */

                .defaultAdvisors(        // 默认的拦截器，每次ai读取和生成数据的时候，都要先去看这个。在每次请求大模型的时候，都会调用拦截器来做处理。

                        MessageChatMemoryAdvisor.builder(chatMemory).build(),      //---存储的记忆都在这个chatmemory里面了！

                      // new SimpleLoggerAdvisor()      //官方自定义的拦截器


                        new MyLoggerAdvisor()            //这是我们自己--自定义的拦截器，在ai创建和结束的时候注入我们自己的逻辑进去。
                        //然后这里就可以去--导入到loveapptest里面去了
                        // new ReReadingAdvisor()           //自定义的拦截器，把信息重复喂给ai去执行对应的操作。---输入的token会翻倍，导致成本增多
                        //所以这个ReReading要谨慎开启，根据项目的实际需求来做处理，不能瞎开，如果为了更好的完成项目，那完全可以开启的。

                )      //这里不能加分号，代表语句的结束！

                .build();

    }



    /**
     * 与 AI 进行对话。----基于dochat的普通方法做了改进，改成对应的流式输出的形式！
     * -----dochat---只是一个普通的方法，区分于前面的loveapp的构造方法，因为他有对应的返回值类型！
     *
     * @param message 用户发送的消息
     * @param chatId  当前会话 ID
     * @return AI 返回的文本
     */


    /**
     * AI 流式聊天方法（Flux 响应式输出）。-----flux 是 做流式输出的！
     *
     * @param message 用户本轮输入的问题
     * @param chatId 当前会话 ID，用来区分不同用户或不同对话
     * @return Flux<String>：AI 逐步生成的文本片段
     */
    public Flux<String> doChatByStream(String message, String chatId) {

        // 1. 创建 AI 请求，传入用户问题，并设置当前会话 ID
        // 2. stream()：开启流式调用，接收模型持续生成的响应
        // 3. content()：从响应中提取文本，返回 Flux<String>
        return chatClient.prompt().user(message).advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)).stream().content()
                .doOnNext(content -> log.info("AI 流式片段：{}", content)) // 每收到一个片段就记录日志
                .doOnError(error -> log.error("AI 流式调用失败", error)); // 发生异常时记录日志
    }




    /*
    定义一个lovereport类，后面的方法里面会返回这个类。
*/
    record LoveReport(String title, List<String> suggestions) {
    }


    /**
     * 一、AI恋爱报告的输出，体验结构化输出的功能！ ----和前面的dochat实现的功能类似。
     *
     * @param message 用户发送的消息
     * @param chatId  当前会话 ID
     * @return AI 返回的文本
     */
    public LoveReport doChatWithReport(String message, String chatId) {            //注意这个lovereport的方法不能放到外面去，会报错的！----要放在类的里面去书写普通方法！
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec ->
                        spec.param(
                                ChatMemory.CONVERSATION_ID,
                                chatId
                        )
                )
                .call()
                .entity(LoveReport.class);          //将输出的文本 转换 为 json的输出格式，结构化类型转换！

        log.info("loveReport: {}", loveReport);
        return loveReport;     //返回我们自定义的数据类型---lovereport！

    }




/*
    就是要在其他的类里面用我们已经创建好的方法，就需要用这个resource的注解去引入
    让 Spring 容器把已经创建好的 Bean 自动注入到这两个字段里，不用自己 new。这是两个成员变量字段，常规开发应该放到类的顶部
*/
    @Resource(name = "loveAppVectorStore")
    private VectorStore loveAppVectorStore;

    @Resource(name = "loveAppRagCloudAdvisor")
    private Advisor loveAppRagCloudAdvisor;

    @Resource(name = "pgVectorVectorStore")
    private VectorStore pgVectorVectorStore;          // 注意名字的拼写不要出现错误！不然测试会出现错误的！

    @Resource
    private QueryRewriter queryRewriter;     // 导入查询重写器，做用户信息的重写操作

//    @Resource
//    private LoveAppRagCustomAdvisorFactory loveAppRagCustomAdvisorFactory;



    /**
     * 二、使用 RAG 知识库进行问答
     */
    public String doChatWithRag(String message, String chatId) {

        String rewrittenMessages=queryRewriter.doQueryRewrite(message);      // queryrewriter---就叫做查询重写器
        //把messages里面的数据从右往左处理，最后传给rewrittenmessages里面，调用chatclient给大模型，把重写后的提示词给他，生成更加精确的结果。

        ChatResponse chatResponse = chatClient
                .prompt()

                //.user(message) ---普通的用户对话数据的存入到message当中

                .user(rewrittenMessages)    //这里是重写提示词的用户数据，传入到rewritten当中去

                // 指定当前对话的 conversationId
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID,chatId))    // chatmemory属于是你的对话数量情况，比如ai历史的对话记录这些东西

                // 打印 AI 请求 / 响应日志
                .advisors(new MyLoggerAdvisor())

                 // 使用向量数据库进行 RAG 检索，把用户的信息转入到查询增强的拦截器里面去。---然后调用到vector向量数据库里面！
                // QuestionAnswerAdvisor-----每次回答之前，先去我的恋爱知识库搜索相关知识，然后再让 AI 回答，这样ai就不会出现幻觉了！

                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())


                //应用RAG的云服务进行检索增强服务。
                // .advisors(loveAppRagCloudAdvisor)

                // 用的pgvector做的向量数据的存储！-----新版本用的是builder，而不是build！
                //.advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())

                //调用文档过滤器+上下文增强器来做RAG的处理操作
                //.advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "单身"))

                // 调用大模型
                .call()

                // 获取完整响应
                .chatResponse();

        if (chatResponse == null || chatResponse.getResult() == null) {
            log.error("RAG AI 返回结果为空");
            return "抱歉，本次回答失败，请稍后再试。";
        }

        String content = chatResponse
                .getResult()
                .getOutput()
                .getText();
        log.info("content: {}", content);
        return content;
    }



    @Resource
    private ToolCallback[] allTools;   //因为有Bean来管理对象，所以我们直接引入创建的对象，即可，用resource的注解，就不用在这里一个个的去重新new一个对象了，很慢！

    /**
     * 三、调用AI工具去做处理！
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))

                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;

    }



/*
    AI恋爱报告，调用mcp的服务！
*/
    @Resource
    private ToolCallbackProvider toolCallbackProvider;     //启动mcp的服务，把内容传给tools，然后交给ai客户端去执行。

  /*  toolcallbackProvider---“MCP 工具供应商 / 工具提供器”。它自己负责告诉 Spring AI：
      “我这里有哪些 MCP 工具可以给模型用。”*/

    public String doChatWithMcp(String message, String chatId) {
        //prompt和 message是用户发送的请求数据和id
        ChatResponse response = chatClient.prompt().user(message).advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
        //开启日志，记录信息数据
                .advisors(new MyLoggerAdvisor())

                //把 MCP 提供的工具交给当前这次 AI 请求。
                .toolCallbacks(toolCallbackProvider)

                .call()

                .chatResponse();

        String content = response.getResult().getOutput().getText();

        log.info("content: {}", content);

        return content;
    }

}


