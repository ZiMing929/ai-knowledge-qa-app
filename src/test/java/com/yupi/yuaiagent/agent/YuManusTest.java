package com.yupi.yuaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class YuManusTest {

    @Resource
    private YuManus yuManus;

    @Test
    void run() {
        String userPrompt = """
            我的另一半居住在上海静安区，请帮我找到5公里内合适的约会地点，
            并结合地图路线图片，制定一份详细的约会计划，
            从上海海事大学临港校区出发，到静安去玩，
            必去的地点是静安大悦城，并以PDF格式输出。
            """;

        // 调用流式方法，返回的是 SSE 连接对象，不再是 String
        SseEmitter emitter = yuManus.runStream(userPrompt);

        // 检查是否成功创建 SSE 连接对象
        Assertions.assertNotNull(emitter);
    }

}
