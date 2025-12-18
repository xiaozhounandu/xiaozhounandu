#!/bin/bash

# 🛠️ 测试环境初始化脚本

set -e

echo -e "\n\033[1;34m════════════════════════════════════════\033[0m"
echo -e "\033[1;34m     测试环境初始化\033[0m"
echo -e "\033[1;34m════════════════════════════════════════\033[0m\n"

TEST_DIR="/Users/weizhijie/Desktop/xiaozhounandu-main/test"

# 1. 检查并安装 Python 依赖
echo -e "\033[1;33m[1/4] 安装 Python 依赖...\033[0m"
cd "$TEST_DIR"

if command -v pip3 &> /dev/null; then
    echo "使用 pip3..."
    pip3 install -r requirements.txt
elif command -v pip &> /dev/null; then
    echo "使用 pip..."
    pip install -r requirements.txt
else
    echo -e "\033[0;31m错误: 未找到 pip\033[0m"
    exit 1
fi

echo -e "\033[0;32m✅ Python 依赖安装完成\033[0m"

# 2. 创建必要的目录
echo -e "\n\033[1;33m[2/4] 创建目录结构...\033[0m"
mkdir -p backend
mkdir -p data/reports
mkdir -p frontend
mkdir -p performance
echo -e "\033[0;32m✅ 目录结构创建完成\033[0m"

# 3. 验证服务器状态
echo -e "\n\033[1;33m[3/4] 检查后端服务器...\033[0m"
if curl -s http://localhost:8080/api/init/status > /dev/null 2>&1; then
    echo -e "\033[0;32m✅ 后端服务器已在运行\033[0m"
    echo "   检查详细状态:"
    curl -s http://localhost:8080/api/init/status | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/init/status
else
    echo -e "\033[0;33m⚠️  后端服务器未启动\033[0m"
    echo -e "   请先启动后端服务:"
    echo -e "   1. 打开 IDE"
    echo -e "   2. 运行 XiaozhounanduApplication.java"
    echo -e "   3. 等待出现 'Started XiaozhounanduApplication'"

    # 尝试等待用户输入后重试
    echo ""
    read -p "现在启动服务器了吗? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "等待 10 秒后检查..."
        sleep 10
        if curl -s http://localhost:8080/api/init/status > /dev/null 2>&1; then
            echo -e "\033[0;32m✅ 服务器已启动\033[0m"
        else
            echo -e "\033[0;31m❌ 服务器仍未响应，请检查\033[0m"
            exit 1
        fi
    else
        echo -e "\033[0;33m⚠️  跳过服务器检查\033[0m"
    fi
fi

# 4. 验证测试脚本
echo -e "\n\033[1;33m[4/4] 验证测试脚本...\033[0m"
if [ -f "run_tests.sh" ]; then
    chmod +x run_tests.sh
    echo -e "\033[0;32m✅ 测试脚本就绪\033[0m"
else
    echo -e "\033[0;31m❌ 未找到 run_tests.sh\033[0m"
    exit 1
fi

# 检查关键 Python 文件
for file in backend/client.py backend/config.py backend/test_auth.py backend/test_customer.py; do
    if [ -f "$file" ]; then
        echo -e "   ✅ $file"
    else
        echo -e "   ❌ $file 未找到"
    fi
done

echo -e "\n\033[1;32m════════════════════════════════════════\033[0m"
echo -e "\033[1;32m     初始化完成!\033[0m"
echo -e "\033[1;32m════════════════════════════════════════\033[0m\n"

echo -e "\033[1;36m后续操作:\033[0m"
echo "  1. 运行全部测试: ./run_tests.sh"
echo "  2. 运行单个模块: python3 backend/test_auth.py"
echo "  3. 生成测试报告: 数据将保存在 data/reports/"
echo ""
echo -e "\033[1;35m常用命令:\033[0m"
echo "  - 查看测试代码: ls -R backend/"
echo "  - 查看测试报告: ls -lt data/reports/"
echo "  - 清理报告: rm -f data/reports/*.md"
echo ""
