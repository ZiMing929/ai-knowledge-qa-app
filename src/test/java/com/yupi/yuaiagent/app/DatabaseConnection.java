package com.yupi.yuaiagent.app;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {      // 测试数据量连接是否成功的代码，目前在做项目的排查工作！
/*
    阿里云 PostgreSQL 数据库正常、网络正常、JDBC 驱动正常、数据库地址和账号认证也正常----------做代码的排除测试工作！
*/

    @Test
    void testDatabaseConnection() throws Exception {    // Drivermanager的测试！

        Connection connection = DriverManager.getConnection("jdbc:postgresql://pgm-uf6ytl7bq2o22557qo.rwlb.rds.aliyuncs.com:5432/yu_ai_agent", "my_user", "Xn123456");
        System.out.println("数据库连接成功：" + connection.isValid(5));

        connection.close();
    }
}