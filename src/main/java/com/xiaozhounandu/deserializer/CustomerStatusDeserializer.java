package com.xiaozhounandu.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 客户状态反序列化器
 * 处理字符串状态值转换为数字
 */
public class CustomerStatusDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            return 1; // 默认正常状态
        }

        // 如果已经是数字，直接返回
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 处理字符串状态值
            switch (value.toLowerCase()) {
                case "potential":
                case "潜在客户":
                    return 1; // 正常
                case "contacted":
                case "已联系":
                    return 1; // 正常
                case "negotiating":
                case "谈判中":
                    return 1; // 正常
                case "active":
                case "活跃":
                    return 1; // 正常
                case "success":
                case "已成交":
                case "成交":
                    return 2; // 已成交
                case "failed":
                case "已流失":
                case "流失":
                    return 3; // 已流失
                case "deleted":
                case "删除":
                    return 0; // 删除
                default:
                    return 1; // 默认正常状态
            }
        }
    }
}