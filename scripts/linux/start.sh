#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="easy-ask"
JAR_FILE="${SCRIPT_DIR}/${APP_NAME}.jar"
PID_FILE="${SCRIPT_DIR}/${APP_NAME}.pid"
LOG_FILE="${SCRIPT_DIR}/${APP_NAME}.log"
CONF_DIR="${SCRIPT_DIR}/conf"

# 检查 Java 环境
if ! command -v java &> /dev/null; then
    echo -e "${RED}错误：未找到 Java 环境${NC}"
    exit 1
fi

# 检查 JAR 文件
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}错误：找不到 ${JAR_FILE}${NC}"
    exit 1
fi

# 检查是否已在运行
if [ -f "$PID_FILE" ]; then
    OLD_PID=$(cat "$PID_FILE")
    if ps -p "$OLD_PID" > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠ 应用已在运行中 (PID: ${OLD_PID})${NC}"
        echo -e "${YELLOW}提示：如需重启，先执行 stop.sh${NC}"
        exit 0
    fi
    rm -f "$PID_FILE"
fi

# 创建配置目录
mkdir -p "$CONF_DIR"

echo -e "${GREEN}正在启动 ${APP_NAME} 服务${NC}"

# 清空旧日志并启动
> "$LOG_FILE"
nohup java \
  -Xms512m \
  -Xmx1g \
  -Dspring.config.additional-location=file:${CONF_DIR}/ \
  -DbasePath=${SCRIPT_DIR}/${APP_NAME} \
  -jar "${JAR_FILE}" > "${LOG_FILE}" 2>&1 &

echo $! > "$PID_FILE"
echo -e "${GREEN}✓ 进程已启动 (PID: $!)${NC}"

# 等待启动
echo -e "${YELLOW}正在等待应用启动...${NC}"
sleep 5

# 检查进程状态
if ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}✓ 应用启动成功！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo -e "PID: ${GREEN}$(cat "$PID_FILE")${NC}"
    echo -e "日志：${GREEN}${LOG_FILE}${NC}"
    echo -e "配置：${GREEN}${CONF_DIR}${NC}"
    echo -e "\n查看日志：${YELLOW}tail -f ${LOG_FILE}${NC}"
    echo -e "停止服务：${YELLOW}${SCRIPT_DIR}/stop.sh${NC}"
else
    echo -e "${RED}========================================${NC}"
    echo -e "${RED}✗ 应用启动失败！${NC}"
    echo -e "${RED}========================================${NC}"
    if [ -f "$LOG_FILE" ]; then
        echo -e "${RED}最近错误日志:${NC}"
        tail -n 30 "$LOG_FILE"
    fi
    echo -e "\n完整日志：${YELLOW}cat ${LOG_FILE}${NC}"
    exit 1
fi
