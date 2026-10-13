package com.yupi.yuaiagent.demo.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;



/**
 * MultiQueryExpander 多查询扩展测试类
 * 作用：测试能否把用户原始的一个问题，通过大模型扩展成多个不同表达方式的问题。
 */


@SpringBootTest
class MultiQueryExpanderDemoTest {

    /**
     * 注入我们自己编写的 MultiQueryExpanderDemo
     * @Resource 会从 Spring 容器中找到对应的 Bean，并赋值给 multiQueryExpanderDemo。
     */
    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;


    /**
     * 测试多查询扩展功能。
     * 原始问题："王子茗是谁，他喜欢谁呢？"
     * 经过 MultiQueryExpander 后，会让大模型生成多个意思相近、表达方式不同的 Query。
     */
    @Test
    void expand() {

        // 1. 调用我们自己写的 expand() 方法，把一个原始问题扩展成多个 Query。
        List<Query> queries = multiQueryExpanderDemo.expand("王子茗是谁，他喜欢谁呢？");

        // 2. 判断返回的 List 不能为 null，如果是 null，则说明扩展过程出现问题。
        Assertions.assertNotNull(queries);          //可以在这里打上对应的断点！

        // 3. 判断扩展后的 Query 列表不能为空。
        Assertions.assertFalse(queries.isEmpty());

        // 4. 打印生成了多少个查询问题，方便我们观察 MultiQueryExpander 的实际效果。
        System.out.println("扩展后的 Query 数量：" + queries.size());

        // 5. 遍历所有 Query，把每一个扩展后的问题打印出来。
        for (int i = 0; i < queries.size(); i++) System.out.println("第 " + (i + 1) + " 个 Query：" + queries.get(i).text());

    }

}

