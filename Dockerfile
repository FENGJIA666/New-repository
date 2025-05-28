# 第一阶段：构建阶段（Build Stage）
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app
COPY . .

# 使用 Maven Wrapper 编译项目（你已经有 mvnw 了）
RUN chmod +x ./mvnw && ./mvnw -B -DskipTests clean package

# 第二阶段：运行阶段（Run Stage）
FROM openjdk:17

WORKDIR /app

# 复制构建产物 JAR 文件到运行容器中
COPY --from=build /app/target/*.jar app.jar

# 启动 Spring Boot 项目
ENTRYPOINT ["java", "-jar", "app.jar"]
