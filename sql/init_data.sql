-- =============================================
-- 个人博客系统初始化数据脚本
-- 用于插入系统运行所需的基础数据
-- =============================================

USE `Blogs`;

-- =============================================
-- 1. 创建默认管理员用户
-- =============================================
-- 密码: admin123 (实际使用时应该使用加密后的密码)
INSERT INTO `tb_user` (`username`, `password`, `email`, `nickname`, `bio`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.GfaAUKyG', 'admin@example.com', '系统管理员', '系统默认管理员账户', 1);

-- 获取刚插入的用户ID
SET @admin_user_id = LAST_INSERT_ID();

-- 为管理员分配超级管理员角色
INSERT INTO `tb_user_role` (`user_id`, `role_id`) VALUES
(@admin_user_id, 1);

-- 初始化管理员积分
INSERT INTO `tb_user_coin` (`user_id`, `total_coin`, `available_coin`, `frozen_coin`) VALUES
(@admin_user_id, 1000, 1000, 0);

-- =============================================
-- 2. 创建示例普通用户
-- =============================================
-- 密码: user123
INSERT INTO `tb_user` (`username`, `password`, `email`, `nickname`, `bio`, `status`) VALUES
('testuser', '$2a$10$7XALq9a4lE5Z8K9mN2Bp4eJ.Hn8fGg5Kp3Qr7Ws9Vt2Xu6Yz1Ab3Cd', 'user@example.com', '测试用户', '这是一个测试用户账户', 1);

-- 获取测试用户ID
SET @test_user_id = LAST_INSERT_ID();

-- 为测试用户分配普通用户角色
INSERT INTO `tb_user_role` (`user_id`, `role_id`) VALUES
(@test_user_id, 3);

-- 初始化测试用户积分
INSERT INTO `tb_user_coin` (`user_id`, `total_coin`, `available_coin`, `frozen_coin`) VALUES
(@test_user_id, 100, 100, 0);

-- =============================================
-- 3. 创建示例文章
-- =============================================
INSERT INTO `tb_post` (`title`, `summary`, `content`, `author_id`, `category_id`, `status`, `view_count`, `like_count`) VALUES
('欢迎使用个人博客系统', '这是系统的第一篇文章，介绍了博客系统的基本功能。', 
'# 欢迎使用个人博客系统

这是一个基于Spring Boot 3开发的个人博客系统，具有以下特性：

## 主要功能
- 文章发布与管理
- 分类和标签系统
- 评论功能
- 文件上传
- 用户积分系统
- 管理员后台

## 技术栈
- Spring Boot 3
- MyBatis-Plus
- MySQL 8
- Vue.js (前端)

感谢您的使用！', 
@admin_user_id, 1, 1, 0, 0),

('Spring Boot 3 新特性介绍', 'Spring Boot 3 带来了许多新特性和改进，本文将详细介绍。',
'# Spring Boot 3 新特性介绍

Spring Boot 3 是一个重要的版本更新，带来了许多新特性：

## 主要更新
1. **Java 17 基线要求**
2. **原生镜像支持**
3. **观察性改进**
4. **Jakarta EE 迁移**

## 性能提升
- 启动时间优化
- 内存使用优化
- 编译时优化

更多详细内容请参考官方文档。',
@admin_user_id, 1, 1, 0, 0);

-- =============================================
-- 4. 为文章添加标签
-- =============================================
-- 获取文章ID
SET @post1_id = (SELECT id FROM tb_post WHERE title = '欢迎使用个人博客系统' LIMIT 1);
SET @post2_id = (SELECT id FROM tb_post WHERE title = 'Spring Boot 3 新特性介绍' LIMIT 1);

-- 为第一篇文章添加标签
INSERT INTO `tb_post_tag` (`post_id`, `tag_id`) VALUES
(@post1_id, 1), -- Java
(@post1_id, 2), -- Spring Boot
(@post1_id, 3); -- MySQL

-- 为第二篇文章添加标签
INSERT INTO `tb_post_tag` (`post_id`, `tag_id`) VALUES
(@post2_id, 1), -- Java
(@post2_id, 2); -- Spring Boot

-- =============================================
-- 5. 创建示例评论
-- =============================================
INSERT INTO `tb_comment` (`post_id`, `user_id`, `content`, `status`) VALUES
(@post1_id, @test_user_id, '很棒的博客系统，期待更多功能！', 1),
(@post2_id, @test_user_id, 'Spring Boot 3 确实带来了很多改进，感谢分享！', 1);

-- =============================================
-- 6. 更新统计数据
-- =============================================
-- 更新分类文章数量
UPDATE `tb_category` SET `post_count` = (
    SELECT COUNT(*) FROM `tb_post` 
    WHERE `category_id` = `tb_category`.`id` AND `is_deleted` = 0 AND `status` = 1
);

-- 更新标签使用次数
UPDATE `tb_tag` SET `use_count` = (
    SELECT COUNT(*) FROM `tb_post_tag` 
    WHERE `tag_id` = `tb_tag`.`id`
);

-- =============================================
-- 7. 创建积分记录
-- =============================================
INSERT INTO `tb_coin` (`user_id`, `amount`, `operation_type`, `description`, `related_id`) VALUES
(@admin_user_id, 10, 1, '发布文章：欢迎使用个人博客系统', @post1_id),
(@admin_user_id, 10, 1, '发布文章：Spring Boot 3 新特性介绍', @post2_id),
(@test_user_id, 2, 2, '发表评论', (SELECT id FROM tb_comment WHERE user_id = @test_user_id LIMIT 1));

-- =============================================
-- 8. 创建管理员操作日志示例
-- =============================================
INSERT INTO `tb_admin_log` (`user_id`, `username`, `module`, `operation_type`, `description`, `method`, `url`, `ip_address`, `result`) VALUES
(@admin_user_id, 'admin', '用户管理', 1, '创建系统初始化数据', 'POST', '/api/admin/init', '127.0.0.1', 1),
(@admin_user_id, 'admin', '文章管理', 1, '发布文章：欢迎使用个人博客系统', 'POST', '/api/posts', '127.0.0.1', 1);

-- =============================================
-- 9. 验证数据完整性
-- =============================================
-- 检查数据是否正确插入
SELECT 
    '用户数据' as '数据类型',
    COUNT(*) as '记录数'
FROM tb_user
WHERE is_deleted = 0

UNION ALL

SELECT 
    '文章数据' as '数据类型',
    COUNT(*) as '记录数'
FROM tb_post
WHERE is_deleted = 0

UNION ALL

SELECT 
    '评论数据' as '数据类型',
    COUNT(*) as '记录数'
FROM tb_comment
WHERE is_deleted = 0

UNION ALL

SELECT 
    '分类数据' as '数据类型',
    COUNT(*) as '记录数'
FROM tb_category
WHERE is_deleted = 0

UNION ALL

SELECT 
    '标签数据' as '数据类型',
    COUNT(*) as '记录数'
FROM tb_tag
WHERE is_deleted = 0;

-- =============================================
-- 初始化完成
-- =============================================
SELECT 'Initial data inserted successfully!' AS message;
SELECT 'Default admin user: admin, password: admin123' AS login_info;
SELECT 'Default test user: testuser, password: user123' AS test_info;
