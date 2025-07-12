# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 blog system built with Java 21, MyBatis-Plus, and MySQL. The system includes user management, blog posts, comments, categories, tags, file uploads, and a coin/points system.

## Build and Development Commands

### Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application locally
mvn spring-boot:run

# Skip tests during build
mvn clean package -DskipTests
```

### Running the Application
```bash
# Run via Maven
mvn spring-boot:run

# Run packaged JAR
java -jar target/blogs-0.0.1-SNAPSHOT.jar

# Default port: 8080
# API base path: /api
# Full URL: http://localhost:8080/api
```

### Database Setup
Database scripts are in the `sql/` directory and must be executed in order:
```bash
# 1. Create tables and basic structure
mysql -u root -p < sql/create_tables.sql

# 2. Insert initial data and test accounts
mysql -u root -p < sql/init_data.sql

# 3. Apply performance optimizations (optional)
mysql -u root -p < sql/optimize_indexes.sql

# Alternative: Execute from MySQL command line
mysql -u root -p
source sql/create_tables.sql
source sql/init_data.sql
source sql/optimize_indexes.sql
```

## Architecture

### Package Structure
- `org.easytech.blogs.common` - Shared response classes (Result, ErrorResponse, PageResult)
- `org.easytech.blogs.config` - Configuration classes (MyBatis-Plus, Web, File Upload)
- `org.easytech.blogs.controller` - REST controllers for all endpoints
- `org.easytech.blogs.entity` - JPA entities mapped to database tables
- `org.easytech.blogs.exception` - Custom exceptions and global exception handler
- `org.easytech.blogs.mapper` - MyBatis-Plus mappers for data access
- `org.easytech.blogs.service` - Business logic interfaces and implementations

### Core Components

#### Service Layer Architecture
- **Interface-Implementation Pattern**: All services follow interface-implementation separation
- **Service Location**: Interfaces in `service/`, implementations in `service/impl/`
- **Transaction Management**: Use `@Transactional` for data consistency
- **Business Logic**: Core business rules implemented in service layer, not controllers

#### Database Access
- Uses **MyBatis-Plus** for ORM with auto-configuration
- Pagination enabled via `PaginationInnerInterceptor`
- Auto-fill for `createTime` and `updateTime` fields
- Logic delete support with `isDeleted` field
- Mapper scan configured for `org.easytech.blogs.mapper` package

#### Exception Handling
- Comprehensive global exception handler in `GlobalExceptionHandler`
- Custom business exceptions: `BusinessException`, `ResourceNotFoundException`, `ValidationException`, `UnauthorizedException`, `ForbiddenException`, `FileUploadException`
- Handles Spring validation errors with detailed field-level error responses
- Consistent error response format via `ErrorResponse` class

#### Response Format
All API responses use the standardized `Result<T>` wrapper:
```java
Result.success(data)    // 200 with data
Result.error(message)   // 500 with error message
Result.notFound()       // 404
Result.badRequest(msg)  // 400
Result.unauthorized()   // 401
Result.forbidden()      // 403
```

#### Security Configuration
- **Spring Security Integration**: Password encoding and authentication support
- **Password Encryption**: Uses BCrypt for secure password hashing
- **No Session Management**: Stateless configuration (suitable for API-first design)
- **CORS Configuration**: Cross-origin requests supported via WebConfig

#### File Upload
- Configured in `FileUploadConfig` and `application.yml`
- Upload path: `./uploads/`
- Max file size: 10MB, Max request size: 50MB
- Allowed types: jpg,jpeg,png,gif,mp4,avi,pdf,doc,docx,txt,zip,gzip
- Tracks uploads via `FileUpload` entity

#### Logging
- Uses Log4j2 (spring-boot-starter-logging excluded)
- Configuration in `src/main/resources/log4j2-spring.xml`
- Debug level for application packages
- Logs stored in `log/` directory

## Database Configuration

### Connection Details
- Database: `Blogs` (note: case-sensitive)
- URL: `jdbc:mysql://localhost:3306/Blogs?useSSL=false&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true`
- Default credentials: root/root (change for production)
- Character set: utf8mb4
- Timezone: Asia/Shanghai
- Connection pool: HikariCP with max 20 connections

### Key Entities
- **User Management**: `User`, `Role`, `UserRole`
- **Content**: `Post`, `Comment`, `Category`, `Tag`, `PostTag`
- **Features**: `FileUpload`, `LikeRecord`, `Coin`, `UserCoin`
- **System**: `AdminLog`, `SystemConfig`

## Testing

### Default Test Accounts
After running `init_data.sql`:
- **Admin**: username=`admin`, password=`admin123`
- **User**: username=`testuser`, password=`user123`

### Exception Testing
Use `TestExceptionController` endpoints to verify error handling:
```bash
curl http://localhost:8080/api/test/exception/business
curl http://localhost:8080/api/test/exception/not-found
curl -X POST http://localhost:8080/api/test/exception/param-validation -H "Content-Type: application/json" -d '{"name":"","age":null}'
```

## Development Notes

### Key Dependencies
- **Spring Boot**: 3.5.3 with Java 21
- **MyBatis-Plus**: 3.5.5 for database operations
- **MySQL Connector**: 8.0.33
- **Spring Security**: For password encryption
- **Lombok**: For reducing boilerplate code
- **Log4j2**: Custom logging configuration

### Custom Configuration Properties
The application supports custom configuration under `blog.*` prefix:
- `blog.upload.*` - File upload settings
- `blog.coin.*` - Points/rewards system settings

### Validation
- Uses Spring Boot Validation (`spring-boot-starter-validation`)
- Bean validation annotations supported on request DTOs
- Validation errors automatically handled by global exception handler

### MyBatis-Plus Features
- Automatic pagination
- Logic delete support
- Auto-fill timestamps
- Camel case to underscore mapping
- Debug SQL logging enabled

## Important Files

- `src/main/resources/application.yml` - Main configuration
- `src/main/resources/log4j2-spring.xml` - Logging configuration
- `src/main/java/org/easytech/blogs/config/MybatisPlusConfig.java` - Database configuration
- `src/main/java/org/easytech/blogs/exception/GlobalExceptionHandler.java` - Error handling
- `docs/exception-handling.md` - Detailed exception handling guide
- `sql/README.md` - Database setup instructions