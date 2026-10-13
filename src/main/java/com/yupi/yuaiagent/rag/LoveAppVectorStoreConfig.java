package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;      //导包的时候，别导入错了，优先都是以springai为主的！--springframework.ai为主的！---不然会报错，不兼容！
import java.util.List;


/**
 * 恋爱大师 VectorStore 配置类
 */
@Configuration       //“这个类不是普通业务类，这里面主要负责创建和配置 Bean，会优先去检查有没有bean这个文件！
public class LoveAppVectorStoreConfig {

    /**
     * 注入我们自己写的 Markdown 文档加载器
     */
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;       //想在这个文件用关键词的标签信息，就需要加入这个resource的注解类。

    /**
     * 创建 VectorStore Bean
     */
    @Bean            //Spring，请执行这个方法，把这个方法最终返回的对象交给你管理。---后面我们想去使用vectorstore的时候，就不用去new一个对象了，直接去使用即可。
    public VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {

        // 1. 创建一个内存型向量数据库   ---VectorStore 存的不是普通文本，而是文本对应的向量。---然后找到语义相互比较接近的，做为匹配成功的结果。
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        // 2. 加载 Markdown 文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();

        //3. 自主切分文档，把文档做切片的处理！
      //  List<Document> splitDocuments=myTokenTextSplitter.splitCustomized(documents);

        // 4、加入文档的元数据标签信息进来！
        List<Document> enrichDocument = myKeywordEnricher.enrichDocuments(documents);

        // 5、 把文档加入向量数据库
        simpleVectorStore.add(enrichDocument);

        // 6、 返回 VectorStore
        return simpleVectorStore;         // 这里是把md文档转换为对应的向量数据库的形式！

    }

}