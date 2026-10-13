package com.yupi.yuaiagent.constant;



/*
定义一个接口类，保存临时文件的目录信息，放到根目录下的tmp路径当中！
*/


public interface FileConstant {


    /**
     * 文件保存目录
     */

    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";


}
