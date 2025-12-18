"""
端到端完整业务流程测试
"""
import sys
sys.path.append(".")

from backend.client import (
    APIClient, AuthClient, CustomerClient,
    FollowUpClient, StatsClient, LogClient
)
from backend.utils import (
    print_test_result, validate_response, wait_for_server,
    generate_customer_data, generate_followup_data, save_test_report
)
from backend.config import TEST_USERS


class EndToEndTestSuite:
    """端到端测试套件"""

    def __init__(self):
        self.client = APIClient()
        self.auth = AuthClient(self.client)
        self.customer = CustomerClient(self.client)
        self.followup = FollowUpClient(self.client)
        self.stats = StatsClient(self.client)
        self.log = LogClient(self.client)

        self.created_customers = []
        self.created_followups = []

    def login_as_admin(self) -> bool:
        """以管理员身份登录"""
        print("\n🔐 登录管理工程员...")
        response = self.auth.login("admin", "admin123")
        if response.get("success"):
            token = response.get("data", {}).get("token") or response.get("data", {}).get("data", {}).get("token")
            self.client.set_token(token)
            print("✅ 登录成功")
            return True
        print("❌ 登录失败")
        return False

    def test_complete_workflow(self) -> list:
        """完整业务流程测试"""
        results = []

        print("\n" + "="*60)
        print("🔄 开始完整业务流程测试")
        print("="*60)

        # 步骤1: 登录
        print("\n--- 步骤1: 用户登录 ---")
        login_success = self.login_as_admin()
        results.append({
            "name": "步骤1: 登录",
            "passed": login_success,
            "message": "登录成功" if login_success else "登录失败"
        })
        if not login_success:
            return results

        # 步骤2: 创建客户
        print("\n--- 步骤2: 创建客户 ---")
        customer_data = generate_customer_data()
        create_resp = self.customer.create(customer_data)
        create_success, msg = validate_response(create_resp)

        if create_success:
            customer_id = create_resp.get("data", {}).get("id")
            self.created_customers.append(customer_id)
            print(f"  ✅ 客户创建成功，ID: {customer_id}")
        else:
            customer_id = None

        results.append({
            "name": "步骤2: 创建客户",
            "passed": create_success,
            "message": f"客户ID: {customer_id}" if create_success else msg
        })

        if not create_success:
            return results

        # 步骤3: 为该客户添加跟进记录
        print("\n--- 步骤3: 添加跟进记录 ---")
        followup_data = generate_followup_data()
        followup_data["customerId"] = customer_id

        followup_resp = self.followup.create(followup_data)
        followup_success, msg = validate_response(followup_resp)

        if followup_success:
            followup_id = followup_resp.get("data", {}).get("id")
            self.created_followups.append(followup_id)
            print(f"  ✅ 跟进记录创建成功，ID: {followup_id}")
        else:
            followup_id = None

        results.append({
            "name": "步骤3: 添加跟进",
            "passed": followup_success,
            "message": f"跟进ID: {followup_id}" if followup_success else msg
        })

        # 步骤4: 查询客户跟进历史
        print("\n--- 步骤4: 查询客户跟进历史 ---")
        if customer_id:
            history_resp = self.followup.get_by_customer(customer_id)
            history_success, msg = validate_response(history_resp)

            if history_success:
                data = history_resp.get("data", {})
                list_data = data.get("list", [])
                print(f"  查找到 {len(list_data)} 条跟进记录")
                if list_data:
                    print(f"  最新一条: {list_data[0].get('content')}")

            results.append({
                "name": "步骤4: 查询跟进历史",
                "passed": history_success,
                "message": msg
            })

        # 步骤5: 验证统计数据
        print("\n--- 步骤5: 验证统计数据 ---")
        stats_resp = self.stats.get_dashboard()
        stats_success, msg = validate_response(stats_resp, check_data=False)

        if stats_success:
            data = stats_resp.get("data", {})
            print(f"  总客户数: {data.get('totalCustomers')}")
            print(f"  活跃客户: {data.get('activeCustomers')}")
            print(f"  今日新增: {data.get('newCustomersToday')}")
            print("  ✅ 统计数据接口正常")
        else:
            print("  ⚠️  统计数据获取失败，但不影响主要流程")

        results.append({
            "name": "步骤5: 统计数据",
            "passed": stats_success,
            "message": "接口返回" if stats_success else msg
        })

        # 步骤6: 查询操作日志
        print("\n--- 步骤6: 查询操作日志 ---")
        log_resp = self.log.get_operation_logs(page=1, page_size=5)
        log_success, msg = validate_response(log_resp)

        if log_success:
            data = log_resp.get("data", {})
            logs = data.get("list", [])
            print(f"  查询到 {len(logs)} 条日志")
            if logs:
                print("  最近操作:")
                for log in logs[:3]:
                    print(f"    - {log.get('module')}.{log.get('operation')} - {log.get('targetName', 'N/A')}")

        results.append({
            "name": "步骤6: 操作日志",
            "passed": log_success,
            "message": msg
        })

        # 步骤7: 权限验证 - 尝试用普通用户访问日志
        print("\n--- 步骤7: 权限验证 ---")
        print("  切换到普通用户...")
        user_login = self.auth.login("user", "admin123")
        if user_login.get("success"):
            user_token = user_login.get("data", {}).get("token")
            self.client.set_token(user_token)

            # 普通用户尝试查看操作日志
            user_log_resp = self.log.get_operation_logs(page=1, page_size=5)
            # 应该失败或返回空列表
            permission_success = (
                user_log_resp.get("status_code") == 403 or
                user_log_resp.get("data", {}).get("list", []) == []
            )
            results.append({
                "name": "步骤7: 权限验证",
                "passed": permission_success,
                "message": "普通用户权限控制正常" if permission_success else "权限检查异常"
            })
        else:
            results.append({
                "name": "步骤7: 权限验证",
                "passed": False,
                "message": "普通用户登录失败"
            })

        # 步骤8: 管理员删除测试数据
        print("\n--- 步骤8: 清理测试数据 ---")
        clean_success = True
        if self.created_customers:
            self.login_as_admin()  # 切换回管理员
            for cid in self.created_customers:
                try:
                    self.customer.delete(cid)
                    print(f"  ✅ 删除客户 {cid}")
                except:
                    clean_success = False
                    print(f"  ❌ 删除客户 {cid} 失败")

        results.append({
            "name": "步骤8: 数据清理",
            "passed": clean_success,
            "message": "清理完成" if clean_success else "部分清理失败"
        })

        return results


def run_complete_flow_test():
    """运行完整流程测试"""
    print("\n" + "="*60)
    print("🚀 启动端到端完整业务流程测试")
    print("="*60)

    # 检查服务器
    if not wait_for_server():
        return [{
            "name": "服务器检查",
            "passed": False,
            "message": "服务器未启动"
        }]

    suite = EndToEndTestSuite()
    results = suite.test_complete_workflow()

    # 汇总
    print("\n" + "="*60)
    print("📊 完整流程测试汇总")
    print("="*60)
    passed = sum(1 for r in results if r["passed"])
    total = len(results)

    for result in results:
        icon = "✅" if result["passed"] else "❌"
        print(f"{icon} {result['name']}: {result['message']}")

    print(f"\n总计: {total} | 通过: {passed} | 失败: {total - passed} | 成功率: {passed/total*100:.1f}%")

    # 保存详细报告
    save_test_report(results, "test/data/reports/complete_flow_report.md")

    return results


if __name__ == "__main__":
    run_complete_flow_test()
