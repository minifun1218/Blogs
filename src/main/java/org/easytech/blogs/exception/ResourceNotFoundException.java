package org.easytech.blogs.exception;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(404, String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
