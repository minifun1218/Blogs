package org.easytech.blogs.exception;

/**
 * 文件上传异常
 */
public class FileUploadException extends BusinessException {
    
    public FileUploadException(String message) {
        super(400, message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
