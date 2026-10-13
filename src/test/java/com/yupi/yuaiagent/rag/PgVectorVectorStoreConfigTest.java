package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


//这里是做向量数据库PgVector的测试的！
@SpringBootTest
@ActiveProfiles("local")
public class PgVectorVectorStoreConfigTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void test() {

        List<Document> documents = List.of(
                new Document(
                        "鱼皮的编程导航有什么用呢？跟他一起学习编程，动手练习项目！",
                        Map.of("meta1", "meta1")
                ),
                new Document(
                        "鱼皮的项目很好，马上准备找实习了"
                ),
                new Document(
                        "我要努力学习编程，准备找实习！",
                        Map.of("meta2", "meta2")
                )
        );

        // 添加文档
        pgVectorVectorStore.add(documents);

        // 相似度查询
        List<Document> results =
                pgVectorVectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("怎么学习编程呢？")
                                .topK(3)
                                .build()
                );

        Assertions.assertNotNull(results);

        results.forEach(System.out::println);
    }
}

