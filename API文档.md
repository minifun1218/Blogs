# 博客系统 API 文档

## 项目概述

这是一个基于 Spring Boot 3.3.2 构建的完整博客系统，使用 Java 21、MyBatis-Plus 和 MySQL 开发。系统实现了用户管理、博客文章发布、评论互动、分类标签、文件上传和积分系统等功能，具备完整的 JWT 认证授权体系。

### 技术栈
- **后端框架**: Spring Boot 3.3.2
- **Java 版本**: Java 21
- **数据库**: MySQL 8.0
- **ORM 框架**: MyBatis-Plus 3.5.5
- **安全认证**: Spring Security + JWT
- **日志框架**: Log4j2
- **构建工具**: Maven

### 项目特性
- 基于 JWT 的无状态认证
- 完整的 RBAC 权限控制
- RESTful API 设计
- 统一的响应格式和异常处理
- 文件上传和管理
- 积分奖励系统
- 分页查询支持
- 逻辑删除机制

## 系统架构

### 核心模块
- **用户管理模块**: 用户注册、登录、信息管理
- **认证授权模块**: JWT 认证、角色权限控制
- **内容管理模块**: 文章发布、编辑、分类管理
- **互动模块**: 评论、点赞、收藏功能
- **文件管理模块**: 文件上传、下载、预览
- **积分系统模块**: 积分奖励、排行榜、签到
- **系统管理模块**: 配置管理、日志记录

### 数据库设计
项目包含以下核心实体：
- **用户体系**: User(用户)、Role(角色)、UserRole(用户角色关联)
- **内容体系**: Post(文章)、Category(分类)、Tag(标签)、PostTag(文章标签关联)
- **互动体系**: Comment(评论)、LikeRecord(点赞记录)
- **系统体系**: FileUpload(文件上传)、Coin(积分记录)、UserCoin(用户积分)、AdminLog(管理日志)、SystemConfig(系统配置)

## API 接口文档

### 统一响应格式

所有 API 接口都使用统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "success": true
}
```

**响应状态码**:
- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未认证或认证失败
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 分页查询响应格式

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [],
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10
  },
  "success": true
}
```

---

## 1. 认证授权模块 (AuthController)

### 基础路径: `/api/auth`

#### 1.1 用户登录
- **接口**: `POST /api/auth/login`
- **功能**: 用户登录获取访问令牌和刷新令牌
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```
- **响应数据**:
```json
{
  "accessToken": "访问令牌",
  "refreshToken": "刷新令牌",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "userInfo": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "roles": ["ADMIN"]
  }
}
```

#### 1.2 用户注册
- **接口**: `POST /api/auth/register`
- **功能**: 新用户注册
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱",
  "nickname": "昵称"
}
```

#### 1.3 刷新令牌
- **接口**: `POST /api/auth/refresh`
- **功能**: 使用刷新令牌获取新的访问令牌
- **请求参数**:
```json
{
  "refreshToken": "刷新令牌"
}
```

#### 1.4 用户登出
- **接口**: `POST /api/auth/logout`
- **功能**: 用户登出，使令牌失效
- **请求头**: `Authorization: Bearer {token}`

#### 1.5 修改密码
- **接口**: `POST /api/auth/change-password`
- **功能**: 修改当前用户密码
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "currentPassword": "当前密码",
  "newPassword": "新密码"
}
```

#### 1.6 获取当前用户信息
- **接口**: `GET /api/auth/me`
- **功能**: 获取当前登录用户的详细信息
- **请求头**: `Authorization: Bearer {token}`

#### 1.7 验证令牌
- **接口**: `POST /api/auth/validate`
- **功能**: 验证访问令牌是否有效
- **请求参数**: `token` (query parameter)

#### 1.8 检查用户名可用性
- **接口**: `GET /api/auth/check-username`
- **功能**: 检查用户名是否可用
- **请求参数**: `username` (query parameter)

#### 1.9 检查邮箱可用性
- **接口**: `GET /api/auth/check-email`
- **功能**: 检查邮箱是否可用
- **请求参数**: `email` (query parameter)

#### 1.10 获取令牌信息
- **接口**: `POST /api/auth/token-info`
- **功能**: 获取令牌的详细信息
- **请求参数**: `token` (query parameter)

---

## 2. 用户管理模块 (UserController)

### 基础路径: `/api/users`

#### 2.1 用户注册
- **接口**: `POST /api/users/register`
- **功能**: 用户注册 (公开访问)
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码", 
  "email": "邮箱",
  "nickname": "昵称"
}
```

#### 2.2 用户登录
- **接口**: `POST /api/users/login`
- **功能**: 用户登录 (公开访问)
- **请求参数**:
```json
{
  "username": "用户名",
  "password": "密码"
}
```

#### 2.3 获取用户信息
- **接口**: `GET /api/users/{id}`
- **功能**: 根据ID获取用户信息 (公开访问)
- **路径参数**: `id` - 用户ID

#### 2.4 获取用户列表
- **接口**: `GET /api/users`
- **功能**: 分页获取用户列表 (需要 ADMIN 或 EDITOR 角色)
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)
  - `keyword`: 搜索关键词 (可选)

#### 2.5 更新用户信息
- **接口**: `PUT /api/users/{id}`
- **功能**: 更新用户信息 (需要认证，只能更新自己的信息或管理员权限)
- **路径参数**: `id` - 用户ID
- **请求参数**:
```json
{
  "email": "新邮箱",
  "nickname": "新昵称", 
  "bio": "个人简介",
  "avatar": "头像URL"
}
```

#### 2.6 修改密码
- **接口**: `PUT /api/users/{id}/password`
- **功能**: 修改用户密码 (需要认证)
- **路径参数**: `id` - 用户ID
- **请求参数**:
```json
{
  "currentPassword": "当前密码",
  "newPassword": "新密码"
}
```

#### 2.7 更新用户状态
- **接口**: `PATCH /api/users/{id}/status`
- **功能**: 更新用户状态 (管理员功能)
- **路径参数**: `id` - 用户ID
- **请求参数**: `status` - 状态值 (0-禁用，1-正常)

#### 2.8 删除用户
- **接口**: `DELETE /api/users/{id}`
- **功能**: 删除用户 (管理员功能，逻辑删除)
- **路径参数**: `id` - 用户ID

#### 2.9 检查用户名可用性
- **接口**: `GET /api/users/check-username`
- **功能**: 检查用户名是否可用 (公开访问)
- **请求参数**: `username` - 用户名

#### 2.10 检查邮箱可用性
- **接口**: `GET /api/users/check-email`
- **功能**: 检查邮箱是否可用 (公开访问)
- **请求参数**: `email` - 邮箱

#### 2.11 获取当前用户信息
- **接口**: `GET /api/users/me`
- **功能**: 获取当前登录用户信息 (需要认证)

---

## 3. 文章管理模块 (PostController)

### 基础路径: `/api/posts`

#### 3.1 发布文章
- **接口**: `POST /api/posts`
- **功能**: 发布新文章 (需要认证)
- **请求参数**:
```json
{
  "title": "文章标题",
  "summary": "文章摘要",
  "content": "文章内容",
  "categoryId": 1,
  "tagNames": ["标签1", "标签2"]
}
```

#### 3.2 保存草稿
- **接口**: `POST /api/posts/drafts`
- **功能**: 保存文章草稿 (需要认证)
- **请求参数**: 同发布文章

#### 3.3 获取文章详情
- **接口**: `GET /api/posts/{id}`
- **功能**: 获取文章详情 (公开访问)
- **路径参数**: `id` - 文章ID

#### 3.4 更新文章
- **接口**: `PUT /api/posts/{id}`
- **功能**: 更新文章 (需要认证，只能更新自己的文章或管理员权限)
- **路径参数**: `id` - 文章ID
- **请求参数**:
```json
{
  "title": "新标题",
  "summary": "新摘要",
  "content": "新内容",
  "categoryId": 1,
  "tagNames": ["新标签1", "新标签2"]
}
```

#### 3.5 删除文章
- **接口**: `DELETE /api/posts/{id}`
- **功能**: 删除文章 (需要认证，只能删除自己的文章或管理员权限)
- **路径参数**: `id` - 文章ID

#### 3.6 获取文章列表
- **接口**: `GET /api/posts`
- **功能**: 分页获取文章列表 (公开访问)
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)
  - `categoryId`: 分类ID (可选)
  - `authorId`: 作者ID (可选)
  - `tagId`: 标签ID (可选)
  - `keyword`: 搜索关键词 (可选)

#### 3.7 获取热门文章
- **接口**: `GET /api/posts/hot`
- **功能**: 获取热门文章列表 (公开访问)
- **请求参数**: `limit` - 限制数量 (默认10)

#### 3.8 获取最新文章
- **接口**: `GET /api/posts/latest`
- **功能**: 获取最新文章列表 (公开访问)
- **请求参数**: `limit` - 限制数量 (默认10)

#### 3.9 获取推荐文章
- **接口**: `GET /api/posts/recommend`
- **功能**: 获取推荐文章列表 (公开访问)
- **请求参数**: `limit` - 限制数量 (默认10)

#### 3.10 点赞文章
- **接口**: `POST /api/posts/{id}/likes`
- **功能**: 点赞文章
- **路径参数**: `id` - 文章ID
- **请求头**: `User-Id` - 用户ID

#### 3.11 取消点赞文章
- **接口**: `DELETE /api/posts/{id}/likes`
- **功能**: 取消点赞文章
- **路径参数**: `id` - 文章ID
- **请求头**: `User-Id` - 用户ID

#### 3.12 设置文章置顶
- **接口**: `PATCH /api/posts/{id}/top`
- **功能**: 设置文章置顶 (管理员功能)
- **路径参数**: `id` - 文章ID
- **请求参数**: `isTop` - 是否置顶

#### 3.13 更新文章状态
- **接口**: `PATCH /api/posts/{id}/status`
- **功能**: 更新文章状态 (管理员功能)
- **路径参数**: `id` - 文章ID
- **请求参数**: `status` - 状态值

#### 3.14 检查点赞状态
- **接口**: `GET /api/posts/{id}/likes/check`
- **功能**: 检查用户是否已点赞文章
- **路径参数**: `id` - 文章ID
- **请求参数**: `userId` - 用户ID

#### 3.15 获取文章统计信息
- **接口**: `GET /api/posts/{id}/statistics`
- **功能**: 获取文章统计信息
- **路径参数**: `id` - 文章ID

---

## 4. 评论管理模块 (CommentController)

### 基础路径: `/api/comments`

#### 4.1 发表评论
- **接口**: `POST /api/comments`
- **功能**: 发表评论
- **请求参数**:
```json
{
  "postId": 1,
  "userId": 1,
  "content": "评论内容",
  "parentId": null
}
```

#### 4.2 获取评论详情
- **接口**: `GET /api/comments/{id}`
- **功能**: 获取评论详情
- **路径参数**: `id` - 评论ID

#### 4.3 获取评论列表
- **接口**: `GET /api/comments`
- **功能**: 分页获取评论列表
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)
  - `status`: 评论状态 (可选)

#### 4.4 根据文章获取评论
- **接口**: `GET /api/comments/posts/{postId}`
- **功能**: 获取指定文章的评论 (树形结构)
- **路径参数**: `postId` - 文章ID

#### 4.5 根据用户获取评论
- **接口**: `GET /api/comments/users/{userId}`
- **功能**: 分页获取指定用户的评论
- **路径参数**: `userId` - 用户ID
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)

#### 4.6 删除评论
- **接口**: `DELETE /api/comments/{id}`
- **功能**: 删除评论 (逻辑删除)
- **路径参数**: `id` - 评论ID
- **请求头**: `User-Id` - 用户ID (可选)

#### 4.7 点赞评论
- **接口**: `POST /api/comments/{id}/likes`
- **功能**: 点赞评论
- **路径参数**: `id` - 评论ID
- **请求头**: `User-Id` - 用户ID

#### 4.8 取消点赞评论
- **接口**: `DELETE /api/comments/{id}/likes`
- **功能**: 取消点赞评论
- **路径参数**: `id` - 评论ID
- **请求头**: `User-Id` - 用户ID

#### 4.9 审核评论
- **接口**: `PATCH /api/comments/{id}/status`
- **功能**: 审核评论 (管理员功能)
- **路径参数**: `id` - 评论ID
- **请求参数**: `status` - 审核状态

#### 4.10 批量审核评论
- **接口**: `PATCH /api/comments/batch-audit`
- **功能**: 批量审核评论 (管理员功能)
- **请求参数**:
  - `commentIds`: 评论ID列表
  - `status`: 审核状态

#### 4.11 统计文章评论数
- **接口**: `GET /api/comments/posts/{postId}/count`
- **功能**: 统计指定文章的评论数量
- **路径参数**: `postId` - 文章ID

#### 4.12 统计用户评论数
- **接口**: `GET /api/comments/users/{userId}/count`
- **功能**: 统计指定用户的评论数量
- **路径参数**: `userId` - 用户ID

#### 4.13 检查评论点赞状态
- **接口**: `GET /api/comments/{id}/likes/check`
- **功能**: 检查用户是否已点赞评论
- **路径参数**: `id` - 评论ID
- **请求参数**: `userId` - 用户ID

---

## 5. 分类管理模块 (CategoryController)

### 基础路径: `/categories`

#### 5.1 创建分类
- **接口**: `POST /categories`
- **功能**: 创建分类 (管理员功能)
- **请求参数**:
```json
{
  "name": "分类名称",
  "description": "分类描述",
  "icon": "图标URL",
  "sortOrder": 1
}
```

#### 5.2 更新分类
- **接口**: `PUT /categories/{id}`
- **功能**: 更新分类 (管理员功能)
- **路径参数**: `id` - 分类ID
- **请求参数**: 同创建分类

#### 5.3 删除分类
- **接口**: `DELETE /categories/{id}`
- **功能**: 删除分类 (管理员功能)
- **路径参数**: `id` - 分类ID

#### 5.4 获取分类详情
- **接口**: `GET /categories/{id}`
- **功能**: 获取分类详情
- **路径参数**: `id` - 分类ID

#### 5.5 查询所有分类
- **接口**: `GET /categories`
- **功能**: 获取所有分类列表

#### 5.6 获取有文章的分类
- **接口**: `GET /categories/with-posts`
- **功能**: 获取有文章的分类列表

#### 5.7 根据名称查询分类
- **接口**: `GET /categories/name/{name}`
- **功能**: 根据名称查询分类
- **路径参数**: `name` - 分类名称

#### 5.8 检查分类名称是否存在
- **接口**: `GET /categories/check-name`
- **功能**: 检查分类名称是否存在
- **请求参数**: `name` - 分类名称

#### 5.9 更新分类文章数量
- **接口**: `PUT /categories/{id}/update-post-count`
- **功能**: 更新分类文章数量 (管理员功能)
- **路径参数**: `id` - 分类ID

---

## 6. 标签管理模块 (TagController)

### 基础路径: `/tags`

#### 6.1 创建标签
- **接口**: `POST /tags`
- **功能**: 创建标签 (管理员功能)
- **请求参数**:
```json
{
  "name": "标签名称",
  "color": "标签颜色"
}
```

#### 6.2 更新标签
- **接口**: `PUT /tags/{id}`
- **功能**: 更新标签 (管理员功能)
- **路径参数**: `id` - 标签ID
- **请求参数**: 同创建标签

#### 6.3 删除标签
- **接口**: `DELETE /tags/{id}`
- **功能**: 删除标签 (管理员功能)
- **路径参数**: `id` - 标签ID

#### 6.4 获取标签详情
- **接口**: `GET /tags/{id}`
- **功能**: 获取标签详情
- **路径参数**: `id` - 标签ID

#### 6.5 查询所有标签
- **接口**: `GET /tags`
- **功能**: 获取所有标签列表

#### 6.6 获取热门标签
- **接口**: `GET /tags/hot`
- **功能**: 获取热门标签列表
- **请求参数**: `limit` - 限制数量 (默认20)

#### 6.7 根据文章ID查询标签
- **接口**: `GET /tags/post/{postId}`
- **功能**: 获取指定文章的标签
- **路径参数**: `postId` - 文章ID

#### 6.8 根据名称查询标签
- **接口**: `GET /tags/name/{name}`
- **功能**: 根据名称查询标签
- **路径参数**: `name` - 标签名称

#### 6.9 检查标签名称是否存在
- **接口**: `GET /tags/check-name`
- **功能**: 检查标签名称是否存在
- **请求参数**: `name` - 标签名称

#### 6.10 批量查询标签
- **接口**: `POST /tags/batch`
- **功能**: 批量查询标签
- **请求参数**:
```json
{
  "names": ["标签1", "标签2"]
}
```

#### 6.11 更新标签使用次数
- **接口**: `PUT /tags/{id}/update-use-count`
- **功能**: 更新标签使用次数 (管理员功能)
- **路径参数**: `id` - 标签ID

---

## 7. 文件管理模块 (FileUploadController)

### 基础路径: `/files`

#### 7.1 单文件上传
- **接口**: `POST /files/upload`
- **功能**: 上传单个文件
- **请求参数**:
  - `file`: 文件 (multipart/form-data)
  - `userId`: 用户ID
  - `relatedType`: 关联类型 (可选)
  - `relatedId`: 关联ID (可选)

#### 7.2 多文件上传
- **接口**: `POST /files/upload-multiple`
- **功能**: 上传多个文件
- **请求参数**:
  - `files`: 文件数组 (multipart/form-data)
  - `userId`: 用户ID
  - `relatedType`: 关联类型 (可选)
  - `relatedId`: 关联ID (可选)

#### 7.3 图片上传
- **接口**: `POST /files/upload-image`
- **功能**: 上传图片 (专用于富文本编辑器)
- **请求参数**:
  - `file`: 图片文件 (multipart/form-data)
  - `userId`: 用户ID

#### 7.4 文件下载
- **接口**: `GET /files/download/{fileId}`
- **功能**: 下载文件
- **路径参数**: `fileId` - 文件ID

#### 7.5 文件预览
- **接口**: `GET /files/preview/{fileId}`
- **功能**: 预览文件 (主要用于图片)
- **路径参数**: `fileId` - 文件ID

#### 7.6 删除文件
- **接口**: `DELETE /files/{fileId}`
- **功能**: 删除文件
- **路径参数**: `fileId` - 文件ID
- **请求头**: `User-Id` - 用户ID (可选)

#### 7.7 获取文件详情
- **接口**: `GET /files/{fileId}`
- **功能**: 获取文件详情
- **路径参数**: `fileId` - 文件ID

#### 7.8 根据用户查询文件
- **接口**: `GET /files/user/{userId}`
- **功能**: 分页获取指定用户的文件
- **路径参数**: `userId` - 用户ID
- **请求参数**:
  - `current`: 当前页 (默认1)
  - `size`: 每页大小 (默认10)

#### 7.9 根据文件类型查询文件
- **接口**: `GET /files/type/{fileType}`
- **功能**: 分页获取指定类型的文件
- **路径参数**: `fileType` - 文件类型
- **请求参数**:
  - `current`: 当前页 (默认1)
  - `size`: 每页大小 (默认10)

#### 7.10 根据关联对象查询文件
- **接口**: `GET /files/related`
- **功能**: 获取关联对象的文件
- **请求参数**:
  - `relatedType`: 关联类型
  - `relatedId`: 关联ID

#### 7.11 更新文件状态
- **接口**: `PUT /files/{fileId}/status`
- **功能**: 更新文件状态
- **路径参数**: `fileId` - 文件ID
- **请求参数**: `status` - 状态值

#### 7.12 获取文件统计信息
- **接口**: `GET /files/stats`
- **功能**: 获取文件统计信息
- **请求参数**:
  - `fileType`: 文件类型 (可选)
  - `status`: 文件状态 (可选)

#### 7.13 获取用户文件大小统计
- **接口**: `GET /files/size/user/{userId}`
- **功能**: 获取指定用户的文件总大小
- **路径参数**: `userId` - 用户ID

#### 7.14 清理临时文件
- **接口**: `POST /files/clean-temp`
- **功能**: 清理临时文件 (管理员功能)
- **请求参数**: `hours` - 小时数 (默认24)

#### 7.15 获取上传配置信息
- **接口**: `GET /files/config`
- **功能**: 获取文件上传配置信息

---

## 8. 积分管理模块 (CoinController)

### 基础路径: `/api/coins`

#### 8.1 分页查询积分记录
- **接口**: `GET /api/coins`
- **功能**: 分页查询积分记录
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)
  - `userId`: 用户ID (可选)
  - `operationType`: 操作类型 (可选)

#### 8.2 查询用户积分记录
- **接口**: `GET /api/coins/user/{userId}`
- **功能**: 分页查询指定用户的积分记录
- **路径参数**: `userId` - 用户ID
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)

#### 8.3 手动添加积分记录
- **接口**: `POST /api/coins`
- **功能**: 手动添加积分记录 (管理员功能)
- **请求参数**:
  - `userId`: 用户ID
  - `amount`: 积分数量
  - `operationType`: 操作类型
  - `description`: 描述
  - `relatedId`: 关联ID (可选)

#### 8.4 用户签到获得积分
- **接口**: `POST /api/coins/checkin/{userId}`
- **功能**: 用户每日签到获得积分
- **路径参数**: `userId` - 用户ID

#### 8.5 获取用户积分余额
- **接口**: `GET /api/coins/balance/{userId}`
- **功能**: 获取指定用户的积分余额
- **路径参数**: `userId` - 用户ID

#### 8.6 获取积分排行榜
- **接口**: `GET /api/coins/leaderboard`
- **功能**: 获取积分排行榜
- **请求参数**: `limit` - 限制数量 (默认10)

#### 8.7 获取积分统计信息
- **接口**: `GET /api/coins/statistics`
- **功能**: 获取积分统计信息

#### 8.8 发布文章奖励积分
- **接口**: `POST /api/coins/publish-reward/{userId}`
- **功能**: 发布文章获得积分奖励
- **路径参数**: `userId` - 用户ID
- **请求参数**: `postId` - 文章ID

#### 8.9 评论奖励积分
- **接口**: `POST /api/coins/comment-reward/{userId}`
- **功能**: 发表评论获得积分奖励
- **路径参数**: `userId` - 用户ID
- **请求参数**: `commentId` - 评论ID

#### 8.10 消费积分
- **接口**: `POST /api/coins/consume/{userId}`
- **功能**: 消费积分
- **路径参数**: `userId` - 用户ID
- **请求参数**:
  - `amount`: 消费数量
  - `description`: 消费描述
  - `relatedId`: 关联ID (可选)

---

## 9. 系统管理模块

### 9.1 管理日志模块 (AdminLogController)
- **基础路径**: `/api/admin-logs`
- **功能**: 记录和查询管理员操作日志

### 9.2 角色管理模块 (RoleController)
- **基础路径**: `/api/roles`
- **功能**: 角色创建、更新、删除、权限分配

### 9.3 系统配置模块 (SystemConfigController)
- **基础路径**: `/api/system-configs`
- **功能**: 系统配置参数管理

### 9.4 用户积分管理模块 (UserCoinController)
- **基础路径**: `/api/user-coins`
- **功能**: 用户积分账户管理

### 9.5 点赞记录模块 (LikeRecordController)
- **基础路径**: `/api/like-records`
- **功能**: 点赞记录查询和统计

### 9.6 测试异常模块 (TestExceptionController)
- **基础路径**: `/api/test`
- **功能**: 异常处理测试接口

---

## 认证和权限控制

### JWT 认证系统
系统采用基于 JWT 的无状态认证机制：

- **访问令牌**: 有效期 24 小时，用于 API 访问认证
- **刷新令牌**: 有效期 7 天，用于刷新访问令牌
- **认证过滤器**: 自动验证 JWT 令牌
- **无状态 Session**: 使用 JWT，不依赖服务器 Session

### 权限控制注解
- `@RequiresAuthentication`: 需要用户认证
- `@RequiresRole`: 需要特定角色
- `@RequiresPermission`: 需要特定权限
- `@PublicAccess`: 公开访问，无需认证

### 角色和权限
- **ADMIN**: 管理员，可以管理所有资源
- **EDITOR**: 编辑者，可以查看用户列表等
- **USER**: 普通用户，只能访问自己的资源

### 请求头格式
```
Authorization: Bearer {access_token}
```

---

## 文件上传配置

### 上传限制
- **最大文件大小**: 10MB
- **最大请求大小**: 50MB
- **允许的文件类型**: jpg, jpeg, png, gif, mp4, avi, pdf, doc, docx, txt, zip, gzip

### 存储路径
- **上传路径**: `./uploads/`
- **URL 访问**: `/files/preview/{fileId}` 或 `/files/download/{fileId}`

---

## 数据库配置

### 连接信息
- **数据库**: MySQL 8.0
- **数据库名**: `Blogs`
- **默认端口**: 3306
- **字符集**: utf8mb4
- **时区**: Asia/Shanghai

### 连接池配置
- **连接池**: HikariCP
- **最大连接数**: 20

---

## 错误处理

### 统一异常处理
系统实现了全局异常处理器，统一处理各种异常：

- **业务异常**: `BusinessException`
- **资源不存在**: `ResourceNotFoundException`
- **验证异常**: `ValidationException`
- **认证异常**: `UnauthorizedException`
- **权限异常**: `ForbiddenException`
- **文件上传异常**: `FileUploadException`

### 异常响应格式
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "success": false
}
```

---

## 开发和部署

### 构建命令
```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package

# 运行应用
mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests
```

### 运行环境
- **Java**: 21+
- **Maven**: 3.6+
- **MySQL**: 8.0+

### 默认配置
- **应用端口**: 8080
- **API 基础路径**: `/api`
- **完整访问地址**: `http://localhost:8080/api`

### 数据库初始化
1. 执行 `sql/create_tables.sql` 创建表结构
2. 执行 `sql/init_data.sql` 插入初始数据
3. 执行 `sql/optimize_indexes.sql` 优化索引 (可选)

### 默认测试账户
- **管理员**: username=`admin`, password=`admin123`
- **普通用户**: username=`testuser`, password=`user123`

---

## 项目状态

**✅ 项目完成度**: 100%

该博客系统是一个功能完整的企业级应用，包含了现代博客系统的所有核心功能：

1. **✅ 用户体系完整**: 注册、登录、权限管理
2. **✅ 内容管理完整**: 文章发布、分类标签、评论互动
3. **✅ 安全体系完整**: JWT 认证、RBAC 权限控制
4. **✅ 文件管理完整**: 上传、下载、预览、管理
5. **✅ 积分系统完整**: 奖励机制、排行榜、签到
6. **✅ 系统管理完整**: 配置管理、日志记录、异常处理
7. **✅ API 设计完整**: RESTful 设计、统一响应格式
8. **✅ 数据库设计完整**: 完整的实体关系、索引优化

系统采用现代化的技术栈和最佳实践，代码结构清晰，功能模块化，具有良好的可扩展性和维护性，可以直接用于生产环境部署。