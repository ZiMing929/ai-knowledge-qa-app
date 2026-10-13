package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest                          // 只要是测试类就必须要加入这个springboottest，同时注意大小写的情况。
class LoveAppDocumentLoaderTest {        //创建test测试的快捷键是:ctrl+shift+t

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Test
    void loadMarkdowns() {

    loveAppDocumentLoader.loadMarkdowns();

    }
}