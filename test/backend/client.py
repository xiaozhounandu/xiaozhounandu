"""
HTTP API 客户端封装
"""
import requests
import json
import time
from typing import Dict, Any, Optional
from backend.config import BASE_URL, TIMEOUT, RETRY_COUNT, RETRY_DELAY


class APIClient:
    """后端 API 客户端"""

    def __init__(self, base_url: str = BASE_URL):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        self.token = None
        self.headers = {
            "Content-Type": "application/json",
            "User-Agent": "AutoTest/1.0"
        }

    def request(self, method: str, endpoint: str, **kwargs) -> Dict[str, Any]:
        """
        发送 HTTP 请求，带重试机制

        Args:
            method: HTTP 方法 (GET, POST, PUT, DELETE)
            endpoint: API 端点 (如 /api/auth/login)
            **kwargs: requests 参数

        Returns:
            响应数据
        """
        url = f"{self.base_url}{endpoint}"
        headers = self.headers.copy()

        # 添加认证 token
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"

        # 合并请求配置
        kwargs.setdefault("timeout", TIMEOUT)
        kwargs.setdefault("headers", headers)

        # 重试逻辑
        last_error = None
        for retry in range(RETRY_COUNT):
            try:
                response = self.session.request(method, url, **kwargs)

                # 返回 JSON 格式
                if response.status_code != 204:  # 204 No Content 不返回数据
                    try:
                        return {
                            "status_code": response.status_code,
                            "data": response.json(),
                            "success": response.ok
                        }
                    except json.JSONDecodeError:
                        return {
                            "status_code": response.status_code,
                            "data": response.text,
                            "success": response.ok
                        }
                else:
                    return {
                        "status_code": response.status_code,
                        "data": None,
                        "success": True
                    }

            except requests.exceptions.RequestException as e:
                last_error = str(e)
                if retry < RETRY_COUNT - 1:
                    time.sleep(RETRY_DELAY)
                    continue
                else:
                    return {
                        "status_code": 0,
                        "data": f"请求失败: {last_error}",
                        "success": False
                    }

    def get(self, endpoint: str, params: Optional[Dict] = None) -> Dict[str, Any]:
        """GET 请求"""
        return self.request("GET", endpoint, params=params)

    def post(self, endpoint: str, json_data: Optional[Dict] = None) -> Dict[str, Any]:
        """POST 请求"""
        return self.request("POST", endpoint, json=json_data)

    def put(self, endpoint: str, json_data: Optional[Dict] = None) -> Dict[str, Any]:
        """PUT 请求"""
        return self.request("PUT", endpoint, json=json_data)

    def delete(self, endpoint: str) -> Dict[str, Any]:
        """DELETE 请求"""
        return self.request("DELETE", endpoint)

    def set_token(self, token: str):
        """设置认证 token"""
        self.token = token

    def clear_token(self):
        """清除认证 token"""
        self.token = None


class AuthClient:
    """认证相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def login(self, username: str, password: str) -> Dict[str, Any]:
        """用户登录"""
        return self.client.post("/api/auth/login", {
            "username": username,
            "password": password
        })

    def register(self, username: str, password: str, email: str = None) -> Dict[str, Any]:
        """用户注册"""
        data = {"username": username, "password": password}
        if email:
            data["email"] = email
        return self.client.post("/api/auth/register", data)

    def logout(self) -> Dict[str, Any]:
        """用户登出"""
        return self.client.post("/api/auth/logout", {})

    def get_current_user(self) -> Dict[str, Any]:
        """获取当前用户信息"""
        return self.client.get("/api/auth/current")


class CustomerClient:
    """客户管理相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def list(self, page: int = 1, page_size: int = 10, **filters) -> Dict[str, Any]:
        """获取客户列表"""
        params = {"page": page, "pageSize": page_size, **filters}
        return self.client.get("/api/customers", params=params)

    def get(self, customer_id: int) -> Dict[str, Any]:
        """获取客户详情"""
        return self.client.get(f"/api/customers/{customer_id}")

    def create(self, customer_data: Dict[str, Any]) -> Dict[str, Any]:
        """创建客户"""
        return self.client.post("/api/customers", customer_data)

    def update(self, customer_id: int, customer_data: Dict[str, Any]) -> Dict[str, Any]:
        """更新客户"""
        return self.client.put(f"/api/customers/{customer_id}", customer_data)

    def delete(self, customer_id: int) -> Dict[str, Any]:
        """删除客户"""
        return self.client.delete(f"/api/customers/{customer_id}")

    def transfer(self, customer_id: int, target_user_id: int) -> Dict[str, Any]:
        """转移客户"""
        return self.client.put(f"/api/customers/{customer_id}/transfer", {
            "newOwnerId": target_user_id
        })

    def get_available_owners(self) -> Dict[str, Any]:
        """获取可用负责人列表"""
        return self.client.get("/api/customers/available-owners")


class FollowUpClient:
    """跟进记录相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def list(self, page: int = 1, page_size: int = 10, **filters) -> Dict[str, Any]:
        """获取跟进列表"""
        params = {"page": page, "pageSize": page_size, **filters}
        return self.client.get("/api/followups", params=params)

    def get_by_customer(self, customer_id: int, page: int = 1, page_size: int = 10) -> Dict[str, Any]:
        """获取指定客户的跟进记录"""
        params = {"page": page, "pageSize": page_size}
        return self.client.get(f"/api/followups/customer/{customer_id}", params=params)

    def create(self, followup_data: Dict[str, Any]) -> Dict[str, Any]:
        """创建跟进记录"""
        return self.client.post("/api/followups", followup_data)

    def update(self, followup_id: int, followup_data: Dict[str, Any]) -> Dict[str, Any]:
        """更新跟进记录"""
        return self.client.put(f"/api/followups/{followup_id}", followup_data)

    def delete(self, followup_id: int) -> Dict[str, Any]:
        """删除跟进记录"""
        return self.client.delete(f"/api/followups/{followup_id}")


class StatsClient:
    """统计数据相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def get_dashboard(self) -> Dict[str, Any]:
        """获取仪表盘数据"""
        return self.client.get("/api/stats/dashboard")

    def get_stats(self) -> Dict[str, Any]:
        """获取统计信息"""
        return self.client.get("/api/stats")


class LogClient:
    """日志相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def get_operation_logs(self, page: int = 1, page_size: int = 10, **filters) -> Dict[str, Any]:
        """获取操作日志"""
        params = {"page": page, "pageSize": page_size, **filters}
        return self.client.get("/api/logs/operations", params=params)

    def get_login_logs(self, page: int = 1, page_size: int = 10, **filters) -> Dict[str, Any]:
        """获取登录日志"""
        params = {"page": page, "pageSize": page_size, **filters}
        return self.client.get("/api/logs/logins", params=params)


class InitClient:
    """初始化相关的 API"""

    def __init__(self, client: APIClient):
        self.client = client

    def get_status(self) -> Dict[str, Any]:
        """检查初始化状态"""
        return self.client.get("/api/init/status")

    def create_users(self) -> Dict[str, Any]:
        """创建测试用户"""
        return self.client.post("/api/init/create-users", {})


def create_test_client() -> APIClient:
    """创建测试客户端实例"""
    return APIClient()


print("✅ API 客户端封装完成")
