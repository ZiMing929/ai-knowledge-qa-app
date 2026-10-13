package com.yupi.yuaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSearchToolTest {

/*
    @Value 是 Spring 提供的注解，作用是把配置值注入变量；${search-api.api-key} 是一个配置占位符，意思是“请帮我寻找名为 search-api.api-key 的配置”。
*/
    @Value("${search-api.api-key}")
    private String searchApiKey;         //最后把传入的api-key数据--放入到searchApiKey里面去，这个是一个java的变量！

    @Test
    public void testSearchWeb() {

        WebSearchTool tool = new WebSearchTool(searchApiKey);    //把我们的值进行传入
        String query = "程序员鱼皮编程导航 codefather.cn";         //query是我们要检索的内容，后面的result是大模型返回来的结果，assertnotnull--判断返回的内容是否为空！
        String result = tool.searchWeb(query);
        assertNotNull(result);

    }
}
