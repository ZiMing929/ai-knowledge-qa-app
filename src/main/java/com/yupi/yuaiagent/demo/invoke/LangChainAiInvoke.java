package com.yupi.yuaiagent.demo.invoke;


import com.yupi.yuaiagent.demo.TestApiKey;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;



/*这里也是跟着鱼皮一样来做的，导入的 langchain4j 的框架来调用ai大模型。*/
/*
这里的chatlanguage model 和 spring ai 的chatmodel 是一个意思，只不过有不同的使用方法而已。
*/


public class LangChainAiInvoke {     //定义的类名称

    public static void main(String[] args) {    //主方法运行代码的入口
        ChatLanguageModel qwenModel = QwenChatModel.builder()      //这里的chatlanguagemodel就是和我们的chatmodel很类似的。
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-max")
                .build();
        String answer = qwenModel.chat("后天就是上海冠军赛了，你觉得edg能拿下冠军吗，最有可能拿冠军的会是哪一个队伍呢？");
        System.out.println(answer);

    }
}

/*
如果这里的chatlanguagemodel 和 qwenchatmodel 都是报错的话，就可以去直接导入对应的类，Import进去就可以了，不用问gpt老师！
*/


