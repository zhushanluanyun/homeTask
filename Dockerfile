# 基础镜像：使用官方 OpenJDK 21
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制 Maven 构建产物
COPY target/homeTask-*.jar app.jar

# 暴露应用端口（默认为 8080）
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]