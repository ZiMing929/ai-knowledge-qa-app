package com.yupi.yuaiagent.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;


/*
这里是pgvectore的向量存储配置！将文件导入到这里，并且用向量来进行存储！，编写了这个配置累，所以要对这个配置类进行对应的测试！
*/



@Configuration                    //临时注释！！！，后面改回来！
public class PgVectorVectorStoreConfig {


/*
    下面代码都是官方文档下粘贴下来的文档
*/
    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1024)                    // 不要盲目设置
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(false)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
                                    //我们的表名称就是vector_store，后续可以在这个云平台查看到表的信息和对应的数据内容。

        return vectorStore;         // 最后返回一个vectorstore的对象。

    }

}

