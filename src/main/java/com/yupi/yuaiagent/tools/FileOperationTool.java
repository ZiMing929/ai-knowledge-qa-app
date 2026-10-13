package com.yupi.yuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.yupi.yuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;



//  本质大部分代码都是ai写的，只是我们需要去设计业务流程，告诉ai怎么去写代码，怎么去做。
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";      // 拼接最终文件目录--把文件存入到我们指定的位置当中！，final是一个常量

    @Tool(description = "Read content from a file")      // 这个 description 主要不是写给用户看的，而是帮助大模型理解这个工具是干什么的。

    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {  //readfile是方法名称，后面括号里面的是参数
        //ToolParam---解释给大模型---这个参数是干啥的

        String filePath = FILE_DIR + "/" + fileName;
        try {

            return FileUtil.readUtf8String(filePath);   //按 UTF-8 编码读取 filePath 指向的文件，并把里面的内容转换成 String。---引入了hutool的工具类

        } catch (Exception e) {

            return "Error reading file: " + e.getMessage();     //如果没有成功的执行，就返回异常的处理！

        }
    }


    @Tool(description = "Write content to a file")

    public String writeFile(

            @ToolParam(description = "Name of the file to write") String fileName,     //这个参数是干啥的，filename ---存放的是文件的名称

            @ToolParam(description = "Content to write to the file") String content) {    // --存放的是文件的内容！

        String filePath = FILE_DIR + "/" + fileName;

        try {

            // 创建目录
            FileUtil.mkdir(FILE_DIR);

            FileUtil.writeUtf8String(content, filePath);

            return "File written successfully to: " + filePath;       // 文件写成功了，而且写到了这个地方。

        }
        catch (Exception e) {

            return "Error writing to file: " + e.getMessage();

        }
    }
}

