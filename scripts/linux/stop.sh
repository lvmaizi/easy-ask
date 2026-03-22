#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="easy-ask"
PID_FILE="${SCRIPT_DIR}/${APP_NAME}.pid"

# 检查 PID 文件是否存在
if [ ! -f "$PID_FILE" ]; then
    echo -e "${RED}错误：找不到 PID 文件 ${PID_FILE}${NC}"
    echo -e "${YELLOW}提示：应用可能未运行或已异常退出${NC}"
    exit 1
fi

# 读取 PID
PID=$(cat "$PID_FILE")

# 检查进程是否在运行
if ! ps -p "$PID" > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠ 进程 (PID: ${PID}) 已不存在${NC}"
    rm -f "$PID_FILE"
    exit 0
fi

echo -e "${GREEN}正在停止 ${APP_NAME} 服务 (PID: ${PID})...${NC}"

# 发送 SIGTERM 信号，优雅关闭
kill "$PID"

# 等待进程结束
WAIT_TIME=0
MAX_WAIT=15

while ps -p "$PID" > /dev/null 2>&1; do
    if [ $WAIT_TIME -ge $MAX_WAIT ]; then
        echo -e "${YELLOW}⚠ 优雅关闭超时，强制终止进程...${NC}"
        kill -9 "$PID" 2>/dev/null
        sleep 1
        break
    fi

    sleep 1
    WAIT_TIME=$((WAIT_TIME + 1))
done

# 再次检查进程是否还存在
if ps -p "$PID" > /dev/null 2>&1; then
    echo -e "${RED}✗ 无法终止进程 (PID: ${PID})${NC}"
    exit 1
fi

# 清理 PID 文件
rm -f "$PID_FILE"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ 应用已成功停止${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "提示：启动服务请执行 ${YELLOW}${SCRIPT_DIR}/start.sh${NC}"
