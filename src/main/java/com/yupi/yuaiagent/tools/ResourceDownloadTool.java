package com.yupi.yuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.yupi.yuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;


//资源下载的工具类，用的hutool的download方法来实现的！

public class ResourceDownloadTool {    // 如果无法创建测试类的话，把鼠标的光标移到类名旁边，然后再按快捷键去生成！

    @Tool(description = "Download a resource from a given URL")      //告诉大模型要去哪一个网址下面，去下载图片！

    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/download";   //定义下载后的文件存放地址
        String filePath = fileDir + "/" + fileName;

        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();

        }
    }
}
