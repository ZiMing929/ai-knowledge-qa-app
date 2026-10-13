# 第一阶段：使用 Maven + JDK 21 构建 Spring Boot 项目
FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /app

# 复制 Maven 配置和 Java 源代码
COPY pom.xml .
COPY src ./src

# 打包，跳过测试
RUN mvn clean package -DskipTests

# 第二阶段：使用更轻量的 JDK 21 运行应用
FROM amazoncorretto:21

WORKDIR /app

# 从构建阶段复制可执行 JAR，不依赖固定项目名称
COPY --from=builder /app/target/*.jar /app/app.jar

# 为 Render 免费实例限制 JVM 内存，降低内存不足风险
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:+UseSerialGC"

# Render 默认常用端口
EXPOSE 10000

# 启动 Spring Boot，实际端口由 application.yml 决定
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
