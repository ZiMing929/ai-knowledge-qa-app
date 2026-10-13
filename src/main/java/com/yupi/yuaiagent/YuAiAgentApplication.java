package com.yupi.yuaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;    //如果有灰色的依赖加入进来，说明代表没有引入进去！


@SpringBootApplication        // 这是application是我们项目的启动类，
public class YuAiAgentApplication {

    public static void main(String[] args) { SpringApplication.run(YuAiAgentApplication.class, args); }

}