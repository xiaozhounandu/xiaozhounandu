#!/bin/bash

# 🚀 快速修复验证脚本
# 用于验证后端修复是否成功

echo ""
echo "======================================================"
echo "  🔧 后端修复验证工具"
echo "======================================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查Python
echo -e "${YELLOW}[1/4] 检查Python环境...${NC}"
if command -v python3 &> /dev/null; then
    echo -e "  ✅ Python3: $(python3 --version)"
else
    echo -e "  ${RED}❌ 未找到python3${NC}"
    exit 1
fi

# 检查requests
echo ""
echo -e "${YELLOW}[2/4] 检查Python依赖...${NC}"
python3 -c "import requests" 2>/dev/null
if [ $? -eq 0 ]; then
    echo -e "  ✅ requests 库已安装"
else
    echo -e "  🔄 安装requests..."
    pip3 install requests
fi

# 检查服务器
echo ""
echo -e "${YELLOW}[3/4] 检查后端服务器...${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/init/status 2>/dev/null)

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "  ✅ 后端服务运行正常 (8080端口)"
else
    echo -e "  ${RED}❌ 后端服务未启动或异常${NC}"
    echo ""
    echo -e "${BLUE}解决方法:${NC}"
    echo "  1. 在IDE中打开 XiaozhounanduApplication.java"
    echo "  2. 右键 → Run As → Java Application"
    echo "  3. 等待 'Started XiaozhounanduApplication'"
    echo ""
    echo "  或者按 Ctrl+C 退出，启动服务后再运行此脚本"
    echo ""
    read -p "按回车键继续等待5秒后重试，或Ctrl+C退出..." -t 10

    # 等待并重试
    echo "  等待5秒后重试..."
    sleep 5
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/init/status 2>/dev/null)

    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "  ✅ 后端服务已启动"
    else
        echo -e "  ${RED}❌ 服务仍未响应，请手动启动后重试${NC}"
        exit 1
    fi
fi

# 运行验证
echo ""
echo -e "${YELLOW}[4/4] 运行修复验证...${NC}"
echo ""
echo "======================================================"

python3 verify_fixes.py

RESULT=$?

echo ""
echo "======================================================"
echo ""

if [ $RESULT -eq 0 ]; then
    echo -e "${GREEN}✅ 所有修复验证通过！${NC}"
    echo ""
    echo -e "${BLUE}您现在可以：${NC}"
    echo "  1. 访问前端页面测试功能"
    echo "  2. 查看数据看板统计"
    echo "  3. 测试登录日志"
    echo "  4. 测试跟进记录删除权限"
    echo ""
    echo -e "📊 详细报告: ${BLUE}test/data/reports/${NC}"
    exit 0
else
    echo -e "${RED}❌ 部分测试失败，请查看上面的错误信息${NC}"
    echo ""
    echo "可能的原因："
    echo "  1. 需要创建测试数据"
    echo "  2. 用户权限不足"
    echo "  3. 数据库连接异常"
    echo ""
    echo "查看详细文档："
    echo "  cat 修复说明.md"
    exit 1
fi
