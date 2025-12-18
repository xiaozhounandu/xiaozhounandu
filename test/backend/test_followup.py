"""
测试模块: 跟进记录功能
"""
import sys
sys.path.append(".")

from backend.client import APIClient, AuthClient, CustomerClient, FollowUpClient
from backend.utils import print_test_result, validate_response, wait_for_server, generate_customer_data, generate_followup_data


class FollowUpTestSuite:
    """跟进记录测试套件"""

    def __init__(self):
        self.client = APIClient()
        self.auth = AuthClient(self.client)
        self.customer = CustomerClient(self.client)
        self.followup = FollowUpClient(self.client)

        self.test_customer_id = None
        self.test_followup_id = None

    def setup(self) -> bool:
        """准备测试环境"""
        # 1. 登录
        login_resp = self.auth.login("admin", "admin123")
        if not login_resp.get("success"):
            return False

        token = login_resp.get("data", {}).get("token") or login_resp.get("data", {}).get("data", {}).get("token")
        self.client.set_token(token)

        # 2. 创建测试客户
        customer_data = generate_customer_data()
        create_resp = self.customer.create(customer_data)

        if create_resp.get("success"):
            self.test_customer_id = create_resp.get("data", {}).get("id")
            print(f"✅ 测试客户已创建: ID={self.test_customer_id}")

        return self.test_customer_id is not None

    def test_create_followup(self) -> bool:
        """创建跟进记录"""
        print("\n=== 测试: 创建跟进记录 ===")

        followup_data = generate_followup_data()
        followup_data["customerId"] = self.test_customer_id

        response = self.followup.create(followup_data)
        success, message = validate_response(response)

        if success:
            self.test_followup_id = response.get("data", {}).get("id")
            print(f"  跟进ID: {self.test_followup_id}")

        print_test_result("创建跟进记录", success, message)
        return success

    def test_list_customer_followups(self) -> bool:
        """获取客户跟进列表"""
        print("\n=== 测试: 获取客户跟进列表 ===")

        if not self.test_customer_id:
            print("⚠️  未创建测试客户")
            return True

        response = self.followup.get_by_customer(self.test_customer_id, page=1, page_size=10)
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            list_data = data.get("list", [])
            print(f"  查找到 {len(list_data)} 条跟进记录")
            for item in list_data[:3]:
                print(f"    - [{item.get('type')}] {item.get('content')}")

        print_test_result("获取客户跟进列表", success, message)
        return success

    def test_list_all_followups(self) -> bool:
        """获取所有跟进记录"""
        print("\n=== 测试: 获取所有跟进记录 ===")

        response = self.followup.list(page=1, page_size=10)
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            list_data = data.get("list", [])
            print(f"  总跟进数: {data.get('total', 0)}")
            print(f"  本次返回: {len(list_data)} 条")

        print_test_result("获取所有跟进记录", success, message)
        return success

    def test_update_followup(self) -> bool:
        """更新跟进记录"""
        print("\n=== 测试: 更新跟进记录 ===")

        if not self.test_followup_id:
            print("⚠️  未创建测试跟进记录")
            return True

        update_data = {
            "type": "拜访",
            "content": "已更新的自动化测试跟进记录",
            "result": "已成单"
        }

        response = self.followup.update(self.test_followup_id, update_data)
        success, message = validate_response(response)

        print_test_result("更新跟进记录", success, message)
        return success

    def test_filter_followups(self) -> bool:
        """筛选跟进记录"""
        print("\n=== 测试: 筛选跟进记录 ===")

        # 按类型筛选
        response = self.followup.list(page=1, page_size=10, type="电话")
        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            list_data = data.get("list", [])
            print(f"  筛选结果: {len(list_data)} 条")

        print_test_result("筛选跟进记录", success, message)
        return success

    def test_delete_followup(self) -> bool:
        """删除跟进记录"""
        print("\n=== 测试: 删除跟进记录 ===")

        if not self.test_followup_id:
            print("⚠️  未创建测试跟进记录")
            return True

        response = self.followup.delete(self.test_followup_id)
        success, message = validate_response(response)

        print_test_result("删除跟进记录", success, message)
        return success

    def cleanup(self):
        """清理测试数据"""
        print("\n--- 清理测试数据 ---")
        if self.test_customer_id:
            try:
                self.customer.delete(self.test_customer_id)
                print(f"✅ 删除测试客户 {self.test_customer_id}")
            except:
                print(f"⚠️  删除客户失败")


def run_followup_tests():
    """运行跟进记录测试"""
    print("\n" + "="*60)
    print("🚀 开始运行跟进记录模块测试")
    print("="*60)

    if not wait_for_server():
        return [{"name": "服务器检查", "passed": False, "message": "服务器未启动"}]

    suite = FollowUpTestSuite()

    if not suite.setup():
        return [{"name": "环境准备", "passed": False, "message": "准备失败"}]

    tests = [
        ("创建跟进记录", suite.test_create_followup),
        ("获取客户跟进列表", suite.test_list_customer_followups),
        ("获取所有跟进记录", suite.test_list_all_followups),
        ("更新跟进记录", suite.test_update_followup),
        ("筛选跟进记录", suite.test_filter_followups),
        ("删除跟进记录", suite.test_delete_followup),
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

    # 清理
    suite.cleanup()

    # 汇总
    print("\n" + "="*60)
    print("📊 跟进记录测试汇总")
    print("="*60)
    passed = sum(1 for r in results if r["passed"])
    total = len(results)
    print(f"总计: {total} | 通过: {passed} | 失败: {total - passed} | 成功率: {passed/total*100:.1f}%")

    return results


if __name__ == "__main__":
    run_followup_tests()
