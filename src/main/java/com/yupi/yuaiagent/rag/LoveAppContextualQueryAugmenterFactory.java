package com.yupi.yuaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;



/*这段代码是做上下文查询器的，当用户输入的数据不在我们的库当中或者输入为空时，就显示我们自己定义的内容！----主要的实现核心是:ContextualQueryAugmenter(上下文查询增强器！)
也相当于是一个异常的处理机制，提高用户的使用体验！
*/

public class LoveAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                
                """);

        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();


    }
}
