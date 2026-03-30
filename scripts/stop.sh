#!/bin/bash

# 应用名称
APP_NAME="easy-ask.jar"

echo "Stopping $APP_NAME..."

# 查找进程 PID
PID=$(ps aux | grep "$APP_NAME" | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "$APP_NAME is not running."
    exit 0
fi

echo "Found $APP_NAME with PID: $PID"

# 优雅关闭进程
kill -9 $PID