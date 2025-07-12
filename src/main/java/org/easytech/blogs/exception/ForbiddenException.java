package org.easytech.blogs.exception;

/**
 * 禁止访问异常
 */
public class ForbiddenException extends BusinessException {
    
    public ForbiddenException() {
        super(403, "禁止访问");
    }
    
    public ForbiddenException(String message) {
        super(403, message);
    }
}
