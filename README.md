# 个人博客系统

一个基于 Spring Boot 3.3.2 构建的现代化博客系统，采用前后端分离架构，提供完整的内容管理、用户交互和系统管理功能。

## 项目特性

### 核心功能
- **用户管理系统** - 用户注册、登录、个人信息管理、权限控制
- **内容管理系统** - 文章发布、编辑、分类管理、标签系统
- **评论互动系统** - 多级评论、点赞功能、评论审核
- **文件管理系统** - 文件上传、下载、图片预览、存储管理
- **积分奖励系统** - 签到积分、发布奖励、积分排行榜
- **系统管理功能** - 配置管理、操作日志、数据统计

### 技术特色
- **JWT 无状态认证** - 访问令牌 + 刷新令牌双令牌机制
- **RBAC 权限控制** - 基于角色的访问控制，支持细粒度权限管理
- **RESTful API 设计** - 遵循 REST 规范，提供 80+ 个标准化接口
- **统一异常处理** - 全局异常捕获，标准化错误响应
- **分页查询支持** - 所有列表接口支持分页和条件查询
- **逻辑删除机制** - 数据安全删除，支持数据恢复

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.3.2
- **Java 版本**: Java 21
- **数据库**: MySQL 8.0
- **ORM 框架**: MyBatis-Plus 3.5.5
- **安全框架**: Spring Security
- **认证方案**: JWT (JSON Web Token)
- **日志框架**: Log4j2
- **构建工具**: Maven 3.x
- **连接池**: HikariCP

### 系统架构设计
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │     Service     │    │     Mapper      │
│   控制层         │───▶│    业务逻辑层    │───▶│    数据访问层    │
│   RESTful API   │    │   事务管理       │    │   MyBatis-Plus  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Global Exception│    │   Spring AOP    │    │   MySQL 8.0     │
│    Handler      │    │   Transaction   │    │   Database      │
│   统一异常处理    │    │     事务切面     │    │     数据库      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 核心模块设计
- **org.easytech.blogs.common** - 公共响应类和分页结果封装
- **org.easytech.blogs.config** - 系统配置类（数据库、文件上传、安全配置）
- **org.easytech.blogs.controller** - REST 控制器层
- **org.easytech.blogs.entity** - JPA 实体类，映射数据库表
- **org.easytech.blogs.service** - 业务逻辑层接口和实现
- **org.easytech.blogs.mapper** - MyBatis-Plus 数据访问层
- **org.easytech.blogs.dto** - 数据传输对象
- **org.easytech.blogs.exception** - 自定义异常类和全局异常处理
- **org.easytech.blogs.security** - JWT 认证过滤器和安全工具类
- **org.easytech.blogs.util** - JWT 工具类和安全工具类

## 数据库设计

### 核心数据表
| 表名 | 中文名称 | 主要功能 |
|-----|---------|---------|
| tb_user | 用户表 | 存储用户基本信息、登录凭证 |
| tb_role | 角色表 | 定义系统角色类型 |
| tb_user_role | 用户角色关联表 | 用户与角色的多对多关系 |
| tb_post | 文章表 | 存储博客文章内容和元数据 |
| tb_category | 分类表 | 文章分类管理 |
| tb_tag | 标签表 | 文章标签管理 |
| tb_post_tag | 文章标签关联表 | 文章与标签的多对多关系 |
| tb_comment | 评论表 | 用户评论内容，支持多级评论 |
| tb_like_record | 点赞记录表 | 用户点赞行为记录 |
| tb_file_upload | 文件上传表 | 文件存储信息和元数据 |
| tb_coin | 积分记录表 | 用户积分变动历史 |
| tb_user_coin | 用户积分表 | 用户当前积分余额 |
| tb_admin_log | 管理日志表 | 管理员操作记录 |
| tb_system_config | 系统配置表 | 系统参数配置 |

### 数据关系设计
- **用户体系**: User ↔ UserRole ↔ Role (多对多关系)
- **内容体系**: Post → Category (多对一), Post ↔ PostTag ↔ Tag (多对多)
- **互动体系**: Comment → Post (多对一), Comment → User (多对一)
- **积分体系**: Coin → User (多对一), UserCoin → User (一对一)

## 安全机制

### JWT 认证体系
```
用户登录 → 验证凭证 → 生成 JWT 令牌对
                    ├── Access Token (24小时有效期)
                    └── Refresh Token (7天有效期)

API 请求 → JWT 过滤器 → 令牌验证 → 用户身份确认 → 权限检查 → 业务处理
```

### 权限控制注解
- `@PublicAccess` - 公开访问，无需认证
- `@RequiresAuthentication` - 需要用户登录
- `@RequiresRole("ADMIN")` - 需要特定角色
- `@RequiresPermission("user:edit")` - 需要特定权限

### 角色权限体系
| 角色 | 权限范围 | 主要功能 |
|-----|---------|---------|
| ADMIN | 系统管理员 | 所有功能的完全访问权限 |
| EDITOR | 内容编辑 | 内容管理、用户查看权限 |
| USER | 普通用户 | 个人信息管理、内容发布 |

## API 接口设计

### 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "success": true
}
```

### 接口模块划分
| 模块 | 路径前缀 | 接口数量 | 主要功能 |
|-----|---------|---------|---------|
| 认证授权 | /api/auth | 10个 | 登录、注册、令牌管理 |
| 用户管理 | /api/users | 11个 | 用户CRUD、信息更新 |
| 文章管理 | /api/posts | 15个 | 文章发布、编辑、查询 |
| 评论管理 | /api/comments | 13个 | 评论发布、审核、互动 |
| 分类管理 | /categories | 9个 | 分类CRUD、统计查询 |
| 标签管理 | /tags | 11个 | 标签CRUD、热门标签 |
| 文件管理 | /files | 15个 | 文件上传、下载、管理 |
| 积分管理 | /api/coins | 10个 | 积分奖励、消费、排行 |

### 分页查询标准
所有列表查询接口统一支持以下参数：
- `page` - 页码（默认1）
- `size` - 每页大小（默认10）
- `keyword` - 搜索关键词（可选）

## 开发环境搭建

### 环境要求
- **JDK**: Java 21 或更高版本
- **Maven**: 3.6 或更高版本
- **MySQL**: 8.0 或更高版本
- **IDE**: IntelliJ IDEA 或 Eclipse

### 快速启动

#### 1. 克隆项目
```bash
git clone <repository-url>
cd blogs
```

#### 2. 数据库初始化
```bash
# 连接 MySQL 数据库
mysql -u root -p

# 按顺序执行 SQL 脚本
source sql/create_tables.sql
source sql/init_data.sql
source sql/optimize_indexes.sql
```

#### 3. 配置数据库连接
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Blogs?useSSL=false&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

#### 4. 编译和运行
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

#### 5. 访问应用
- **API 基础地址**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/actuator/health

### 测试账户
系统预置了测试账户：
- **管理员账户**: username=`admin`, password=`admin123`
- **普通用户**: username=`testuser`, password=`user123`

## 项目配置

### 核心配置文件
- `application.yml` - 主配置文件（数据库、服务器、日志等）
- `log4j2-spring.xml` - 日志配置
- `pom.xml` - Maven 依赖配置

### 自定义配置
系统支持以下自定义配置前缀：
- `blog.jwt.*` - JWT 相关配置（密钥、过期时间等）
- `blog.upload.*` - 文件上传配置（路径、大小限制等）
- `blog.coin.*` - 积分系统配置（奖励规则等）

### 文件上传配置
```yaml
blog:
  upload:
    path: ./uploads/
    max-size: 10MB
    allowed-types: jpg,jpeg,png,gif,mp4,avi,pdf,doc,docx,txt,zip,gzip
```

### JWT 配置
```yaml
blog:
  jwt:
    secret: mySecretKey123456789012345678901234567890
    expiration: 86400  # 24小时
    refresh-expiration: 604800  # 7天
    issuer: blog-system
```

## 异常处理机制

### 全局异常处理
系统实现了统一的异常处理机制，所有异常都会被 `GlobalExceptionHandler` 捕获并返回标准格式的错误响应。

### 自定义异常类型
- `BusinessException` - 业务逻辑异常
- `ResourceNotFoundException` - 资源不存在异常
- `ValidationException` - 数据验证异常
- `UnauthorizedException` - 认证失败异常
- `ForbiddenException` - 权限不足异常
- `FileUploadException` - 文件上传异常

### 错误响应格式
```json
{
  "code": 400,
  "message": "具体错误信息",
  "data": null,
  "success": false,
  "timestamp": "2024-01-01T10:00:00",
  "path": "/api/users/1"
}
```

## 日志系统

### 日志配置
使用 Log4j2 作为日志框架，配置文件位于 `src/main/resources/log4j2-spring.xml`。

### 日志级别
- **应用日志**: DEBUG 级别，记录详细的业务操作
- **框架日志**: INFO 级别，记录系统关键信息
- **SQL 日志**: DEBUG 级别，记录 MyBatis-Plus 执行的 SQL

### 日志文件
- `log/info.log` - 应用信息日志
- `log/error.log` - 错误日志
- 日志文件按天滚动，自动压缩历史文件

## 性能优化

### 数据库优化
- **连接池优化**: HikariCP 连接池，最大连接数 20
- **索引优化**: 关键查询字段建立索引
- **分页查询**: 使用 MyBatis-Plus 分页插件
- **逻辑删除**: 避免物理删除，保证数据安全

### 缓存策略
- **JVM 缓存**: 系统配置等静态数据缓存
- **数据库缓存**: MyBatis 一级、二级缓存
- **Redis 支持**: 预留 Redis 缓存接口

### 查询优化
- **批量查询**: 支持批量获取标签、分类等
- **关联查询**: 合理使用 JOIN 减少 N+1 查询
- **条件查询**: 支持多条件组合查询

## 安全建议

### 生产环境配置
1. **修改默认密钥**: 更换 JWT 密钥为高强度随机字符串
2. **数据库安全**: 使用专用数据库用户，限制权限
3. **HTTPS 部署**: 生产环境必须使用 HTTPS
4. **定期备份**: 配置数据库定期备份策略

### 安全检查清单
- [ ] JWT 密钥已更换为生产密钥
- [ ] 数据库密码已修改
- [ ] 默认管理员密码已修改
- [ ] 文件上传路径权限已限制
- [ ] SQL 注入防护已验证
- [ ] XSS 防护已验证

## 扩展开发

### 添加新功能模块
1. 创建实体类 (Entity)
2. 创建数据访问层 (Mapper)
3. 创建业务逻辑层 (Service)
4. 创建控制器 (Controller)
5. 添加相应的 DTO 类
6. 编写单元测试

### 自定义权限注解
```java
@RequiresPermission("custom:action")
public Result<String> customAction() {
    // 业务逻辑
}
```

### 添加新的异常类型
```java
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
```

## 测试指南

### 单元测试
项目包含完整的单元测试用例：
- **Service 层测试**: 业务逻辑测试
- **Controller 层测试**: API 接口测试
- **工具类测试**: JWT、安全工具类测试

### 测试运行
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthServiceTest

# 跳过测试打包
mvn clean package -DskipTests
```

### API 测试
可以使用以下工具进行 API 测试：
- **Postman**: 导入 API 文档进行接口测试
- **curl**: 命令行测试工具
- **Swagger**: 在线 API 文档和测试

### 异常测试接口
系统提供专门的异常测试接口：
```bash
curl http://localhost:8080/api/test/exception/business
curl http://localhost:8080/api/test/exception/not-found
curl http://localhost:8080/api/test/exception/param-validation
```

## 部署指南

### 本地部署
```bash
# 编译打包
mvn clean package

# 运行 JAR 包
java -jar target/blogs-0.0.1-SNAPSHOT.jar

# 指定配置文件
java -jar target/blogs-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/application-prod.yml
```

### Docker 部署
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/blogs-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 服务器部署
1. 安装 Java 21 运行环境
2. 安装和配置 MySQL 8.0
3. 上传应用 JAR 包
4. 配置系统服务 (systemd)
5. 配置反向代理 (Nginx)

## 维护和监控

### 应用监控
- **健康检查**: `/actuator/health`
- **应用信息**: `/actuator/info`
- **性能指标**: `/actuator/metrics`

### 日志监控
- 定期检查错误日志
- 监控关键业务操作日志
- 配置日志告警机制

### 数据备份
- 数据库定期备份
- 上传文件定期备份
- 配置文件版本控制

## 常见问题

### Q: JWT 令牌过期如何处理？
A: 系统支持刷新令牌机制，客户端应该实现自动刷新逻辑。当访问令牌过期时，使用刷新令牌获取新的访问令牌。

### Q: 文件上传失败如何排查？
A: 检查以下几个方面：
1. 文件大小是否超过限制 (10MB)
2. 文件类型是否在允许列表中
3. 上传目录是否有写入权限
4. 磁盘空间是否充足

### Q: 数据库连接失败如何处理？
A: 检查数据库配置：
1. 数据库服务是否启动
2. 连接字符串是否正确
3. 用户名密码是否正确
4. 数据库是否存在

### Q: 如何添加新的权限控制？
A: 在相应的 Controller 方法上添加权限注解：
```java
@RequiresRole("ADMIN")
@PreAuthorize("hasRole('ADMIN')")
public Result<String> adminOnlyAction() {
    // 管理员专用功能
}
```

## 贡献指南

### 代码规范
- 遵循 Java 编码规范
- 使用有意义的变量和方法名
- 添加必要的注释和文档
- 保持代码简洁和可读性

### 提交规范
```
feat: 添加新功能
fix: 修复 bug
docs: 更新文档
style: 代码格式化
refactor: 代码重构
test: 添加测试
chore: 构建过程或辅助工具的变动
```

### 开发流程
1. Fork 项目到个人仓库
2. 创建功能分支
3. 开发和测试新功能
4. 提交 Pull Request
5. 代码审查和合并

## 许可证

本项目采用 MIT 许可证，详见 LICENSE 文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送 Pull Request
- 邮件咨询

---

**项目状态**: ✅ 生产就绪
**最后更新**: 2024年1月
**版本**: 1.0.0
