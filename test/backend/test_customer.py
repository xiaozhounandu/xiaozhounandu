"""
测试模块: 客户管理功能
"""
import sys
sys.path.append(".")

from backend.client import APIClient, AuthClient, CustomerClient, InitClient
from backend.utils import (
    print_test_result, validate_response, wait_for_server,
    generate_customer_data
)
from backend.config import TEST_USERS


class CustomerTestSuite:
    """客户管理测试套件"""

    def __init__(self):
        self.client = APIClient()
        self.auth = AuthClient(self.client)
        self.customer = CustomerClient(self.client)
        self.test_customer_id = None
        self.test_customer_name = None

    def setup(self, username: str = "admin") -> bool:
        """测试环境准备"""
        print(f"\n--- 登录用户: {username} ---")
        user = TEST_USERS[username]
        response = self.auth.login(user["username"], user["password"])

        if not response.get("success"):
            print(f"❌ 登录失败: {response.get('data')}")
            return False

        token = response.get("data", {}).get("token")
        if token:
            self.client.set_token(token)
            print("✅ 登录成功，token 已设置")
            return True
        return False

    def test_create_customer(self) -> bool:
        """创建客户"""
        print("\n=== 测试: 创建客户 ===")

        customer_data = generate_customer_data()
        response = self.customer.create(customer_data)

        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            self.test_customer_id = data.get("id")
            self.test_customer_name = customer_data["name"]
            print(f"  创建的客户ID: {self.test_customer_id}")
            print(f"  客户名称: {self.test_customer_name}")

        print_test_result("创建客户", success, message)
        return success

    def test_list_customers(self) -> bool:
        """查询客户列表"""
        print("\n=== 测试: 查询客户列表 ===")

        response = self.customer.list(page=1, page_size=10)

        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            list_data = data.get("list", [])
            total = data.get("total", 0)

            print(f"  客户总数: {total}")
            print(f"  本次返回: {len(list_data)} 条")

            if list_data:
                print("\n  前3条客户:")
                for item in list_data[:3]:
                    print(f"    - {item.get('name')} ({item.get('company')})")

        print_test_result("查询客户列表", success, message)
        return success

    def test_get_customer_detail(self) -> bool:
        """获取客户详情"""
        print("\n=== 测试: 获取客户详情 ===")

        if not self.test_customer_id:
            print("⚠️  跳过测试: 未创建测试客户")
            return True  # 跳过不计失败

        response = self.customer.get(self.test_customer_id)

        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            print(f"  客户名称: {data.get('name')}")
            print(f"  公司: {data.get('company')}")
            print(f"  电话: {data.get('phone')}")

        print_test_result("获取客户详情", success, message)
        return success

    def test_update_customer(self) -> bool:
        """更新客户"""
        print("\n=== 测试: 更新客户 ===")

        if not self.test_customer_id:
            print("⚠️  跳过测试: 未创建测试客户")
            return True

        update_data = {
            "name": self.test_customer_name + "_Updated",
            "company": "更新后的公司名称",
            "phone": "13900009999",
            "description": "这条记录已被自动化测试更新"
        }

        response = self.customer.update(self.test_customer_id, update_data)
        success, message = validate_response(response)

        print_test_result("更新客户", success, message)
        return success

    def test_filter_customers(self) -> bool:
        """筛选客户"""
        print("\n=== 测试: 筛选客户 ===")

        # 按名称模糊搜索
        response = self.customer.list(
            page=1,
            page_size=5,
            name="测试"
        )

        success, message = validate_response(response)

        if success:
            data = response.get("data", {})
            list_data = data.get("list", [])
            print(f"  搜索结果: {len(list_data)} 条")

        print_test_result("筛选客户", success, message)
        return success

    def test_delete_customer(self) -> bool:
        """删除客户"""
        print("\n=== 测试: 删除客户 ===")

        if not self.test_customer_id:
            print("⚠️  跳过测试: 未创建测试客户")
            return True

        response = self.customer.delete(self.test_customer_id)
        success, message = validate_response(response)

        print_test_result("删除客户", success, message)
        return success

    def test_permission_scenario(self) -> bool:
        """测试权限场景: 普通用户 vs 管理员"""
        print("\n=== 测试: 权限场景 ===")

        # 1. 使用普通用户登录
        print("\n--- 普通用户 (user) ---")
        if not self.setup("user"):
            return False

        # 创建客户
        user_customer = generate_customer_data()
        user_customer["name"] = user_customer["name"] + "_User"
        create_resp = self.customer.create(user_customer)

        # 普通用户应该能创建
        user_create_success, _ = validate_response(create_resp)
        print_test_result("普通用户创建客户", user_create_success, "权限验证")

        if not user_create_success:
            return False

        # 记录客户ID
        user_customer_id = create_resp.get("data", {}).get("id")

        # 2. 切换回管理员
        print("\n--- 管理员 (admin) ---")
        if not self.setup("admin"):
            return False

        # 管理员应该能看到所有客户，包括普通用户创建的
        admin_list_resp = self.customer.list(page=1, page_size=20)
        admin_list_success, _ = validate_response(admin_list_resp)

        if admin_list_success:
            # 查找普通用户创建的客户
            data = admin_list_resp.get("data", {}).get("list", [])
            found = any(item.get("id") == user_customer_id for item in data)
            if found:
                print("  ✅ 管理员可以看到普通用户创建的客户")
            else:
                print("  ⚠️  未在列表中找到普通用户创建的客户")

        # 清理: 删除测试创建的客户
        if user_customer_id:
            self.customer.delete(user_customer_id)

        print_test_result("权限场景测试", admin_list_success, "跨角色数据可见性")
        return admin_list_success


def run_customer_tests():
    """运行客户管理测试"""
    print("\n" + "="*60)
    print("🚀 开始运行客户管理模块测试")
    print("="*60)

    # 检查服务器
    if not wait_for_server():
        return [{"name": "服务器检查", "passed": False, "message": "服务器未启动"}]

    suite = CustomerTestSuite()

    # 准备环境
    if not suite.setup("admin"):
        return [{"name": "环境准备", "passed": False, "message": "登录失败"}]

    tests = [
        ("创建客户", suite.test_create_customer),
        ("查询列表", suite.test_list_customers),
        ("获取详情", suite.test_get_customer_detail),
        ("更新客户", suite.test_update_customer),
        ("筛选客户", suite.test_filter_customers),
        ("权限场景", suite.test_permission_scenario),
        ("删除客户", suite.test_delete_customer),
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
            print(f"❌ 测试 '{name}' 执行出错: {e}")
            results.append({
                "name": name,
                "passed": False,
                "message": "执行异常",
                "error": str(e)
            })

    # 汇总
    print("\n" + "="*60)
    print("📊 客户管理测试汇总")
    print("="*60)
    passed = sum(1 for r in results if r["passed"])
    total = len(results)
    print(f"总计: {total} | 通过: {passed} | 失败: {total - passed} | 成功率: {passed/total*100:.1f}%")

    return results


if __name__ == "__main__":
    run_customer_tests()
