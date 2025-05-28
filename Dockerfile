# 第一阶段：使用 Maven 构建应用
FROM eclipse-temurin:17-jdk as build
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# 第二阶段：运行构建好的 JAR 包
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
