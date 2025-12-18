# ⚡ 快速参考指南 - 自动化测试框架

## 🎯 3分钟快速上手

```bash
# 1. 进入测试目录
cd /Users/weizhijie/Desktop/xiaozhounandu-main/test

# 2. 运行一键设置
./setup.sh

# 3. 启动后端 (IDE中运行 XiaozhounanduApplication.java)

# 4. 运行测试
./run_tests.sh      # 快速测试 (3模块)
./run_all.sh        # 完整测试 (5模块 + 报告)
```

---

## 📋 完整测试列表

|测试文件|测试用例数|描述|优先级|
|--------|----------|----|------|
|`test_auth.py`|5|登录、注册、授权、登出、权限拦截|P0|
|`test_customer.py`|7|增删改查、搜索、分页、软删除|P0|
|`test_followup.py`|6|创建、查询、更新、筛选、删除|P0|
|`test_stats_logs.py`|6|仪表盘、统计、操作日志、登录日志、权限、筛选|P1|
|`test_complete_flow.py`|1|完整业务流程端到端测试|P1|

**总计**: 25个测试用例

---

## 🔧 常用命令速查

### 运行测试
```bash
./setup.sh                    # 环境初始化
./run_tests.sh                # 快速测试
./run_all.sh                  # 完整测试 + 报告

# 单独模块
python3 backend/test_auth.py
python3 backend/test_customer.py
python3 backend/test_followup.py
python3 backend/test_stats_logs.py
python3 backend/test_complete_flow.py
```

### 环境检查
```bash
# 检查服务器
curl http://localhost:8080/api/init/status

# 检查数据库
mysql -u root -p -e "SHOW DATABASES;"

# 检查Python
python3 --version
python3 -c "import requests; print('requests OK')"
```

### 报告管理
```bash
# 查看报告
ls -lh data/reports/
cat data/reports/full_test_report_*.md

# 清理报告
rm -f data/reports/*.{md,log}
```

### 权限修复
```bash
chmod +x setup.sh run_tests.sh run_all.sh
```

---

## 🐛 常见问题速查

### 服务器未启动
```
❌ 后端服务器未启动
```
**修复**: 在IDE中启动 `XiaozhounanduApplication.java`

### 依赖缺失
```
ModuleNotFoundError: No module named 'requests'
```
**修复**: `pip3 install requests`

### 数据库连接失败
```
Communications link failure
```
**修复**: 确认MySQL运行且配置正确

### 0个测试通过
```
测试总数: 0 | 通过: 0 | 失败: 0
```
**修复**: 确认数据库有测试数据，使用 `./setup.sh`

---

## ✅ 成功标志

### 测试通过时的输出
```
📊 最终汇总
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
测试总数: 26
通过: 26
失败: 0
成功率: 100.0%

🎉 恭喜！所有测试通过！
```

---

## 📁 文件清单

```
test/
├── README.md              ← 详细文档 (阅读这个！)
├── QUICK_REFERENCE.md     ← 本文件 (快速参考)
├── requirements.txt       ← Python依赖
├── setup.sh              ← 环境初始化
├── run_tests.sh          ← 快速测试
├── run_all.sh            ← 完整测试
└── backend/
    ├── __init__.py
    ├── config.py          ← 配置
    ├── client.py          ← HTTP客户端
    ├── utils.py           ← 工具函数
    ├── test_auth.py       ← 认证测试
    ├── test_customer.py   ← 客户管理
    ├── test_followup.py   ← 跟进记录
    ├── test_stats_logs.py ← 统计日志
    └── test_complete_flow.py ← 完整流程
```

---

## 💡 快速提示

**首次运行**:
1. 运行 `./setup.sh`
2. 启动后端服务
3. 运行 `./run_tests.sh`

**发布前**:
1. 运行 `./run_all.sh`
2. 检查 `data/reports/` 下的报告
3. 所有测试必须100%通过

**调试问题**:
1. 单独运行问题模块
2. 查看详细输出
3. 检查 `data/reports/` 中的日志

---

**测试框架版本**: V2.0
**最后更新**: 2025-12-18
