#!/bin/bash

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# JAR 包路径
JAR_PATH="$PROJECT_ROOT/backend/target/easy-ask.jar"

# 配置文件路径
CONFIG_PATH="$SCRIPT_DIR/conf/application.properties"

# 检查 JAR 包是否存在
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: JAR file not found at $JAR_PATH"
    exit 1
fi

# 检查配置文件是否存在
if [ ! -f "$CONFIG_PATH" ]; then
    echo "Error: Config file not found at $CONFIG_PATH"
    exit 1
fi

echo "Starting easy-ask application..."
echo "JAR path: $JAR_PATH"
echo "Config path: $CONFIG_PATH"

# 启动应用，指定配置文件
java -jar "$JAR_PATH" --spring.config.location="file:$CONFIG_PATH" &

echo "Application started with PID: $!"