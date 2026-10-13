package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MyTokenTextSplitterTest {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Test
    void testSplitDocuments() {
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
        System.out.println("原始文档数量：" + documents.size());
        System.out.println("切片后的文档数量：" + splitDocuments.size());
        for (int i = 0; i < splitDocuments.size(); i++) System.out.println("第 " + (i + 1) + " 个切片：\n" + splitDocuments.get(i).getText() + "\n----------------------");
    }
}