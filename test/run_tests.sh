#!/bin/bash

# 🧪 自动化测试运行脚本
# 适用于 Linux/macOS

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
TEST_DIR="/Users/weizhijie/Desktop/xiaozhounandu-main/test"
BACKEND_DIR="$TEST_DIR/backend"
REPORT_DIR="$TEST_DIR/data/reports"
BASE_URL="http://localhost:8080"

echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}     🚀 客户管理系统 V2.0 - 自动化测试套件${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""

# 1. 检查Python环境
echo -e "${YELLOW}步骤1: 检查Python环境...${NC}"
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}❌ 未找到 python3，请安装 Python 3.8+${NC}"
    exit 1
fi
PYTHON_VERSION=$(python3 -V 2>&1 | cut -d" " -f2)
echo -e "${GREEN}✅ Python $PYTHON_VERSION${NC}"

# 2. 检查依赖
echo ""
echo -e "${YELLOW}步骤2: 检查Python依赖...${NC}"
cd "$TEST_DIR"

if python3 -c "import requests" 2>/dev/null; then
    echo -e "${GREEN}✅ requests 已安装${NC}"
else
    echo -e "${RED}❌ 缺少 requests 库，正在安装...${NC}"
    pip3 install requests
fi

# 3. 检查服务器状态
echo ""
echo -e "${YELLOW}步骤3: 检查后端服务器...${NC}"
if curl -s "$BASE_URL/api/init/status" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ 后端服务器运行正常${NC}"
else
    echo -e "${RED}❌ 后端服务器未启动或不在 $BASE_URL${NC}"
    echo -e "${YELLOW}请先启动后端服务: cd /Users/weizhijie/Desktop/xiaozhounandu-main && 在IDE中运行 XiaozhounanduApplication${NC}"
    echo -e "${YELLOW}或等待服务器启动后重试...${NC}"
    exit 1
fi

# 4. 创建报告目录
echo ""
echo -e "${YELLOW}步骤4: 准备测试环境...${NC}"
mkdir -p "$REPORT_DIR"
echo -e "${GREEN}✅ 报告目录已准备${NC}"

# 5. 运行测试
echo ""
echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}     开始执行测试${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$REPORT_DIR/test_run_${TIMESTAMP}.log"

# 运行测试并记录日志
{
    echo "=== 认证模块测试 ==="
    python3 "$BACKEND_DIR/test_auth.py"
    echo ""
    echo "=== 客户管理测试 ==="
    python3 "$BACKEND_DIR/test_customer.py"
    echo ""
    echo "=== 完整流程测试 ==="
    python3 "$BACKEND_DIR/test_complete_flow.py"
} | tee "$LOG_FILE"

# 6. 汇总结果
echo ""
echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}     测试执行完成${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""
echo -e "📊 测试日志: ${LOG_FILE}"
echo ""

# 统计结果
PASSED=$(grep -o "✅ PASS" "$LOG_FILE" | wc -l)
FAILED=$(grep -o "❌ FAIL" "$LOG_FILE" | wc -l)
TOTAL=$((PASSED + FAILED))

if [ $TOTAL -gt 0 ]; then
    SUCCESS_RATE=$(echo "scale=1; $PASSED * 100 / $TOTAL" | bc)
    echo -e "测试总计: ${TOTAL} | 通过: ${GREEN}${PASSED}${NC} | 失败: ${RED}${FAILED}${NC} | 成功率: ${GREEN}${SUCCESS_RATE}%${NC}"

    if [ $FAILED -eq 0 ]; then
        echo -e "${GREEN}🎉 所有测试通过！${NC}"
        exit 0
    else
        echo -e "${RED}⚠️  有 ${FAILED} 个测试失败，请检查日志${NC}"
        echo -e "日志文件: $LOG_FILE"
        exit 1
    fi
else
    echo -e "${RED}❌ 未检测到测试结果，请检查错误${NC}"
    exit 1
fi
