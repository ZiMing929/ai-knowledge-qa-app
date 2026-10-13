package com.yupi.yuaiagent.tools;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

/*
    把这些普通 Java Tool 对象统一转换成 Spring AI 能识别的 ToolCallback[]，然后注册成一个 Spring Bean，后续交给 ChatClient / 大模型使用。
    TOOL---工具说明，Toolparam--参数说明，ToolCallback[]---告诉ai，什么时候去执行相应的工具。

    @Tool 负责声明工具，ToolCallbacks.from() 负责转换工具。

    ToolCallback[] 负责把工具交给 Spring AI 使用。
*/

    @Bean  //bean做容器管理，管理我们后面创建的对象---把对象交给chatclient大模型去执行调用处理！------可以直接在Loveapp里面用resource去导入这些对象，便于后续的管理和维护！
    public ToolCallback[] allTools() {      //核心就是这个toolcallback的工具，告诉ai，什么时候去用哪些工具，而这个allTools---就是一个工具的集合！

        FileOperationTool fileOperationTool = new FileOperationTool();

        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);

        WebScrapingTool webScrapingTool = new WebScrapingTool();

        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();

        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();

        TerminateTool terminateTool = new TerminateTool();   //新加的---中止工具，根据这个TerminateTool来加的

        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();

        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool       // 补充terminateTool来做的！

/*
                allTools() 返回的这个 ToolCallback[] 对象，不只是一个普通 Java 对象，而是把它注册到 Spring 容器里，让 Spring 帮我们管理。----这就是Bean的管理！
                这样我在loveapp里面就不用去重新一个个对象的new了，很麻烦，直接用这个resource的注解，即可完成一键的注入，很方便了！
                因为这些重复的new工作在这个文件下，就已经全部完成了，不需要在到loveapp里面去重复执行一遍，很麻烦，也影响代码的观感！

                Bean----创建/注册东西到 Spring 容器。
                Resource---从 Spring 容器里面把已经存在的东西拿过来给当前类使用。
                @Bean 是“放进去”，@Resource 是“拿出来用”。

*/

        );

    }

}
