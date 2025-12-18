"""
测试模块: 统计数据和日志功能
"""
import sys
sys.path.append(".")

from backend.client import APIClient, AuthClient, StatsClient, LogClient
from backend.utils import print_test_result, validate_response, wait_for_server, format_table


class StatsLogsTestSuite:
    """统计和日志测试套件"""

    def __init__(self):
        self.client = APIClient()
        self.auth = AuthClient(self.client)
        self.stats = StatsClient(self.client)
        self.log = LogClient(self.client)

    def setup_admin(self) -> bool:
        """以管理员身份登录"""
        login_resp = self.auth.login("admin", "admin123")
        if login_resp.get("success"):
            token = login_resp.get("data", {}).get("token") or login_resp.get("data", {}).get("data", {}).get("token")
            self.client.set_token(token)
            return True
        return False

    def setup_user(self) -> bool:
        """以普通用户身份登录"""
        login_resp = self.auth.login("user", "admin123")
        if login_resp.get("success"):
            token = login_resp.get("data", {}).get("token") or login_resp.get("data", {}).get("data", {}).get("token")
            self.client.set_token(token)
            return True
        return False

    def test_dashboard_stats(self) -> bool:
        """测试仪表盘统计"""
        print("\n=== 测试: 仪表盘统计 ===")

        response = self.stats.get_dashboard()
        success, message = validate_response(response, check_data=False)

        if success:
            data = response.get("data", {})
            print("  统计数据:")
            print(f"    - 总客户数: {data.get('totalCustomers', 0)}")
            print(f"    - 活跃客户: {data.get('activeCustomers', 0)}")
            print(f"    - 已成单: {data.get('dealedCustomers', 0)}")
            print(f"    - 已流失: {data.get('lostCustomers', 0)}")
            print(f"    - 今日新增: {data.get('newCustomersToday', 0)}")

            # 检查数据完整性
            required_keys = ['totalCustomers', 'activeCustomers']
            if all(k in data for k in required_keys):
                print("  ✅ 数据完整")
            else:
                print("  ⚠️  部分数据缺失")
                message = "部分字段缺失"

        print_test_result("仪表盘统计", success, message)
        return success

    def test_stats_api(self) -> bool:
        """测试统计 API"""
        print("\n=== 测试: 统计 API ===")

        # 尝试调用不同的统计接口（如果有）
        response = self.stats.get_stats()
        success, message = validate_response(response, check_data=False)

        if success:
            print("  ✅ 统计接口响应正常")

        print_test_result("统计 API", success, message)
        return success

    def test_operation_logs_admin(self) -> bool:
        """测试操作日志 - 管理员权限"""
        print("\n=== 测试: 操作日志 (管理员) ===")

        response = self.log.get_operation_logs(
            page=1,
            page_size=10,
            username="",
            startDate="2025-12-01"
        )
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            logs = data.get("list", [])
            total = data.get("total", 0)

            print(f"  总日志数: {total}")
            print(f"  本次返回: {len(logs)} 条")

            if logs:
                print("\n  最近3条操作日志:")
                preview = []
                for log in logs[:3]:
                    preview.append({
                        "时间": log.get("createTime", "")[:19],
                        "用户": log.get("username", ""),
                        "操作": f"{log.get('module', '')}.{log.get('operation', '')}",
                        "目标": log.get("targetName", "N/A")[:10]
                    })
                format_table(preview, ["时间", "用户", "操作", "目标"])

        print_test_result("操作日志查询", success, message)
        return success

    def test_login_logs_admin(self) -> bool:
        """测试登录日志 - 管理员权限"""
        print("\n=== 测试: 登录日志 (管理员) ===")

        response = self.log.get_login_logs(
            page=1,
            page_size=10
        )
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            logs = data.get("list", [])

            print(f"  登录日志数: {data.get('total', 0)}")
            if logs:
                print("\n  最近登录记录:")
                for log in logs[:3]:
                    print(f"    - {log.get('username')} @ {log.get('createTime', '')[:19]} "
                          f"[{log.get('result', '成功')}] - {log.get('ipAddress', '未知IP')}")

        print_test_result("登录日志查询", success, message)
        return success

    def test_logs_permission_user(self) -> bool:
        """测试日志权限 - 普通用户"""
        print("\n=== 测试: 日志权限 (普通用户) ===")

        # 切换到普通用户
        if not self.setup_user():
            print("⚠️  普通用户登录失败")
            return False

        # 普通用户访问操作日志
        response = self.log.get_operation_logs(page=1, page_size=5)

        # 应该被拒绝或返回空
        is_forbidden = response.get("status_code") == 403
        is_empty = (
            response.get("data", {}).get("list", []) == [] or
            response.get("data", {}).get("total", 0) == 0
        )

        permission_ok = is_forbidden or is_empty

        if permission_ok:
            if is_forbidden:
                print("  ✅ 被正确拒绝访问")
            else:
                print("  ✅ 返回空列表（数据隔离）")
        else:
            print("  ❌ 权限控制异常")

        print_test_result("普通用户日志权限", permission_ok, "权限验证")
        return permission_ok

    def test_log_filters(self) -> bool:
        """测试日志筛选功能"""
        print("\n=== 测试: 日志筛选 ===")

        # 切换回管理员
        if not self.setup_admin():
            return False

        # 筛选特定用户的日志
        response = self.log.get_operation_logs(
            page=1,
            page_size=5,
            username="admin"
        )
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            logs = data.get("list", [])

            # 验证筛选结果
            all_admin = all(log.get("username") == "admin" for log in logs) if logs else True

            if all_admin:
                print(f"  ✅ 筛选正确，找到 {len(logs)} 条管理员日志")
            else:
                print("  ⚠️  筛选结果可能有误")
                message = "筛选结果不准确"

        print_test_result("日志筛选", success, message)
        return success


def run_stats_logs_tests():
    """运行统计和日志测试"""
    print("\n" + "="*60)
    print("🚀 开始运行统计与日志模块测试")
    print("="*60)

    if not wait_for_server():
        return [{"name": "服务器检查", "passed": False, "message": "服务器未启动"}]

    suite = StatsLogsTestSuite()
    if not suite.setup_admin():
        return [{"name": "环境准备", "passed": False, "message": "管理员登录失败"}]

    tests = [
        ("仪表盘统计", suite.test_dashboard_stats),
        ("统计 API", suite.test_stats_api),
        ("操作日志 (管理员)", suite.test_operation_logs_admin),
        ("登录日志 (管理员)", suite.test_login_logs_admin),
        ("日志权限 (普通用户)", suite.test_logs_permission_user),
        ("日志筛选", suite.test_log_filters),
    ]

    results = []
    for name, test_func in tests:
        try:
            result = test_func()
            results.append({
                "name": name,
                "passed": result,
                "message": "通过" if result else "失败"
            })
        except Exception as e:
            results.append({
                "name": name,
                "passed": False,
                "message": "执行异常",
                "error": str(e)
            })

    # 汇总
    print("\n" + "="*60)
    print("📊 统计与日志测试汇总")
    print("="*60)
    passed = sum(1 for r in results if r["passed"])
    total = len(results)
    print(f"总计: {total} | 通过: {passed} | 失败: {total - passed} | 成功率: {passed/total*100:.1f}%")

    return results


if __name__ == "__main__":
    run_stats_logs_tests()
