package com.yupi.yuaiagent.demo.rag;    //包名就是他的文件目录的路径，从根目录一路往下走的

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MultiQueryExpanderDemo {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    /* 让RAG在检索问题的时候，不在以单一的问题来源做检索，从多个角度来找相关的文档进行检索，
       multiqueryexpander --就是多查询拓展，把一个问题拓展为多个问题，把大模型拓展为不同的问题去提问。
    */
    public List<Query> expand(String query) {
        MultiQueryExpander queryExpander = MultiQueryExpander.builder().chatClientBuilder(chatClientBuilder).numberOfQueries(3).build();
        List<Query> queries = queryExpander.expand(new Query("谁是王子茗大王呢？他喜欢谁？喜欢男生还是女生，还是张雯雯宝宝"));
        return queries;

    }
}