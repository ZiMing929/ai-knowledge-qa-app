package com.yupi.yuaiagent.app;      //java类属于哪一个包，大白话就是属于哪一个路径下的文件

import jakarta.annotation.Resource;      //import就是导入外部的类进来，方便我们的使用。
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Duration;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest      // 导入springboot的注解  测试的时候把 Spring Boot 项目启动起来，包括 Bean、配置文件、Spring AI、ChatModel 等。
                    //启动整个项目做测试。

class LoveAppTest {    //这个类是做loveapp的一个测试类，测试功能是否正常的。 -----这是我们定义的一个类，也就是对应的测试类！

    @Resource
    private LoveApp loveApp;   //定义了私有类型的变量名。

    @Test           //测试的注解类
    void testChat() {    //这是我们类的一个方法，用来测试的！   -----------下面就是业务逻辑的核心代码，具体的业务代码！

        String chatId = UUID.randomUUID().toString();        //uuid 是生成一个随机的chatid，要在类里面去提前的导入进来。
        // 第一轮
        String message = "你好，我是王子茗，我的瓦id叫坤坤两斤半，我有一个漂亮的女朋友叫张雯雯，我很爱她，我明天来上海找我玩，过中秋节和国庆。";

        String answer = loveApp.doChatByStream(message, chatId)
                .collectList()
                .map(chunks -> String.join("", chunks))
                .block(Duration.ofMinutes(3));   //通过id来做区分，决定是否读取前面的对话内容,本质是用之前写的loveapp类里面的方法dochat来做调用。

        Assertions.assertNotNull(answer);
//        loveApp.doChat(answer, chatId);

        // 第二轮
        message = "从湘潭坐火车14小时来找我，你觉得我俩是真爱吗，我真的特别喜欢她，她也爱我，准备研究生读完就订婚了！";
        answer = loveApp.doChatByStream(message, chatId).collectList()
                .map(chunks -> String.join("", chunks))
                .block(Duration.ofMinutes(3));
        Assertions.assertNotNull(answer);

        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，回忆一下。";
        answer = loveApp.doChatByStream(message, chatId).collectList()
                .map(chunks -> String.join("", chunks))
                .block(Duration.ofMinutes(3));
        Assertions.assertNotNull(answer);         /*如果answer没有值就会返回null，这是测试框架提供的模板，也是测试，当值为空的时候，能否正常返回对应的数据信息。*/

//        /*给 AI 发一句话 → AI 返回回答 → 检查 AI 有没有真的返回东西。*/  ---本质就是这段执行的流程和逻辑！
    }

    @Test
    void doChatWithReport() {            //对Loveapp里面新的方法进行测试，看一下效果如何
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员鱼皮，我想让另一半（编程导航）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);     //定义断言，如果为空，则报错。

    }

    @Test
    void doChatWithRag() {               // 测试RAG的功能实现的咋样！
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {            // 这个测试类还有问题，导出的pdf文件打不开，损坏了显示！
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份七夕约会计划PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);

    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);

    }
}
