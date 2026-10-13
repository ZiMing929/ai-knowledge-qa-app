package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LoveAppVectorStoreConfigTest {

    @Resource(name = "loveAppVectorStore")
    private VectorStore loveAppVectorStore;

    @Test
    void testLoveAppVectorStore() {
        Assertions.assertNotNull(loveAppVectorStore);

        SearchRequest searchRequest = SearchRequest.builder().query("如何解决恋爱中的沟通问题？").topK(3).build();
        List<Document> documents = loveAppVectorStore.similaritySearch(searchRequest);

        Assertions.assertNotNull(documents);

        System.out.println("检索到的文档数量：" + documents.size());

        for (int i = 0; i < documents.size(); i++) {
            System.out.println("第 " + (i + 1) + " 个检索结果：");
            System.out.println("正文：" + documents.get(i).getText());             //这个只是数据的信息！
            System.out.println("元数据：" + documents.get(i).getMetadata());        //这个才是展示元数据的标签信息！
            System.out.println("----------------------");

        }
    }
}