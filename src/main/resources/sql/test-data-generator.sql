-- ==================================================
-- 客户管理系统 - 测试数据生成脚本
-- 数据库名: xiaozhounandu_v2
-- ==================================================

USE `xiaozhounandu_v2`;

-- 清理现有测试数据（保留基础用户）
DELETE FROM follow_up WHERE customer_id > 4;
DELETE FROM customer WHERE id > 4;


-- ==================================================
-- 1. 生成更多测试用户
-- ==================================================
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `role`, `status`) VALUES
('sales1', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '销售员张三', 'zhangsan@company.com', '13800138001', 'USER', 1),
('sales2', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '销售员李四', 'lisi@company.com', '13800138002', 'USER', 1),
('sales3', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '销售员王五', 'wangwu@company.com', '13800138003', 'USER', 1),
('manager2', '$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq', '区域经理', 'manager2@company.com', '13800138004', 'MANAGER', 1);

-- ==================================================
-- 2. 生成大量测试客户数据
-- ==================================================

-- 客户行业选项
SET @industries = '互联网,金融,制造业,房地产,教育,医疗,零售,物流,能源,化工,农业,建筑,交通,旅游,文化传媒,咨询服务,法律服务,会计审计,广告公关,食品饮料';

-- 客户等级选项 (A-VIP, B-重要, C-普通, D-低价值)
SET @levels = 'A,B,C,D';

-- 客户状态选项 (0-删除, 1-正常, 2-成交, 3-流失)
SET @statuses = '1,1,1,1,1,2,2,3'; -- 大部分正常，部分成交，少量流失

-- 客户来源选项
SET @sources = '线上,线下,推荐,展会,广告,电话营销,合作伙伴,其他';

-- 职位选项
SET @positions = 'CEO,CTO,CFO,部门经理,技术总监,采购总监,运营总监,市场总监,销售总监,人事总监,财务经理,项目经理,产品经理,技术经理';

-- 生成200个测试客户
INSERT INTO `customer` (
    `name`, `company`, `phone`, `email`, `industry`, `position`, `address`,
    `source`, `status`, `level`, `owner_id`, `creator_id`, `remark`, `next_follow_time`
)
SELECT
    CONCAT(
        CASE FLOOR(RAND() * 10)
            WHEN 0 THEN '张'
            WHEN 1 THEN '李'
            WHEN 2 THEN '王'
            WHEN 3 THEN '刘'
            WHEN 4 THEN '陈'
            WHEN 5 THEN '杨'
            WHEN 6 THEN '赵'
            WHEN 7 THEN '黄'
            WHEN 8 THEN '周'
            ELSE '吴'
        END,
        CASE FLOOR(RAND() * 10)
            WHEN 0 THEN '伟'
            WHEN 1 THEN '芳'
            WHEN 2 THEN '娜'
            WHEN 3 THEN '秀英'
            WHEN 4 THEN '敏'
            WHEN 5 THEN '静'
            WHEN 6 THEN '丽'
            WHEN 7 THEN '强'
            WHEN 8 THEN '磊'
            ELSE '军'
        END
    ) as name,
    CONCAT(
        CASE FLOOR(RAND() * 5)
            WHEN 0 THEN '上海'
            WHEN 1 THEN '北京'
            WHEN 2 THEN '深圳'
            WHEN 3 THEN '广州'
            ELSE '杭州'
        END,
        CASE FLOOR(RAND() * 5)
            WHEN 0 THEN '科技'
            WHEN 1 THEN '实业'
            WHEN 2 THEN '集团'
            WHEN 3 THEN '有限'
            ELSE '股份'
        END,
        '公司'
    ) as company,
    CONCAT('1', FLOOR(RAND() * 900000000 + 100000000)) as phone,
    CONCAT(
        SUBSTRING_INDEX(SUBSTRING_INDEX(xiaozhounandu_v2.customer.name, ' ', 1), ' ', -1),
        LOWER(
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN '@qq.com'
                WHEN 1 THEN '@163.com'
                ELSE '@gmail.com'
            END
        )
    ) as email,
    SUBSTRING_INDEX(@industries, ',', FLOOR(RAND() * 20) + 1) as industry,
    SUBSTRING_INDEX(@positions, ',', FLOOR(RAND() * 14) + 1) as position,
    CONCAT(
        CASE FLOOR(RAND() * 10)
            WHEN 0 THEN '北京市朝阳区'
            WHEN 1 THEN '上海市浦东新区'
            WHEN 2 THEN '深圳市南山区'
            WHEN 3 THEN '广州市天河区'
            WHEN 4 THEN '杭州市西湖区'
            WHEN 5 THEN '成都市高新区'
            WHEN 6 THEN '武汉市江汉区'
            WHEN 7 THEN '西安市雁塔区'
            WHEN 8 THEN '南京市鼓楼区'
            ELSE '重庆市渝北区'
        END,
        FLOOR(RAND() * 999 + 1),
        '号'
    ) as address,
    SUBSTRING_INDEX(@sources, ',', FLOOR(RAND() * 8) + 1) as source,
    CAST(SUBSTRING_INDEX(@statuses, ',', FLOOR(RAND() * 8) + 1) AS UNSIGNED) as status,
    SUBSTRING_INDEX(@levels, ',', FLOOR(RAND() * 4) + 1) as level,
    (FLOOR(RAND() * 7) + 1) as owner_id, -- 随机分配给7个用户
    (FLOOR(RAND() * 7) + 1) as creator_id,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN '意向强烈，需要尽快跟进'
        WHEN 1 THEN '对产品感兴趣，需技术演示'
        WHEN 2 THEN '价格谈判中，客户比较敏感'
        WHEN 3 THEN '已成交，持续服务中'
        ELSE '客户流失，竞品价格更低'
    END as remark,
    CASE
        WHEN RAND() > 0.3 THEN DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)
        ELSE NULL
    END as next_follow_time
FROM (
    SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50 UNION
    SELECT 51 UNION SELECT 52 UNION SELECT 53 UNION SELECT 54 UNION SELECT 55 UNION
    SELECT 56 UNION SELECT 57 UNION SELECT 58 UNION SELECT 59 UNION SELECT 60 UNION
    SELECT 61 UNION SELECT 62 UNION SELECT 63 UNION SELECT 64 UNION SELECT 65 UNION
    SELECT 66 UNION SELECT 67 UNION SELECT 68 UNION SELECT 69 UNION SELECT 70 UNION
    SELECT 71 UNION SELECT 72 UNION SELECT 73 UNION SELECT 74 UNION SELECT 75 UNION
    SELECT 76 UNION SELECT 77 UNION SELECT 78 UNION SELECT 79 UNION SELECT 80 UNION
    SELECT 81 UNION SELECT 82 UNION SELECT 83 UNION SELECT 84 UNION SELECT 85 UNION
    SELECT 86 UNION SELECT 87 UNION SELECT 88 UNION SELECT 89 UNION SELECT 90 UNION
    SELECT 91 UNION SELECT 92 UNION SELECT 93 UNION SELECT 94 UNION SELECT 95 UNION
    SELECT 96 UNION SELECT 97 UNION SELECT 98 UNION SELECT 99 UNION SELECT 100 UNION
    SELECT 101 UNION SELECT 102 UNION SELECT 103 UNION SELECT 104 UNION SELECT 105 UNION
    SELECT 106 UNION SELECT 107 UNION SELECT 108 UNION SELECT 109 UNION SELECT 110 UNION
    SELECT 111 UNION SELECT 112 UNION SELECT 113 UNION SELECT 114 UNION SELECT 115 UNION
    SELECT 116 UNION SELECT 117 UNION SELECT 118 UNION SELECT 119 UNION SELECT 120 UNION
    SELECT 121 UNION SELECT 122 UNION SELECT 123 UNION SELECT 124 UNION SELECT 125 UNION
    SELECT 126 UNION SELECT 127 UNION SELECT 128 UNION SELECT 129 UNION SELECT 130 UNION
    SELECT 131 UNION SELECT 132 UNION SELECT 133 UNION SELECT 134 UNION SELECT 135 UNION
    SELECT 136 UNION SELECT 137 UNION SELECT 138 UNION SELECT 139 UNION SELECT 140 UNION
    SELECT 141 UNION SELECT 142 UNION SELECT 143 UNION SELECT 144 UNION SELECT 145 UNION
    SELECT 146 UNION SELECT 147 UNION SELECT 148 UNION SELECT 149 UNION SELECT 150 UNION
    SELECT 151 UNION SELECT 152 UNION SELECT 153 UNION SELECT 154 UNION SELECT 155 UNION
    SELECT 156 UNION SELECT 157 UNION SELECT 158 UNION SELECT 159 UNION SELECT 160 UNION
    SELECT 161 UNION SELECT 162 UNION SELECT 163 UNION SELECT 164 UNION SELECT 165 UNION
    SELECT 166 UNION SELECT 167 UNION SELECT 168 UNION SELECT 169 UNION SELECT 170 UNION
    SELECT 171 UNION SELECT 172 UNION SELECT 173 UNION SELECT 174 UNION SELECT 175 UNION
    SELECT 176 UNION SELECT 177 UNION SELECT 178 UNION SELECT 179 UNION SELECT 180 UNION
    SELECT 181 UNION SELECT 182 UNION SELECT 183 UNION SELECT 184 UNION SELECT 185 UNION
    SELECT 186 UNION SELECT 187 UNION SELECT 188 UNION SELECT 189 UNION SELECT 190 UNION
    SELECT 191 UNION SELECT 192 UNION SELECT 193 UNION SELECT 194 UNION SELECT 195 UNION
    SELECT 196 UNION SELECT 197 UNION SELECT 198 UNION SELECT 199 UNION SELECT 200
) as numbers;

-- ==================================================
-- 3. 生成跟进记录
-- ==================================================

INSERT INTO `follow_up` (`customer_id`, `follower_id`, `type`, `content`, `result`, `next_follow_time`)
SELECT
    c.id as customer_id,
    c.owner_id as follower_id,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN 'CALL'
        WHEN 1 THEN 'EMAIL'
        WHEN 2 THEN 'MEETING'
        WHEN 3 THEN 'WECHAT'
        ELSE 'OTHER'
    END as type,
    CASE FLOOR(RAND() * 6)
        WHEN 0 THEN '电话沟通产品需求，客户表示很感兴趣'
        WHEN 1 THEN '发送产品资料和报价单'
        WHEN 2 THEN '上门拜访，进行产品演示'
        WHEN 3 THEN '技术方案讨论，解决客户疑问'
        WHEN 4 THEN '商务谈判，讨论合作细节'
        ELSE '售后服务，了解客户使用情况'
    END as content,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN '客户表示很有兴趣，约定下次沟通时间'
        WHEN 1 THEN '等待客户内部讨论和决策'
        WHEN 2 THEN '技术方案认可，进入商务谈判阶段'
        WHEN 3 THEN '合同已签订，准备实施阶段'
        ELSE '客户满意，继续深化合作'
    END as result,
    DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 60) DAY) as next_follow_time
FROM customer c
WHERE c.id > 4
AND FLOOR(RAND() * 3) > 0; -- 约2/3的客户有跟进记录

-- ==================================================
-- 4. 生成登录日志
-- ==================================================

INSERT INTO `login_log` (`user_id`, `username`, `ip_address`, `device`, `location`, `result`, `error_msg`)
SELECT
    u.id,
    u.username,
    CONCAT(
        FLOOR(RAND() * 255 + 1),
        '.',
        FLOOR(RAND() * 255 + 1),
        '.',
        FLOOR(RAND() * 255 + 1),
        '.',
        FLOOR(RAND() * 255 + 1)
    ) as ip_address,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'Chrome/Windows'
        WHEN 1 THEN 'Safari/Mac'
        ELSE 'Mobile/Android'
    END as device,
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN '北京市'
        WHEN 1 THEN '上海市'
        WHEN 2 THEN '深圳市'
        WHEN 3 THEN '广州市'
        WHEN 4 THEN '杭州市'
        WHEN 5 THEN '成都市'
        WHEN 6 THEN '武汉市'
        WHEN 7 THEN '西安市'
        WHEN 8 THEN '南京市'
        ELSE '重庆市'
    END as location,
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN 0
        ELSE 1
    END as result,
    CASE FLOOR(RAND() * 10)
        WHEN 0 THEN '密码错误'
        ELSE NULL
    END as error_msg
FROM user u,
(
    SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) as login_counts;

-- ==================================================
-- 5. 生成操作日志
-- ==================================================

INSERT INTO `operation_log` (`user_id`, `module`, `operation`, `target_id`, `target_name`, `ip_address`, `result`, `after_data`)
SELECT
    u.id,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'customer'
        WHEN 1 THEN 'followup'
        WHEN 2 THEN 'user'
        ELSE 'system'
    END as module,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'CREATE'
        WHEN 1 THEN 'UPDATE'
        WHEN 2 THEN 'DELETE'
        ELSE 'VIEW'
    END as operation,
    CASE
        WHEN FLOOR(RAND() * 4) = 0 THEN c.id
        ELSE NULL
    END as target_id,
    CASE
        WHEN FLOOR(RAND() * 4) = 0 THEN c.name
        ELSE NULL
    END as target_name,
    '127.0.0.1' as ip_address,
    1 as result,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN JSON_OBJECT('name', c.name, 'status', c.status)
        WHEN 1 THEN JSON_OBJECT('content', '跟进内容', 'type', 'CALL')
        ELSE JSON_OBJECT('operation', 'success')
    END as after_data
FROM user u, customer c,
(
    SELECT 1 as n UNION SELECT 2 UNION SELECT 3
) as op_counts
WHERE u.id <= 3 AND c.id <= 10;

-- ==================================================
-- 6. 数据统计验证
-- ==================================================

SELECT '=== 数据统计 ===' as 统计信息;
SELECT
    '用户总数' as 项目,
    COUNT(*) as 数量
FROM user
UNION ALL
SELECT
    '客户总数' as 项目,
    COUNT(*) as 数量
FROM customer
UNION ALL
SELECT
    '跟进记录总数' as 项目,
    COUNT(*) as 数量
FROM follow_up
UNION ALL
SELECT
    '今日跟进任务' as 项目,
    COUNT(*) as 数量
FROM follow_up
WHERE DATE(next_follow_time) = CURDATE()
UNION ALL
SELECT
    '本月新增客户' as 项目,
    COUNT(*) as 数量
FROM customer
WHERE YEAR(create_time) = YEAR(CURDATE())
AND MONTH(create_time) = MONTH(CURDATE());

-- 行业分布统计
SELECT '=== 行业分布 ===' as 统计信息;
SELECT
    industry as 行业,
    COUNT(*) as 客户数,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM customer), 2) as 占比百分比
FROM customer
GROUP BY industry
ORDER BY COUNT(*) DESC;

-- 客户等级分布
SELECT '=== 客户等级分布 ===' as 统计信息;
SELECT
    level as 等级,
    COUNT(*) as 客户数,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM customer), 2) as 占比百分比
FROM customer
GROUP BY level
ORDER BY level;

-- 客户状态分布
SELECT '=== 客户状态分布 ===' as 统计信息;
SELECT
    CASE status
        WHEN 0 THEN '删除'
        WHEN 1 THEN '正常'
        WHEN 2 THEN '成交'
        WHEN 3 THEN '流失'
    END as 状态,
    COUNT(*) as 客户数,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM customer), 2) as 占比百分比
FROM customer
GROUP BY status;

-- 月度客户增长趋势（最近12个月）
SELECT '=== 月度客户增长趋势 ===' as 统计信息;
SELECT
    DATE_FORMAT(create_time, '%Y-%m') as 月份,
    COUNT(*) as 新增客户数,
    SUM(COUNT(*)) OVER (ORDER BY DATE_FORMAT(create_time, '%Y-%m')) as 累计客户数
FROM customer
WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
GROUP BY DATE_FORMAT(create_time, '%Y-%m')
ORDER BY 月份;

-- 近7天每日新增客户
SELECT '=== 近7天每日新增客户 ===' as 统计信息;
SELECT
    DATE(create_time) as 日期,
    COUNT(*) as 新增客户数
FROM customer
WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(create_time)
ORDER BY 日期;

SELECT '=== 测试数据生成完成 ===' as 完成信息;
SELECT '现在可以查看数据看板和图表展示效果！' as 提示信息;