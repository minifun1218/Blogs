-- =============================================
-- 个人博客系统数据库索引优化脚本
-- 用于提升查询性能的索引优化
-- =============================================

USE `Blogs`;

-- =============================================
-- 1. 文章表索引优化
-- =============================================

-- 文章列表查询优化（按状态、置顶、时间排序）
ALTER TABLE `tb_post` ADD INDEX `idx_list_query` (`status`, `is_top`, `create_time` DESC);

-- 作者文章查询优化
ALTER TABLE `tb_post` ADD INDEX `idx_author_status` (`author_id`, `status`, `create_time` DESC);

-- 分类文章查询优化
ALTER TABLE `tb_post` ADD INDEX `idx_category_status` (`category_id`, `status`, `create_time` DESC);

-- 热门文章查询优化（按浏览量、点赞数）
ALTER TABLE `tb_post` ADD INDEX `idx_popular` (`status`, `view_count` DESC, `like_count` DESC);

-- 全文搜索优化
ALTER TABLE `tb_post` ADD FULLTEXT INDEX `ft_search` (`title`, `summary`, `content`);

-- =============================================
-- 2. 评论表索引优化
-- =============================================

-- 文章评论查询优化
ALTER TABLE `tb_comment` ADD INDEX `idx_post_comments` (`post_id`, `status`, `parent_id`, `create_time`);

-- 用户评论查询优化
ALTER TABLE `tb_comment` ADD INDEX `idx_user_comments` (`user_id`, `status`, `create_time` DESC);

-- 评论回复查询优化
ALTER TABLE `tb_comment` ADD INDEX `idx_replies` (`parent_id`, `status`, `create_time`);

-- =============================================
-- 3. 文件上传表索引优化
-- =============================================

-- 用户文件查询优化
ALTER TABLE `tb_file_upload` ADD INDEX `idx_user_files` (`user_id`, `file_type`, `status`, `create_time` DESC);

-- 关联对象文件查询优化
ALTER TABLE `tb_file_upload` ADD INDEX `idx_related_files` (`related_type`, `related_id`, `status`);

-- 文件类型统计优化
ALTER TABLE `tb_file_upload` ADD INDEX `idx_file_stats` (`file_type`, `status`, `create_time`);

-- =============================================
-- 4. 积分系统索引优化
-- =============================================

-- 用户积分记录查询优化
ALTER TABLE `tb_coin` ADD INDEX `idx_user_coin_records` (`user_id`, `create_time` DESC);

-- 积分操作类型统计优化
ALTER TABLE `tb_coin` ADD INDEX `idx_operation_stats` (`operation_type`, `create_time`);

-- 关联对象积分记录优化
ALTER TABLE `tb_coin` ADD INDEX `idx_related_coins` (`related_id`, `operation_type`);

-- =============================================
-- 5. 点赞记录索引优化
-- =============================================

-- 目标对象点赞查询优化
ALTER TABLE `tb_like_record` ADD INDEX `idx_target_likes` (`target_type`, `target_id`, `status`);

-- 用户点赞记录查询优化
ALTER TABLE `tb_like_record` ADD INDEX `idx_user_likes` (`user_id`, `status`, `create_time` DESC);

-- =============================================
-- 6. 管理员日志索引优化
-- =============================================

-- 用户操作日志查询优化
ALTER TABLE `tb_admin_log` ADD INDEX `idx_user_logs` (`user_id`, `create_time` DESC);

-- 模块操作日志查询优化
ALTER TABLE `tb_admin_log` ADD INDEX `idx_module_logs` (`module`, `operation_type`, `create_time` DESC);

-- 操作结果统计优化
ALTER TABLE `tb_admin_log` ADD INDEX `idx_result_stats` (`result`, `create_time`);

-- IP地址查询优化
ALTER TABLE `tb_admin_log` ADD INDEX `idx_ip_logs` (`ip_address`, `create_time` DESC);

-- =============================================
-- 7. 系统配置索引优化
-- =============================================

-- 配置分组查询优化
ALTER TABLE `tb_system_config` ADD INDEX `idx_config_group` (`config_group`, `sort_order`);

-- =============================================
-- 8. 创建复合视图索引
-- =============================================

-- 为视图创建物化表（可选，适用于复杂查询）
-- CREATE TABLE `tb_post_summary` AS
-- SELECT 
--     p.id,
--     p.title,
--     p.summary,
--     p.author_id,
--     u.username as author_name,
--     p.category_id,
--     c.name as category_name,
--     p.view_count,
--     p.like_count,
--     p.comment_count,
--     p.create_time,
--     p.status
-- FROM tb_post p
-- LEFT JOIN tb_user u ON p.author_id = u.id
-- LEFT JOIN tb_category c ON p.category_id = c.id
-- WHERE p.is_deleted = 0 AND u.is_deleted = 0;

-- =============================================
-- 9. 分区表优化（适用于大数据量）
-- =============================================

-- 日志表按年份分区
-- ALTER TABLE `tb_admin_log` 
-- PARTITION BY RANGE (YEAR(create_time)) (
--     PARTITION p2023 VALUES LESS THAN (2024),
--     PARTITION p2024 VALUES LESS THAN (2025),
--     PARTITION p2025 VALUES LESS THAN (2026),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- 积分记录表按月份分区
-- ALTER TABLE `tb_coin`
-- PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
--     PARTITION p202401 VALUES LESS THAN (202402),
--     PARTITION p202402 VALUES LESS THAN (202403),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- =============================================
-- 10. 查询性能分析
-- =============================================

-- 分析表统计信息
ANALYZE TABLE `tb_user`, `tb_post`, `tb_comment`, `tb_category`, `tb_tag`;

-- 检查索引使用情况的查询示例
-- EXPLAIN SELECT * FROM tb_post WHERE status = 1 ORDER BY is_top DESC, create_time DESC LIMIT 10;
-- EXPLAIN SELECT * FROM tb_comment WHERE post_id = 1 AND status = 1 ORDER BY create_time;

-- =============================================
-- 11. 索引维护建议
-- =============================================

-- 定期优化表
-- OPTIMIZE TABLE `tb_post`, `tb_comment`, `tb_admin_log`;

-- 检查索引碎片
-- SELECT 
--     TABLE_NAME,
--     INDEX_NAME,
--     CARDINALITY,
--     SUB_PART,
--     PACKED,
--     NULLABLE,
--     INDEX_TYPE
-- FROM information_schema.STATISTICS 
-- WHERE TABLE_SCHEMA = 'mydb'
-- ORDER BY TABLE_NAME, INDEX_NAME;

-- =============================================
-- 12. 性能监控查询
-- =============================================

-- 慢查询监控
-- SELECT 
--     sql_text,
--     exec_count,
--     avg_timer_wait/1000000000 as avg_time_sec,
--     sum_timer_wait/1000000000 as total_time_sec
-- FROM performance_schema.events_statements_summary_by_digest
-- WHERE schema_name = 'mydb'
-- ORDER BY avg_timer_wait DESC
-- LIMIT 10;

-- 表空间使用情况
SELECT 
    TABLE_NAME as '表名',
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) as '大小(MB)',
    TABLE_ROWS as '行数',
    ROUND((INDEX_LENGTH / 1024 / 1024), 2) as '索引大小(MB)'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'mydb'
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- =============================================
-- 索引优化完成
-- =============================================
SELECT 'Database indexes optimized successfully!' AS message;
SELECT 'Remember to monitor query performance and adjust indexes as needed.' AS reminder;
