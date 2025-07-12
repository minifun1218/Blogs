package org.easytech.blogs.exception;

/**
 * 数据验证异常
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(400, message);
    }
    
    public ValidationException(String field, String message) {
        super(400, String.format("字段 %s %s", field, message));
    }
}
