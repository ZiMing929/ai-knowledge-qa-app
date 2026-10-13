package com.yupi.yuaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/*
给 AI 提供一个“百度搜索工具”。AI 遇到自己需要联网查询的问题时，可以调用 searchWeb()，通过 SearchAPI 搜索百度，再把前 5 条搜索结果返回给大模型。
*/
public class WebSearchTool {   //定义一个联网检索的类！

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";    //保存“我要向哪个服务器发送搜索请求”。

    private final String apiKey;

    public WebSearchTool(String apiKey) {     //方法名与类名完全相同，而且没有返回类型。---这个是一个构造方法！
        this.apiKey = apiKey;
    }


    @Tool(description = "Search for information from Baidu Search Engine")     //告诉ai大模型---从百度搜索引擎搜索信息

    public String searchWeb(

            @ToolParam(description = "Search query keyword") String query) {    // query 这个参数代表“搜索关键词，告诉ai，检索关键词的意思是什么？

        Map<String, Object> paramMap = new HashMap<>();     // 通过键值对的方式来保存数据！
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");

        try {     //try-catch -去捕获异常处理机制！

            String response = HttpUtil.get(SEARCH_API_URL, paramMap);    //把联网请求的结果返回给response,

            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);   //解析字符

            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");

            List<Object> objects = organicResults.subList(0, 5);   // 取出联网检索的前面五条的数据！

            // 拼接搜索结果为字符串
            String result = objects.stream().map(obj -> {

                JSONObject tmpJSONObject = (JSONObject) obj;     //把 5 个搜索结果分别转换成 String，然后用逗号连接成一个大 String。---通过stream来执行拼接的结果。

                return tmpJSONObject.toString();

            }).collect(Collectors.joining(","));

            return result;

        } catch (Exception e) {

            return "Error searching Baidu: " + e.getMessage();

        }
    }
}
