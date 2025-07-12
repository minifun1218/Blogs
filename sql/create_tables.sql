-- =============================================
-- 个人博客系统数据库表创建脚本
-- 数据库: mydb
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `mydb` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `mydb`;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `bio` text COMMENT '个人简介',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '用户状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 角色表
-- =============================================
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '角色状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 3. 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `tb_user_role`;
CREATE TABLE `tb_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `tb_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 4. 分类表
-- =============================================
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `post_count` bigint NOT NULL DEFAULT '0' COMMENT '文章数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =============================================
-- 5. 标签表
-- =============================================
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT '#1890ff' COMMENT '标签颜色',
  `use_count` bigint NOT NULL DEFAULT '0' COMMENT '使用次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_use_count` (`use_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =============================================
-- 6. 文章表
-- =============================================
DROP TABLE IF EXISTS `tb_post`;
CREATE TABLE `tb_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext NOT NULL COMMENT '文章内容',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '文章状态：0-草稿，1-已发布，2-已下架',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `view_count` bigint NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` bigint NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` bigint NOT NULL DEFAULT '0' COMMENT '评论数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_view_count` (`view_count`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`),
  CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `tb_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- =============================================
-- 7. 文章标签关联表
-- =============================================
DROP TABLE IF EXISTS `tb_post_tag`;
CREATE TABLE `tb_post_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_post_tag_post` FOREIGN KEY (`post_id`) REFERENCES `tb_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tb_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- =============================================
-- 8. 评论表
-- =============================================
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID（用于回复评论）',
  `content` text NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '评论状态：0-待审核，1-已通过，2-已拒绝',
  `like_count` bigint NOT NULL DEFAULT '0' COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `tb_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `tb_comment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- =============================================
-- 9. 文件上传记录表
-- =============================================
DROP TABLE IF EXISTS `tb_file_upload`;
CREATE TABLE `tb_file_upload` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `file_type` tinyint NOT NULL COMMENT '文件类型：1-图片，2-视频，3-文档，4-其他',
  `mime_type` varchar(100) NOT NULL COMMENT 'MIME类型',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `user_id` bigint NOT NULL COMMENT '上传用户ID',
  `related_type` tinyint DEFAULT NULL COMMENT '关联对象类型：1-文章，2-评论，3-用户头像',
  `related_id` bigint DEFAULT NULL COMMENT '关联对象ID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '文件状态：0-临时，1-正式使用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_related` (`related_type`, `related_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_file_upload_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- =============================================
-- 10. 积分记录表
-- =============================================
DROP TABLE IF EXISTS `tb_coin`;
CREATE TABLE `tb_coin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` int NOT NULL COMMENT '积分变化量（正数为增加，负数为减少）',
  `operation_type` tinyint NOT NULL COMMENT '操作类型：1-发布文章，2-评论，3-点赞，4-被点赞，5-签到，6-消费',
  `description` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `related_id` bigint DEFAULT NULL COMMENT '关联对象ID（如文章ID、评论ID等）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_coin_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- =============================================
-- 11. 用户积分汇总表
-- =============================================
DROP TABLE IF EXISTS `tb_user_coin`;
CREATE TABLE `tb_user_coin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_coin` bigint NOT NULL DEFAULT '0' COMMENT '总积分',
  `available_coin` bigint NOT NULL DEFAULT '0' COMMENT '可用积分',
  `frozen_coin` bigint NOT NULL DEFAULT '0' COMMENT '冻结积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  CONSTRAINT `fk_user_coin_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分汇总表';

-- =============================================
-- 12. 点赞记录表
-- =============================================
DROP TABLE IF EXISTS `tb_like_record`;
CREATE TABLE `tb_like_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-文章，2-评论',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '点赞状态：0-取消点赞，1-点赞',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_like_record_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表';

-- =============================================
-- 13. 管理员操作日志表
-- =============================================
DROP TABLE IF EXISTS `tb_admin_log`;
CREATE TABLE `tb_admin_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '操作用户ID',
  `username` varchar(50) NOT NULL COMMENT '操作用户名',
  `module` varchar(50) NOT NULL COMMENT '操作模块',
  `operation_type` tinyint NOT NULL COMMENT '操作类型：1-新增，2-修改，3-删除，4-查询，5-登录，6-登出',
  `description` varchar(255) NOT NULL COMMENT '操作描述',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `params` text COMMENT '请求参数',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `result` tinyint NOT NULL DEFAULT '1' COMMENT '操作结果：0-失败，1-成功',
  `error_msg` text COMMENT '错误信息',
  `execute_time` bigint DEFAULT NULL COMMENT '执行时间（毫秒）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_result` (`result`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_admin_log_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- =============================================
-- 14. 系统配置表
-- =============================================
DROP TABLE IF EXISTS `tb_system_config`;
CREATE TABLE `tb_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `config_group` varchar(50) DEFAULT 'default' COMMENT '配置分组',
  `data_type` tinyint NOT NULL DEFAULT '1' COMMENT '数据类型：1-字符串，2-数字，3-布尔值，4-JSON',
  `is_system` tinyint NOT NULL DEFAULT '0' COMMENT '是否系统内置：0-否，1-是',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序权重',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化角色数据
INSERT INTO `tb_role` (`name`, `code`, `description`, `status`) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('管理员', 'ADMIN', '系统管理员，拥有管理权限', 1),
('普通用户', 'USER', '普通用户，基础权限', 1);

-- 初始化系统配置数据
INSERT INTO `tb_system_config` (`config_key`, `config_value`, `config_name`, `description`, `config_group`, `data_type`, `is_system`) VALUES
('site.name', '个人博客系统', '网站名称', '网站的名称', 'site', 1, 1),
('site.description', '基于Spring Boot 3的个人博客系统', '网站描述', '网站的描述信息', 'site', 1, 1),
('site.keywords', 'Spring Boot,博客,Java', '网站关键词', '网站的关键词', 'site', 1, 1),
('site.author', 'Admin', '网站作者', '网站的作者信息', 'site', 1, 1),
('upload.max.size', '10485760', '文件上传最大大小', '文件上传的最大大小（字节）', 'upload', 2, 1),
('upload.allowed.types', 'jpg,jpeg,png,gif,mp4,avi,pdf,doc,docx,txt', '允许上传的文件类型', '允许上传的文件扩展名', 'upload', 1, 1),
('comment.need.audit', 'false', '评论是否需要审核', '新评论是否需要管理员审核', 'comment', 3, 1),
('coin.post.reward', '10', '发布文章奖励积分', '用户发布文章获得的积分奖励', 'coin', 2, 1),
('coin.comment.reward', '2', '评论奖励积分', '用户发表评论获得的积分奖励', 'coin', 2, 1),
('coin.like.reward', '1', '点赞奖励积分', '用户点赞获得的积分奖励', 'coin', 2, 1);

-- 初始化分类数据
INSERT INTO `tb_category` (`name`, `description`, `icon`, `sort_order`) VALUES
('技术分享', '分享技术相关的文章', 'icon-tech', 1),
('生活随笔', '记录生活中的点点滴滴', 'icon-life', 2),
('学习笔记', '学习过程中的笔记和总结', 'icon-study', 3),
('项目实战', '实际项目开发经验分享', 'icon-project', 4);

-- 初始化标签数据
INSERT INTO `tb_tag` (`name`, `color`) VALUES
('Java', '#f50'),
('Spring Boot', '#2db7f5'),
('MySQL', '#87d068'),
('Vue.js', '#108ee9'),
('JavaScript', '#faad14'),
('HTML', '#fa541c'),
('CSS', '#13c2c2'),
('Linux', '#722ed1'),
('Docker', '#1890ff'),
('Git', '#eb2f96');

-- =============================================
-- 创建视图
-- =============================================

-- 文章详情视图（包含作者和分类信息）
CREATE OR REPLACE VIEW `v_post_detail` AS
SELECT
    p.`id`,
    p.`title`,
    p.`summary`,
    p.`content`,
    p.`cover_image`,
    p.`status`,
    p.`is_top`,
    p.`view_count`,
    p.`like_count`,
    p.`comment_count`,
    p.`create_time`,
    p.`update_time`,
    u.`username` AS `author_name`,
    u.`nickname` AS `author_nickname`,
    u.`avatar` AS `author_avatar`,
    c.`name` AS `category_name`,
    c.`description` AS `category_description`
FROM `tb_post` p
LEFT JOIN `tb_user` u ON p.`author_id` = u.`id`
LEFT JOIN `tb_category` c ON p.`category_id` = c.`id`
WHERE p.`is_deleted` = 0 AND u.`is_deleted` = 0;

-- 评论详情视图（包含用户信息）
CREATE OR REPLACE VIEW `v_comment_detail` AS
SELECT
    c.`id`,
    c.`post_id`,
    c.`parent_id`,
    c.`content`,
    c.`status`,
    c.`like_count`,
    c.`create_time`,
    c.`update_time`,
    u.`username` AS `user_name`,
    u.`nickname` AS `user_nickname`,
    u.`avatar` AS `user_avatar`,
    p.`title` AS `post_title`
FROM `tb_comment` c
LEFT JOIN `tb_user` u ON c.`user_id` = u.`id`
LEFT JOIN `tb_post` p ON c.`post_id` = p.`id`
WHERE c.`is_deleted` = 0 AND u.`is_deleted` = 0 AND p.`is_deleted` = 0;

-- =============================================
-- 创建存储过程
-- =============================================

-- 更新用户积分汇总的存储过程
DELIMITER $$
CREATE PROCEDURE `UpdateUserCoinSummary`(IN `p_user_id` BIGINT)
BEGIN
    DECLARE `total_amount` BIGINT DEFAULT 0;

    -- 计算用户总积分
    SELECT COALESCE(SUM(`amount`), 0) INTO `total_amount`
    FROM `tb_coin`
    WHERE `user_id` = `p_user_id`;

    -- 更新或插入用户积分汇总
    INSERT INTO `tb_user_coin` (`user_id`, `total_coin`, `available_coin`)
    VALUES (`p_user_id`, `total_amount`, `total_amount`)
    ON DUPLICATE KEY UPDATE
        `total_coin` = `total_amount`,
        `available_coin` = `total_amount` - `frozen_coin`,
        `update_time` = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- 更新文章统计数据的存储过程
DELIMITER $$
CREATE PROCEDURE `UpdatePostStats`(IN `p_post_id` BIGINT)
BEGIN
    DECLARE `comment_cnt` BIGINT DEFAULT 0;
    DECLARE `like_cnt` BIGINT DEFAULT 0;

    -- 计算评论数
    SELECT COUNT(*) INTO `comment_cnt`
    FROM `tb_comment`
    WHERE `post_id` = `p_post_id` AND `is_deleted` = 0 AND `status` = 1;

    -- 计算点赞数
    SELECT COUNT(*) INTO `like_cnt`
    FROM `tb_like_record`
    WHERE `target_type` = 1 AND `target_id` = `p_post_id` AND `status` = 1;

    -- 更新文章统计
    UPDATE `tb_post`
    SET `comment_count` = `comment_cnt`,
        `like_count` = `like_cnt`,
        `update_time` = CURRENT_TIMESTAMP
    WHERE `id` = `p_post_id`;
END$$
DELIMITER ;

-- =============================================
-- 创建触发器
-- =============================================

-- 积分记录插入后更新用户积分汇总
DELIMITER $$
CREATE TRIGGER `tr_coin_after_insert`
AFTER INSERT ON `tb_coin`
FOR EACH ROW
BEGIN
    CALL `UpdateUserCoinSummary`(NEW.`user_id`);
END$$
DELIMITER ;

-- 评论插入后更新文章评论数
DELIMITER $$
CREATE TRIGGER `tr_comment_after_insert`
AFTER INSERT ON `tb_comment`
FOR EACH ROW
BEGIN
    CALL `UpdatePostStats`(NEW.`post_id`);
END$$
DELIMITER ;

-- 点赞记录变更后更新统计
DELIMITER $$
CREATE TRIGGER `tr_like_record_after_update`
AFTER UPDATE ON `tb_like_record`
FOR EACH ROW
BEGIN
    IF NEW.`target_type` = 1 THEN
        CALL `UpdatePostStats`(NEW.`target_id`);
    END IF;
END$$
DELIMITER ;

-- =============================================
-- 性能优化建议
-- =============================================
-- 以下是一些性能优化的建议，可根据实际情况调整

-- 1. 为经常查询的字段组合创建复合索引
-- ALTER TABLE `tb_post` ADD INDEX `idx_status_top_time` (`status`, `is_top`, `create_time`);
-- ALTER TABLE `tb_comment` ADD INDEX `idx_post_status_time` (`post_id`, `status`, `create_time`);

-- 2. 为全文搜索优化
-- ALTER TABLE `tb_post` ADD FULLTEXT(`title`, `summary`, `content`);

-- 3. 分区表优化（适用于大数据量）
-- 可以考虑对日志表按时间分区
-- ALTER TABLE `tb_admin_log` PARTITION BY RANGE (YEAR(`create_time`)) (
--     PARTITION p2024 VALUES LESS THAN (2025),
--     PARTITION p2025 VALUES LESS THAN (2026),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- =============================================
-- 脚本执行完成
-- =============================================
SELECT 'Database tables created successfully!' AS message;
