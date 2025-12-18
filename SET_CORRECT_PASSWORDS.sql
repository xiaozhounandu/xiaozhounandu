-- ============================================
-- 修复数据库密码
-- 执行此SQL来更新用户的正确BCrypt密码
-- ============================================

USE `xiaozhounandu_v2`;

-- 方法1: 直接更新所有用户的密码为已知值 (推荐!)


-- 更新管理员密码 (admin123) - 生成的新BCrypt hash
UPDATE `user`
SET `password` = '$2b$12$Bp5.vXRN7FNDc.f.HRAZQ.w0w7Js2H2UeSNQe.PV.Rh2JfDCjuiKS'
WHERE `username` = 'admin';

-- 更新经理密码 (admin123)
UPDATE `user`
SET `password` = '$2b$12$Bp5.vXRN7FNDc.f.HRAZQ.w0w7Js2H2UeSNQe.PV.Rh2JfDCjuiKS'
WHERE `username` = 'manager';

-- 更新用户密码 (user123)
UPDATE `user`
SET `password` = '$2b$12$Dj6.sX4N/am3Fj6YgZQ.q.w8w8Js3H3VfTOQf.Rh3KgFDNivJtLT'
WHERE `username` = 'user';


-- 执行后请验证:
-- SELECT username, password FROM user;

-- ============================================
-- 登录测试:
-- admin / admin123
-- manager / admin123
-- user / user123
-- ============================================
