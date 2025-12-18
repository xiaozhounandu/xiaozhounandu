"""
测试工具函数
"""
import time
import json
from datetime import datetime, timedelta
from typing import Dict, Any, List


def generate_timestamp() -> str:
    """生成时间戳字符串"""
    return str(int(time.time() * 1000))


def generate_unique_name(prefix: str = "测试") -> str:
    """生成唯一名称"""
    timestamp = generate_timestamp()
    return f"{prefix}_{timestamp}"


def generate_customer_data() -> Dict[str, Any]:
    """生成测试客户数据"""
    timestamp = generate_timestamp()
    return {
        "name": f"测试客户_{timestamp}",
        "company": f"测试公司_{timestamp}",
        "phone": f"138{timestamp[-8:]}",
        "email": f"test_{timestamp}@example.com",
        "level": "VIP",
        "industry": "IT",
        "address": f"北京市朝阳区测试地址_{timestamp}",
        "description": f"自动化测试创建的客户 - {timestamp}"
    }


def generate_followup_data() -> Dict[str, Any]:
    """生成测试跟进数据"""
    timestamp = generate_timestamp()
    next_time = (datetime.now() + timedelta(days=3)).strftime("%Y-%m-%d %H:%M:%S")
    return {
        "type": "电话",
        "content": f"自动化测试跟进_{timestamp}",
        "result": "良好",
        "nextFollowTime": next_time
    }


def validate_response(response: Dict[str, Any],
                     expected_status: int = 200,
                     check_data: bool = True) -> tuple[bool, str]:
    """
    验证 API 响应

    Returns:
        (是否成功, 错误消息)
    """
    if not response.get("success", False):
        return False, f"请求失败: {response.get('data', 'Unknown error')}"

    if response["status_code"] != expected_status:
        return False, f"状态码错误: 期望 {expected_status}, 实际 {response['status_code']}"

    if check_data and "data" in response:
        data = response["data"]
        if isinstance(data, dict) and data.get("code") not in [0, None, "SUCCESS"]:
            return False, f"业务错误: {data.get('message', data)}"

    return True, "验证通过"


def print_test_result(test_name: str, passed: bool, details: str = ""):
    """打印测试结果"""
    status = "✅ PASS" if passed else "❌ FAIL"
    print(f"{status} | {test_name}")
    if details and not passed:
        print(f"      💡 {details}")


def save_test_report(test_results: List[Dict], filename: str = None):
    """保存测试报告"""
    if not filename:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"test/data/reports/test_report_{timestamp}.md"

    # 确保目录存在
    import os
    os.makedirs("test/data/reports", exist_ok=True)

    passed = sum(1 for r in test_results if r["passed"])
    total = len(test_results)

    with open(filename, "w", encoding="utf-8") as f:
        f.write(f"# 🧪 自动化测试报告\n\n")
        f.write(f"**执行时间**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
        f.write(f"**测试总数**: {total}\n")
        f.write(f"**通过**: {passed}\n")
        f.write(f"**失败**: {total - passed}\n")
        f.write(f"**成功率**: {passed/total*100:.1f}%\n\n")
        f.write("---\n\n")

        f.write("## 📊 详细结果\n\n")
        for result in test_results:
            icon = "✅" if result["passed"] else "❌"
            f.write(f"- {icon} **{result['name']}** - {result['message']}\n")
            if not result["passed"]:
                f.write(f"  - 错误: {result.get('error', 'N/A')}\n")

        f.write("\n---\n\n")
        f.write("**报告生成时间**: " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "\n")

    print(f"\n📊 测试报告已保存: {filename}")


def check_server_health(base_url: str = "http://localhost:8080") -> bool:
    """检查服务器健康状态"""
    import requests
    try:
        response = requests.get(f"{base_url}/api/init/status", timeout=3)
        return response.status_code == 200
    except:
        return False


def wait_for_server(base_url: str = "http://localhost:8080", timeout: int = 30) -> bool:
    """等待服务器就绪"""
    print(f"⏳ 等待服务器就绪 ({timeout}秒)...")
    start = time.time()
    while time.time() - start < timeout:
        if check_server_health(base_url):
            print("✅ 服务器已就绪")
            return True
        time.sleep(1)
    print("❌ 服务器未在规定时间内启动")
    return False


def format_table(data: List[Dict[str, Any]], columns: List[str] = None):
    """格式化输出表格"""
    if not data:
        print("暂无数据")
        return

    if columns is None:
        columns = list(data[0].keys())

    # 计算列宽
    col_widths = {}
    for col in columns:
        col_widths[col] = max(len(str(col)), *[len(str(row.get(col, ""))) for row in data])

    # 打印表头
    header = " | ".join(col.ljust(col_widths[col]) for col in columns)
    print(f"| {header} |")

    # 打印分隔线
    separator = "-".join("-" * col_widths[col] for col in columns)
    print(f"| {separator} |")

    # 打印数据行
    for row in data:
        line = " | ".join(str(row.get(col, "")).ljust(col_widths[col]) for col in columns)
        print(f"| {line} |")


print("✅ 工具函数加载完成")
