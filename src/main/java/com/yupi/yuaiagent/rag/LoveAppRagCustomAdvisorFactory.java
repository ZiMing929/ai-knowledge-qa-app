package com.yupi.yuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;


/*
创建自定义的RAG文档增强检索顾问
*
*/

@Slf4j
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {

        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        //加入文档的检索器，来做文档的过滤操作，比如条件、相似度、返回文档数量等。  ----documentretriever

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)  // 过滤条件
                .similarityThreshold(0.5)    // 相似度阈值
                .topK(3)                    // 返回文档数量
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)    //用了上下文的检索查询器----RetrievalAugmentationAdvisor

                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())    //调用我们的上下文增强器！

                .build();


    }
}
