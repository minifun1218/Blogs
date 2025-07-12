# 全局异常处理器使用指南

## 概述

本项目实现了完整的全局异常处理机制，统一处理系统中的各种异常，提供一致的错误响应格式。

## 自定义异常类

### 1. BusinessException - 业务异常
用于处理业务逻辑相关的异常。

```java
// 抛出业务异常
throw new BusinessException("用户已被禁用");
throw new BusinessException(400, "操作失败");
```

### 2. ResourceNotFoundException - 资源不存在异常
用于处理资源不存在的情况。

```java
// 抛出资源不存在异常
throw new ResourceNotFoundException("用户", "id", userId);
throw new ResourceNotFoundException("文章不存在");
```

### 3. ValidationException - 数据验证异常
用于处理数据验证失败的情况。

```java
// 抛出验证异常
throw new ValidationException("用户名不能为空");
throw new ValidationException("email", "格式不正确");
```

### 4. UnauthorizedException - 未授权异常
用于处理用户未登录或token无效的情况。

```java
// 抛出未授权异常
throw new UnauthorizedException();
throw new UnauthorizedException("Token已过期");
```

### 5. ForbiddenException - 禁止访问异常
用于处理用户权限不足的情况。

```java
// 抛出禁止访问异常
throw new ForbiddenException();
throw new ForbiddenException("您没有管理员权限");
```

### 6. FileUploadException - 文件上传异常
用于处理文件上传相关的异常。

```java
// 抛出文件上传异常
throw new FileUploadException("文件大小超过限制");
throw new FileUploadException("不支持的文件类型");
```

## 错误响应格式

所有异常都会被转换为统一的响应格式：

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "timestamp": 1640995200000
}
```

对于参数验证异常，还会包含详细的验证错误信息：

```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": {
    "code": 400,
    "message": "参数验证失败",
    "path": "/api/users",
    "timestamp": "2024-01-01T12:00:00",
    "validationErrors": [
      {
        "field": "username",
        "rejectedValue": "",
        "message": "用户名不能为空"
      }
    ]
  },
  "timestamp": 1640995200000
}
```

## 支持的异常类型

### 自定义异常
- `BusinessException` → 500 (可自定义状态码)
- `ResourceNotFoundException` → 404
- `ValidationException` → 400
- `UnauthorizedException` → 401
- `ForbiddenException` → 403
- `FileUploadException` → 400

### Spring框架异常
- `MethodArgumentNotValidException` → 400 (参数验证失败)
- `BindException` → 400 (参数绑定失败)
- `MissingServletRequestParameterException` → 400 (缺少请求参数)
- `MethodArgumentTypeMismatchException` → 400 (参数类型不匹配)
- `HttpMessageNotReadableException` → 400 (请求体不可读)
- `HttpRequestMethodNotSupportedException` → 405 (不支持的HTTP方法)
- `HttpMediaTypeNotSupportedException` → 415 (不支持的媒体类型)
- `NoHandlerFoundException` → 404 (找不到处理器)
- `MaxUploadSizeExceededException` → 400 (文件上传大小超限)

### 数据库异常
- `DataIntegrityViolationException` → 400 (数据完整性违反)

### 系统异常
- `RuntimeException` → 500 (运行时异常)
- `Exception` → 500 (其他未处理异常)

## 使用示例

### 在Service中使用

```java
@Service
public class UserServiceImpl implements UserService {
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        return user;
    }
    
    public User createUser(String username, String email) {
        if (existsByUsername(username)) {
            throw new ValidationException("用户名已存在");
        }
        if (existsByEmail(email)) {
            throw new ValidationException("邮箱已存在");
        }
        // 创建用户逻辑...
    }
}
```

### 在Controller中使用参数验证

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public Result<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        // 如果参数验证失败，会自动抛出MethodArgumentNotValidException
        // 全局异常处理器会捕获并返回400错误
        return Result.success(userService.createUser(request));
    }
}

public class CreateUserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    // getters and setters...
}
```

## 测试异常处理

项目提供了测试控制器 `TestExceptionController` 来验证异常处理是否正常工作：

```bash
# 测试业务异常
curl http://localhost:8080/api/test/exception/business

# 测试资源不存在异常
curl http://localhost:8080/api/test/exception/not-found

# 测试参数验证异常
curl -X POST http://localhost:8080/api/test/exception/param-validation \
  -H "Content-Type: application/json" \
  -d '{"name":"","age":null}'

# 测试缺少请求参数异常
curl http://localhost:8080/api/test/exception/missing-param

# 测试参数类型不匹配异常
curl http://localhost:8080/api/test/exception/type-mismatch?number=abc
```

## 最佳实践

1. **使用合适的异常类型**：根据具体场景选择合适的自定义异常类型。

2. **提供清晰的错误信息**：异常信息应该清晰、具体，便于前端展示和问题排查。

3. **避免在Controller中处理异常**：让全局异常处理器统一处理，保持Controller的简洁。

4. **记录异常日志**：重要的异常应该记录日志，便于问题排查。

5. **参数验证**：使用Bean Validation注解进行参数验证，而不是手动检查。

6. **生产环境安全**：生产环境中不要暴露敏感的系统信息，如堆栈跟踪等。

## 注意事项

1. **删除测试控制器**：生产环境部署前应删除 `TestExceptionController`。

2. **日志级别**：根据异常的严重程度设置合适的日志级别。

3. **国际化**：如需支持多语言，可以结合Spring的国际化功能。

4. **监控告警**：建议对500错误设置监控告警。
