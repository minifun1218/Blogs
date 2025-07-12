package org.easytech.blogs.common;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 错误响应类
 */
@Data
public class ErrorResponse {
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 详细错误信息
     */
    private String details;
    
    /**
     * 请求路径
     */
    private String path;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 验证错误列表
     */
    private List<ValidationError> validationErrors;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public ErrorResponse(Integer code, String message, String path) {
        this(code, message);
        this.path = path;
    }
    
    public ErrorResponse(Integer code, String message, String details, String path) {
        this(code, message, path);
        this.details = details;
    }
    
    /**
     * 验证错误内部类
     */
    @Data
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;
        
        public ValidationError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }
}
