package com.yupi.yuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j

public class LoveAppDocumentLoader {       //  恋爱大师----读取md文档的配置文件

    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(
            ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 resources/document 目录下的所有 Markdown 文件
     */
    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();

        try {
            Resource[] resources =
                    resourcePatternResolver.getResources("classpath:document/*.md");

            for (Resource resource : resources) {

                String fileName = resource.getFilename();
                String staus = fileName.substring(fileName.length() -6,fileName.length()-4);

                MarkdownDocumentReaderConfig config =
                        MarkdownDocumentReaderConfig.builder()
                                .withHorizontalRuleCreateDocument(true)
                                .withIncludeCodeBlock(false)
                                .withIncludeBlockquote(false)

                                .withAdditionalMetadata("filename", fileName)  //这是文件的名称！

                                .withAdditionalMetadata("staus",staus)     //文件的状态，已婚还是未婚、还是单身！

                                .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }

        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }
}