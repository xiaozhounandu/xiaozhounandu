"""
测试模块: 认证相关功能
"""
import sys
sys.path.append(".")

from backend.client import APIClient, AuthClient, InitClient
from backend.utils import print_test_result, validate_response, wait_for_server
from backend.config import TEST_USERS


def test_server_ready():
    """测试1: 服务器是否就绪"""
    print("\n=== 测试1: 检查服务器状态 ===")

    if not wait_for_server():
        print("❌ 服务器未启动，无法继续测试")
        return False

    client = APIClient()
    init_client = InitClient(client)

    # 测试 init/status 接口
    response = init_client.get_status()
    success, message = validate_response(response)

    print_test_result("服务器状态检查", success, message)

    # 如果管理员不存在，创建用户
    if success:
        data = response.get("data", {})
        if not data.get("adminExists", False):
            print("⚠️  管理员用户不存在，正在初始化...")
            create_resp = init_client.create_users()
            create_success, create_msg = validate_response(create_resp)
            print_test_result("初始化测试用户", create_success, create_msg)

    return success


def test_login_success():
    """测试2: 正常登录"""
    print("\n=== 测试2: 正常登录 ===")

    client = APIClient()
    auth_client = AuthClient(client)

    test_cases = ["admin", "manager", "user"]
    all_passed = True

    for username in test_cases:
        user_info = TEST_USERS[username]
        response = auth_client.login(user_info["username"], user_info["password"])

        success, message = validate_response(response)

        # 验证返回的 token
        if success and response.get("data"):
            token = response["data"].get("token") or response["data"].get("data", {}).get("token")
            if token:
                print_test_result(f"用户 {username} 登录", True, "获取 token 成功")
            else:
                print_test_result(f"用户 {username} 登录", False, "未返回 token")
                all_passed = False
        else:
            print_test_result(f"用户 {username} 登录", False, message)
            all_passed = False

    return all_passed


def test_login_failure():
    """测试3: 登录失败场景"""
    print("\n=== 测试3: 登录失败场景 ===")

    client = APIClient()
    auth_client = AuthClient(client)

    test_cases = [
        ("错误密码", "admin", "wrongpassword"),
        ("不存在的用户", "nonexistent", "password"),
        ("空密码", "admin", ""),
    ]

    all_passed = True

    for case_name, username, password in test_cases:
        response = auth_client.login(username, password)

        # 登录失败应该返回非200状态码或success=false
        success = not response.get("success", True) or response["status_code"] != 200

        print_test_result(f"登录失败: {case_name}", success,
                         "正确拒绝" if success else "未正确处理")

        if not success:
            all_passed = False

    return all_passed


def test_register():
    """测试4: 用户注册"""
    print("\n=== 测试4: 用户注册 ===")

    client = APIClient()
    auth_client = AuthClient(client)

    timestamp = str(int(__import__('time').time()))
    new_username = f"testuser_{timestamp}"
    new_password = "test123"
    new_email = f"test_{timestamp}@example.com"

    # 注册
    response = auth_client.register(new_username, new_password, new_email)
    success, message = validate_response(response)

    print_test_result("用户注册", success, message)

    if success:
        # 尝试用新用户登录
        login_response = auth_client.login(new_username, new_password)
        login_success, login_msg = validate_response(login_response)
        print_test_result("注册后登录", login_success, login_msg)
        return login_success

    return False


def test_current_user():
    """测试5: 获取当前用户信息"""
    print("\n=== 测试5: 获取当前用户信息 ===")

    client = APIClient()
    auth_client = AuthClient(client)

    # 先登录
    login_resp = auth_client.login("admin", "admin123")
    if not login_resp.get("success"):
        print_test_result("获取当前用户", False, "登录失败")
        return False

    # 设置 token
    token = login_resp.get("data", {}).get("token") or login_resp.get("data", {}).get("data", {}).get("token")
    if not token:
        # 某些接口可能返回不同格式
        print_test_result("获取 token", False, f"返回格式异常: {login_resp}")
        return False

    client.set_token(token)

    # 获取当前用户
    response = auth_client.get_current_user()
    success, message = validate_response(response)

    if success:
        user_data = response.get("data", {})
        print(f"  当前用户: {user_data.get('username')} (角色: {user_data.get('role')})")

    print_test_result("获取当前用户信息", success, message)
    return success


def run_all_auth_tests():
    """运行所有认证测试"""
    print("\n" + "="*60)
    print("🚀 开始运行认证模块测试")
    print("="*60)

    results = []

    tests = [
        ("服务器就绪检查", test_server_ready),
        ("正常登录", test_login_success),
        ("登录失败场景", test_login_failure),
        ("用户注册", test_register),
        ("当前用户信息", test_current_user),
    ]

    for test_name, test_func in tests:
        try:
            result = test_func()
            results.append({
                "name": test_name,
                "passed": result,
                "message": "通过" if result else "失败"
            })
        except Exception as e:
            print(f"❌ 测试 '{test_name}' 执行出错: {e}")
            results.append({
                "name": test_name,
                "passed": False,
                "message": "执行异常",
                "error": str(e)
            })

    # 汇总
    print("\n" + "="*60)
    print("📊 认证模块测试汇总")
    print("="*60)
    passed = sum(1 for r in results if r["passed"])
    total = len(results)
    print(f"总计: {total} | 通过: {passed} | 失败: {total - passed} | 成功率: {passed/total*100:.1f}%")

    return results


if __name__ == "__main__":
    run_all_auth_tests()
