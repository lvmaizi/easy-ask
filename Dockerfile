# ============================================================
# Easy Ask Backend Dockerfile
# ============================================================

# 构建阶段
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS builder
# 设置工作目录
WORKDIR /build

# 复制 pom.xml 以利用 Docker 缓存层
COPY backend/pom.xml .

# 下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -B || true

# 复制源代码
COPY backend/src ./src

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM docker.io/library/eclipse-temurin:21-jdk-alpine

# 创建应用目录
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/target/easy-ask.jar ./easy-ask.jar

# 复制外部配置文件
COPY scripts/conf/application.properties ./config/application.properties

# 暴露端口
EXPOSE 8080

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1g -Dbase.path=/app/docs"

# 启动应用，指定配置文件
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar easy-ask.jar --spring.config.additional-location=file:/app/config/"]