-- ==================================================
-- 客户管理系统 - 快速生成剩余800条客户数据
-- 使用循环生成，简单高效
-- ==================================================

USE `xiaozhounandu_v2`;

-- 设置变量
SET @start_id = 201;
SET @end_id = 1000;

-- 使用存储过程批量生成
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS generate_customers()
BEGIN
    DECLARE i INT DEFAULT @start_id;
    DECLARE current_name VARCHAR(50);
    DECLARE current_company VARCHAR(200);
    DECLARE current_phone VARCHAR(20);
    DECLARE current_email VARCHAR(100);
    DECLARE current_industry VARCHAR(50);
    DECLARE current_position VARCHAR(50);
    DECLARE current_address VARCHAR(255);
    DECLARE current_status INT;
    DECLARE current_level VARCHAR(20);
    DECLARE current_owner_id INT;
    DECLARE current_creator_id INT;
    DECLARE current_remark TEXT;

    WHILE i <= @end_id DO
        -- 生成姓名
        SET current_name = CONCAT(
            CASE (i % 20)
                WHEN 0 THEN '张' WHEN 1 THEN '李' WHEN 2 THEN '王' WHEN 3 THEN '刘'
                WHEN 4 THEN '陈' WHEN 5 THEN '杨' WHEN 6 THEN '赵' WHEN 7 THEN '黄'
                WHEN 8 THEN '周' WHEN 9 THEN '吴' WHEN 10 THEN '徐' WHEN 11 THEN '孙'
                WHEN 12 THEN '马' WHEN 13 THEN '朱' WHEN 14 THEN '胡' WHEN 15 THEN '郭'
                WHEN 16 THEN '何' WHEN 17 THEN '高' WHEN 18 THEN '林' WHEN 19 THEN '罗'
                ELSE '郑'
            END,
            CASE (i % 20)
                WHEN 0 THEN '伟' WHEN 1 THEN '芳' WHEN 2 THEN '娜' WHEN 3 THEN '洋'
                WHEN 4 THEN '静' WHEN 5 THEN '磊' WHEN 6 THEN '敏' WHEN 7 THEN '强'
                WHEN 8 THEN '杰' WHEN 9 THEN '倩' WHEN 10 THEN '峰' WHEN 11 THEN '丽'
                WHEN 12 THEN '超' WHEN 13 THEN '萍' WHEN 14 THEN '军' WHEN 15 THEN '燕'
                WHEN 16 THEN '平' WHEN 17 THEN '翔' WHEN 18 THEN '琳' WHEN 19 THEN '涛'
                ELSE '雪'
            END
        );

        -- 生成公司
        SET current_company = CONCAT(
            CASE (i % 20)
                WHEN 0 THEN '北京' WHEN 1 THEN '上海' WHEN 2 THEN '深圳' WHEN 3 THEN '广州'
                WHEN 4 THEN '杭州' WHEN 5 THEN '成都' WHEN 6 THEN '武汉' WHEN 7 THEN '西安'
                WHEN 8 THEN '南京' WHEN 9 THEN '重庆' WHEN 10 THEN '天津' WHEN 11 THEN '苏州'
                WHEN 12 THEN '青岛' WHEN 13 THEN '长沙' WHEN 14 THEN '郑州' WHEN 15 THEN '福州'
                WHEN 16 THEN '厦门' WHEN 17 THEN '济南' WHEN 18 THEN '合肥' WHEN 19 THEN '石家庄'
                ELSE '太原'
            END,
            CASE (i % 10)
                WHEN 0 THEN '科技' WHEN 1 THEN '实业' WHEN 2 THEN '集团' WHEN 3 THEN '有限'
                WHEN 4 THEN '发展' WHEN 5 THEN '创新' WHEN 6 THEN '智能' WHEN 7 THEN '数字'
                WHEN 8 THEN '信息' WHEN 9 THEN '网络'
                ELSE '电子'
            END,
            '有限公司'
        );

        -- 生成电话
        SET current_phone = CONCAT(
            CASE (i % 3)
                WHEN 0 THEN '138' WHEN 1 THEN '139' ELSE '137'
            END,
            LPAD((i % 1000000000), 9, '0')
        );

        -- 生成邮箱
        SET current_email = CONCAT(
            LOWER(SUBSTRING_INDEX(current_name, ' ', 1)),
            i,
            CASE (i % 5)
                WHEN 0 THEN '@qq.com' WHEN 1 THEN '@163.com' WHEN 2 THEN '@gmail.com'
                WHEN 3 THEN '@outlook.com' ELSE '@126.com'
            END
        );

        -- 生成行业
        SET current_industry = CASE (i % 20)
            WHEN 0 THEN '互联网' WHEN 1 THEN '金融' WHEN 2 THEN '制造业' WHEN 3 THEN '房地产'
            WHEN 4 THEN '教育' WHEN 5 THEN '医疗' WHEN 6 THEN '零售' WHEN 7 THEN '物流'
            WHEN 8 THEN '能源' WHEN 9 THEN '化工' WHEN 10 THEN '农业' WHEN 11 THEN '建筑'
            WHEN 12 THEN '交通' WHEN 13 THEN '旅游' WHEN 14 THEN '文化传媒' WHEN 15 THEN '咨询服务'
            WHEN 16 THEN '法律服务' WHEN 17 THEN '会计审计' WHEN 18 THEN '广告公关' WHEN 19 THEN '食品饮料'
            ELSE '其他'
        END;

        -- 生成职位
        SET current_position = CASE (i % 15)
            WHEN 0 THEN 'CEO' WHEN 1 THEN 'CTO' WHEN 2 THEN 'CFO' WHEN 3 THEN '部门经理'
            WHEN 4 THEN '技术总监' WHEN 5 THEN '采购总监' WHEN 6 THEN '运营总监' WHEN 7 THEN '市场总监'
            WHEN 8 THEN '销售总监' WHEN 9 THEN '人事总监' WHEN 10 THEN '财务经理' WHEN 11 THEN '项目经理'
            WHEN 12 THEN '产品经理' WHEN 13 THEN '技术经理' WHEN 14 THEN '运营经理'
            ELSE '行政经理'
        END;

        -- 生成地址
        SET current_address = CONCAT(
            CASE (i % 35)
                WHEN 0 THEN '北京市朝阳区建国门外大街'
                WHEN 1 THEN '上海市浦东新区陆家嘴金融贸易区'
                WHEN 2 THEN '深圳市南山区科技园'
                WHEN 3 THEN '广州市天河区珠江新城'
                WHEN 4 THEN '杭州市西湖区文三路'
                WHEN 5 THEN '成都市高新区天府大道'
                WHEN 6 THEN '武汉市江汉区建设大道'
                WHEN 7 THEN '西安市雁塔区高新路'
                WHEN 8 THEN '南京市鼓楼区中山路'
                WHEN 9 THEN '重庆市渝北区新牌坊'
                WHEN 10 THEN '天津市南开区长江道'
                WHEN 11 THEN '苏州市工业园区星湖街'
                WHEN 12 THEN '青岛市市南区香港中路'
                WHEN 13 THEN '长沙市岳麓区麓山南路'
                WHEN 14 THEN '郑州市金水区农业路'
                WHEN 15 THEN '福州市鼓楼区五四路'
                WHEN 16 THEN '厦门市思明区湖滨北路'
                WHEN 17 THEN '济南市历下区经十路'
                WHEN 18 THEN '合肥市蜀山区长江西路'
                WHEN 19 THEN '石家庄市长安区中山东路'
                WHEN 20 THEN '太原市小店区平阳路'
                WHEN 21 THEN '呼和浩特市赛罕区大学西路'
                WHEN 22 THEN '沈阳市和平区南京街'
                WHEN 23 THEN '大连市中山区人民路'
                WHEN 24 THEN '长春市朝阳区人民大街'
                WHEN 25 THEN '哈尔滨市南岗区东大直街'
                WHEN 26 THEN '南昌市东湖区八一大道'
                WHEN 27 THEN '济南市市中区杆石桥'
                WHEN 28 THEN '福州市台江区五一中路'
                WHEN 29 THEN '南宁市青秀区民族大道'
                WHEN 30 THEN '海口市龙华区国贸大道'
                WHEN 31 THEN '成都市锦江区总府路'
                WHEN 32 THEN '贵阳市南明区中华南路'
                WHEN 33 THEN '昆明市五华区青年路'
                WHEN 34 THEN '拉萨市城关区江苏路'
                ELSE '西安市碑林区南大街'
            END,
            (i % 999 + 1),
            '号'
        );

        -- 生成状态
        SET current_status = CASE (i % 4)
            WHEN 0 THEN 1  -- 正常
            WHEN 1 THEN 1  -- 正常
            WHEN 2 THEN 2  -- 已成交
            ELSE 3          -- 已流失
        END;

        -- 生成等级
        SET current_level = CASE (i % 4)
            WHEN 0 THEN 'A' WHEN 1 THEN 'B' WHEN 2 THEN 'C' ELSE 'D'
        END;

        -- 生成归属人
        SET current_owner_id = (i % 7 + 1);

        -- 生成创建人
        SET current_creator_id = CASE (i % 3)
            WHEN 0 THEN 1 WHEN 1 THEN 2 ELSE 3
        END;

        -- 生成备注
        SET current_remark = CASE (i % 6)
            WHEN 0 THEN '意向强烈，需要尽快跟进'
            WHEN 1 THEN '对产品感兴趣，需技术演示'
            WHEN 2 THEN '价格谈判中，客户比较敏感'
            WHEN 3 THEN '已成交，持续服务中'
            WHEN 4 THEN '客户流失，竞品价格更低'
            ELSE '需要定期维护客户关系'
        END;

        -- 插入数据
        INSERT INTO `customer` (
            `name`, `company`, `phone`, `email`, `industry`, `position`,
            `address`, `source`, `status`, `level`, `owner_id`, `creator_id`,
            `remark`, `next_follow_time`, `create_time`, `update_time`
        ) VALUES (
            current_name, current_company, current_phone, current_email, current_industry, current_position,
            current_address, '线上', current_status, current_level, current_owner_id, current_creator_id,
            current_remark,
            CASE WHEN (i % 4) = 0 THEN NULL ELSE DATE_ADD(CURDATE(), INTERVAL (i % 30 + 1) DAY) END,
            DATE_ADD('2025-01-01', INTERVAL (i * 2) HOUR),
            DATE_ADD('2025-01-01', INTERVAL (i * 2 + 1) HOUR)
        );

        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

-- 执行存储过程生成数据
CALL generate_customers();

-- 删除存储过程
DROP PROCEDURE generate_customers;

-- 生成对应的跟进记录
INSERT INTO `follow_up` (`customer_id`, `follower_id`, `type`, `content`, `result`, `next_follow_time`, `create_time`)
SELECT
    id as customer_id,
    owner_id as follower_id,
    CASE (RAND() * 5)
        WHEN 0 THEN 'CALL'
        WHEN 1 THEN 'EMAIL'
        WHEN 2 THEN 'MEETING'
        WHEN 3 THEN 'WECHAT'
        ELSE 'OTHER'
    END as type,

    CASE (RAND() * 6)
        WHEN 0 THEN '电话沟通产品需求，客户表示很感兴趣'
        WHEN 1 THEN '发送产品资料和报价单'
        WHEN 2 THEN '上门拜访，进行产品演示'
        WHEN 3 THEN '技术方案讨论，解决客户疑问'
        WHEN 4 THEN '商务谈判，讨论合作细节'
        ELSE '售后服务，了解客户使用情况'
    END as content,

    CASE (RAND() * 6)
        WHEN 0 THEN '客户表示很有兴趣，约定下次沟通时间'
        WHEN 1 THEN '等待客户内部讨论和决策'
        WHEN 2 THEN '技术方案认可，进入商务谈判阶段'
        WHEN 3 THEN '合同已签订，准备实施阶段'
        WHEN 4 THEN '客户满意，继续深化合作'
        ELSE '需要进一步技术细节确认'
    END as result,

    DATE_ADD(create_time, INTERVAL (RAND() * 30 + 7) DAY) as next_follow_time,

    DATE_ADD(create_time, INTERVAL (RAND() * 15) DAY) as create_time

FROM customer
WHERE id >= 201
AND (RAND() * 10) < 6;  -- 约60%的客户有跟进记录

-- 显示统计信息
SELECT '=== 数据生成完成 ===' as 状态;
SELECT CONCAT('总客户数: ', COUNT(*)) as 客户统计 FROM customer;
SELECT CONCAT('新增客户数: ', COUNT(*)) as 新增统计 FROM customer WHERE id >= 201;
SELECT CONCAT('跟进记录数: ', COUNT(*)) as 跟进统计 FROM follow_up;
SELECT '现在可以查看数据看板和图表展示效果！' as 使用提示;