# 个人博客系统数据库脚本

本目录包含了个人博客系统的完整数据库创建和初始化脚本。

## 📁 文件说明

### 1. `create_tables.sql` - 数据库表创建脚本
- 创建所有14个数据库表
- 包含完整的字段定义、索引、外键约束
- 包含触发器和存储过程
- 包含基础配置数据

### 2. `init_data.sql` - 初始化数据脚本
- 创建默认管理员账户
- 插入示例文章和评论
- 初始化系统配置
- 创建测试数据

### 3. `optimize_indexes.sql` - 索引优化脚本
- 性能优化索引
- 复合索引创建
- 分区表建议
- 性能监控查询

## 🚀 使用方法

### 方法一：按顺序执行（推荐）

```bash
# 1. 连接到MySQL
mysql -u root -p

# 2. 执行表创建脚本
source /path/to/sql/create_tables.sql

# 3. 执行初始化数据脚本
source /path/to/sql/init_data.sql

# 4. 执行索引优化脚本（可选）
source /path/to/sql/optimize_indexes.sql
```

### 方法二：一次性执行

```bash
# 合并执行所有脚本
cat create_tables.sql init_data.sql optimize_indexes.sql | mysql -u root -p
```

### 方法三：使用MySQL Workbench
1. 打开MySQL Workbench
2. 连接到数据库
3. 依次打开并执行SQL文件

## 📊 数据库表结构

### 核心业务表
- `tb_user` - 用户表
- `tb_post` - 文章表
- `tb_comment` - 评论表
- `tb_category` - 分类表
- `tb_tag` - 标签表

### 关联关系表
- `tb_user_role` - 用户角色关联
- `tb_post_tag` - 文章标签关联

### 功能扩展表
- `tb_file_upload` - 文件上传记录
- `tb_coin` - 积分记录
- `tb_user_coin` - 用户积分汇总
- `tb_like_record` - 点赞记录

### 系统管理表
- `tb_role` - 角色表
- `tb_admin_log` - 管理员日志
- `tb_system_config` - 系统配置

## 🔐 默认账户信息

### 管理员账户
- **用户名**: `admin`
- **密码**: `admin123`
- **邮箱**: `admin@example.com`
- **角色**: 超级管理员

### 测试账户
- **用户名**: `testuser`
- **密码**: `user123`
- **邮箱**: `user@example.com`
- **角色**: 普通用户

> ⚠️ **安全提醒**: 生产环境中请立即修改默认密码！

## 🛠️ 配置说明

### 数据库配置
- **数据库名**: `mydb`
- **字符集**: `utf8mb4`
- **排序规则**: `utf8mb4_unicode_ci`
- **存储引擎**: `InnoDB`

### 系统配置项
脚本会自动插入以下系统配置：
- 网站基本信息
- 文件上传限制
- 积分奖励规则
- 评论审核设置

## 📈 性能优化

### 已创建的索引
- 主键索引（自动）
- 唯一键索引
- 外键索引
- 复合查询索引
- 全文搜索索引

### 优化建议
1. **定期维护**: 运行 `OPTIMIZE TABLE` 命令
2. **监控慢查询**: 启用慢查询日志
3. **分析执行计划**: 使用 `EXPLAIN` 分析查询
4. **适当分区**: 大数据量时考虑表分区

## 🔧 故障排除

### 常见问题

#### 1. 字符集问题
```sql
-- 检查字符集
SHOW VARIABLES LIKE 'character_set%';

-- 修改字符集
ALTER DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 外键约束错误
```sql
-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;
-- 执行SQL
-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
```

#### 3. 权限问题
```sql
-- 创建数据库用户
CREATE USER 'blog_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON mydb.* TO 'blog_user'@'localhost';
FLUSH PRIVILEGES;
```

## 📝 维护建议

### 定期维护任务
1. **备份数据库**
   ```bash
   mysqldump -u root -p mydb > backup_$(date +%Y%m%d).sql
   ```

2. **清理日志数据**
   ```sql
   -- 清理30天前的日志
   DELETE FROM tb_admin_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
   ```

3. **更新统计信息**
   ```sql
   ANALYZE TABLE tb_post, tb_comment, tb_user;
   ```

### 监控指标
- 表大小和行数
- 索引使用率
- 慢查询数量
- 连接数和并发量

## 🔄 版本升级

如需升级数据库结构，请：
1. 备份现有数据
2. 测试升级脚本
3. 在维护窗口执行
4. 验证数据完整性

## 📞 技术支持

如遇到问题，请检查：
1. MySQL版本兼容性（推荐8.0+）
2. 用户权限设置
3. 系统资源（内存、磁盘空间）
4. 错误日志信息

---

**注意**: 请在生产环境使用前充分测试所有脚本！
