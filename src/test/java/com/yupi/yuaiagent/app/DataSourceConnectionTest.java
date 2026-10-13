/*
package com.yupi.yuaiagent.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
import java.sql.Connection;

public class DataSourceConnectionTest {      //这个还是测试数据库连接的test测试类！

    @Test
    void testDataSourceConnection() throws Exception {
        DataSource dataSource = DataSourceBuilder.create().url("jdbc:postgresql://pgm-uf6ytl7bq2o22557qo.rwlb.rds.aliyuncs.com:5432/yu_ai_agent").username("my_user").password("Xn123456").driverClassName("org.postgresql.Driver").build();

        Connection connection = dataSource.getConnection();

        System.out.println("DataSource类型：" + dataSource.getClass().getName());
        System.out.println("数据库连接成功：" + connection.isValid(5));
        System.out.println("数据库URL：" + connection.getMetaData().getURL());
        System.out.println("数据库用户名：" + connection.getMetaData().getUserName());

        connection.close();
    }
}*/



package com.yupi.yuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
public class DataSourceConnectionTest {

    @Resource
    private DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.profiles.active:未设置}")
    private String activeProfile;

    @Test
    void testSpringDataSourceConnection() throws Exception {
        System.out.println("========== Spring 实际配置 ==========");
        System.out.println("当前 Profile：" + activeProfile);
        System.out.println("Spring读取URL：" + url);
        System.out.println("Spring读取用户名：" + username);
        System.out.println("DataSource类型：" + dataSource.getClass().getName());
        System.out.println("========== 开始获取连接 ==========");
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("数据库连接成功：" + connection.isValid(5));
            System.out.println("实际数据库URL：" + connection.getMetaData().getURL());
            System.out.println("实际数据库用户名：" + connection.getMetaData().getUserName());
        }
    }
}