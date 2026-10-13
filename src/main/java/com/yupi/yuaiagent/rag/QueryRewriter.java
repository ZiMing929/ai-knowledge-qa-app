package com.yupi.yuaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;


    public QueryRewriter(ChatModel dashscopeChatModel) {    // 我们的查询重写的构造方法！

        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 创建查询重写转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();

    }

    public String doQueryRewrite(String prompt) {       //自己的方法，需要传入返回值

        Query query = new Query(prompt);

        // 执行查询重写
        Query transformedQuery = queryTransformer.transform(query);

        // 输出重写后的查询
        return transformedQuery.text();

    }
}
