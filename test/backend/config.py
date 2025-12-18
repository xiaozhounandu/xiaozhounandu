"""
测试配置文件
"""

# API 基础配置
BASE_URL = "http://localhost:8080"
API_VERSION = "v1"

# 测试账号
TEST_USERS = {
    "admin": {
        "username": "admin",
        "password": "admin123",
        "role": "ADMIN",
        "expected_status": 200
    },
    "manager": {
        "username": "manager",
        "password": "admin123",
        "role": "MANAGER",
        "expected_status": 200
    },
    "user": {
        "username": "user",
        "password": "admin123",
        "role": "USER",
        "expected_status": 200
    },
    "wrong_password": {
        "username": "admin",
        "password": "wrong",
        "expected_status": 401
    },
    "nonexistent": {
        "username": "notexist",
        "password": "test",
        "expected_status": 401
    }
}

# 测试客户数据模板
CUSTOMER_TEMPLATE = {
    "name": "测试客户_{timestamp}",
    "company": "测试公司_{timestamp}",
    "phone": "13800138000",
    "email": "test_{timestamp}@example.com",
    "level": "VIP",
    "industry": "IT",
    "address": "测试地址",
    "description": "自动化测试创建的客户"
}

# 测试跟进记录模板
FOLLOWUP_TEMPLATE = {
    "type": "电话",
    "content": "自动化测试跟进记录_{timestamp}",
    "result": "良好",
    "nextFollowTime": "2025-12-25 10:00:00"
}

# 日志查询参数
LOG_QUERY_PARAMS = {
    "username": "",
    "result": None,
    "startDate": "2025-12-01",
    "endDate": "2025-12-31",
    "page": 1,
    "pageSize": 10
}

# 超时配置
TIMEOUT = 10  # HTTP 请求超时时间(秒)

# 重试配置
RETRY_COUNT = 3
RETRY_DELAY = 1  # 秒

# 测试数据生成配置
CUSTOMER_NAME_PREFIX = "自动化测试客户"
FOLLOWUP_CONTENT_PREFIX = "自动化测试跟进"

print("✅ 测试配置已加载")
print(f"   API地址: {BASE_URL}")
print(f"   测试账号: {list(TEST_USERS.keys())}")
