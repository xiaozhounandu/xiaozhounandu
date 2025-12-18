#!/bin/bash

# 🚀 一键运行所有测试

echo -e "\n\033[1;34m═══════════════════════════════════════════════════════════\033[0m"
echo -e "\033[1;34m     🧪 客户管理系统 V2.0 - 完整测试套件\033[0m"
echo -e "\033[1;34m═══════════════════════════════════════════════════════════\033[0m\n"

TEST_DIR="/Users/weizhijie/Desktop/xiaozhounandu-main/test"
cd "$TEST_DIR"

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 时间戳
TIME=$(date +"%Y-%m-%d %H:%M:%S")
DATE=$(date +"%Y%m%d_%H%M%S")

# 创建总报告
REPORT_FILE="data/reports/full_test_report_${DATE}.md"
mkdir -p data/reports

echo "**测试开始时间**: $TIME" > $REPORT_FILE
echo "" >> $REPORT_FILE
echo "---" >> $REPORT_FILE
echo "" >> $REPORT_FILE

# 测试模块列表
declare -a MODULES=(
    "test_auth.py"
    "test_customer.py"
    "test_followup.py"
    "test_stats_logs.py"
    "test_complete_flow.py"
)

MODULE_NAMES=(
    "认证模块"
    "客户管理"
    "跟进记录"
    "统计与日志"
    "完整流程"
)

TOTAL_PASSED=0
TOTAL_FAILED=0
TOTAL_TESTS=0

# 运行每个模块
for i in "${!MODULES[@]}"; do
    MODULE="${MODULES[$i]}"
    NAME="${MODULE_NAMES[$i]}"

    echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}测试模块: $NAME ($MODULE)${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"

    # 运行测试并捕获输出
    OUTPUT=$(python3 "backend/$MODULE" 2>&1)

    # 显示输出
    echo "$OUTPUT"

    # 分析结果
    if echo "$OUTPUT" | grep -q "通过:"; then
        # 提取数字（简单解析）
        P=$(echo "$OUTPUT" | grep "通过:" | tail -1 | sed 's/.*通过: \([0-9]*\).*/\1/')
        F=$(echo "$OUTPUT" | grep "失败:" | tail -1 | sed 's/.*失败: \([0-9]*\).*/\1/')
        TOTAL=$(echo "$OUTPUT" | grep "总计:" | tail -1 | sed 's/.*总计: \([0-9]*\).*/\1/')

        if [ -z "$P" ]; then P=0; fi
        if [ -z "$F" ]; then F=0; fi
        if [ -z "$TOTAL" ]; then TOTAL=$((P+F)); fi

        TOTAL_PASSED=$((TOTAL_PASSED + P))
        TOTAL_FAILED=$((TOTAL_FAILED + F))
        TOTAL_TESTS=$((TOTAL_TESTS + TOTAL))

        echo "" >> $REPORT_FILE
        echo "## 模块: $NAME" >> $REPORT_FILE
        echo "- 总计: $TOTAL" >> $REPORT_FILE
        echo "- 通过: $P" >> $REPORT_FILE
        echo "- 失败: $F" >> $REPORT_FILE
        echo "- 成功率: $(awk "BEGIN {printf \"%.1f\", ($P/$TOTAL)*100}")%" >> $REPORT_FILE

        if [ $F -eq 0 ]; then
            echo -e "\n${GREEN}✅ $NAME: 全部通过${NC}"
            echo "- 状态: ✅ 全部通过" >> $REPORT_FILE
        else
            echo -e "\n${RED}❌ $NAME: $F 个失败${NC}"
            echo "- 状态: ❌ $F 个失败" >> $REPORT_FILE
        fi
    else
        echo -e "\n${RED}❌ $NAME: 执行异常或未返回结果${NC}"
        echo "- 状态: ❌ 执行异常" >> $REPORT_FILE
        TOTAL_FAILED=$((TOTAL_FAILED + 1))
        TOTAL_TESTS=$((TOTAL_TESTS + 1))
    fi

    # 短暂暂停
    sleep 1
done

# 最终汇总
echo "" >> $REPORT_FILE
echo "---" >> $REPORT_FILE
echo "" >> $REPORT_FILE
echo "## 总计汇总" >> $REPORT_FILE
echo "- 测试总数: $TOTAL_TESTS" >> $REPORT_FILE
echo "- 通过: $TOTAL_PASSED" >> $REPORT_FILE
echo "- 失败: $TOTAL_FAILED" >> $REPORT_FILE
if [ $TOTAL_TESTS -gt 0 ]; then
    RATE=$(awk "BEGIN {printf \"%.1f\", ($TOTAL_PASSED/$TOTAL_TESTS)*100}")
    echo "- 成功率: ${RATE}%" >> $REPORT_FILE
fi
echo "" >> $REPORT_FILE
echo "**测试结束时间**: $(date +\"%Y-%m-%d %H:%M:%S\")" >> $REPORT_FILE

echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📊 最终汇总${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}\n"

echo "测试总数: $TOTAL_TESTS"
echo -e "通过: ${GREEN}$TOTAL_PASSED${NC}"
echo -e "失败: ${RED}$TOTAL_FAILED${NC}"

if [ $TOTAL_TESTS -gt 0 ]; then
    RATE=$(awk "BEGIN {printf \"%.1f\", ($TOTAL_PASSED/$TOTAL_TESTS)*100}")
    echo -e "成功率: ${GREEN}${RATE}%${NC}"
    echo ""
    echo -e "📊 详细报告: ${REPORT_FILE}"
    echo ""

    if [ $TOTAL_FAILED -eq 0 ]; then
        echo -e "${GREEN}🎉 恭喜！所有测试通过！${NC}\n"
        exit 0
    else
        echo -e "${YELLOW}⚠️  有 ${TOTAL_FAILED} 个测试失败，请查看详细报告${NC}\n"
        exit 1
    fi
else
    echo -e "${RED}❌ 未检测到测试结果${NC}"
    exit 1
fi
