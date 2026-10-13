package com.yupi.yuaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.yupi.yuaiagent.demo.TestApiKey;
import java.util.HashMap;
import java.util.Map;


/**
 * HTTP 方式调用 AI  ，代码已经完成更新了，直接用GPT就可以完成了，不懂的代码报错直接发给GPT去做即可了。
 */

public class HttpAiInvoke {

    public static void main(String[] args) {

        // API 密钥
        String apiKey = TestApiKey.API_KEY;

        // 构建请求 URL
        String url =
                "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");

        // 设置请求体
        JSONObject requestBody = new JSONObject();

        // 指定模型
        requestBody.put("model", "qwen-plus");

        // 构造 input
        JSONObject input = new JSONObject();

        // 构造 messages
        JSONObject[] messages = new JSONObject[2];

        // 系统消息
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        messages[0] = systemMessage;

        // 用户消息
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "我叫坤坤两斤半，我喜欢玩火男和慕蝶，我比较喜欢自由的对枪，才能发挥我的枪法和思路！");
        messages[1] = userMessage;

        // 放入消息数组
        input.put("messages", messages);

        // 放入 input
        requestBody.put("input", input);

        // 构造参数
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");

        requestBody.put("parameters", parameters);

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .addHeaders(headers)
                .body(requestBody.toString())
                .execute();

        // 处理响应
        if (response.isOk()) {
            System.out.println("请求成功，响应内容：");
            System.out.println(response.body());
        } else {
            System.out.println("请求失败，状态码：" + response.getStatus());
            System.out.println("响应内容：" + response.body());

        }

    }

}