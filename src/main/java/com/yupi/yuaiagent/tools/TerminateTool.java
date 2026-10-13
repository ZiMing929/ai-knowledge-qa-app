package com.yupi.yuaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;


//终止工具，让ai智能体能够合理的中断任务！

public class TerminateTool {

    //工具类的注解，写下工具的描述！
    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String doTerminate() {
        return "任务结束";
    }

}
