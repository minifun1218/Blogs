package org.easytech.blogs.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends BusinessException {
    
    public UnauthorizedException() {
        super(401, "未授权访问");
    }
    
    public UnauthorizedException(String message) {
        super(401, message);
    }
}
