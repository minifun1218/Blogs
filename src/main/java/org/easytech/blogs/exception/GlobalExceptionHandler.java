package org.easytech.blogs.exception;

import org.easytech.blogs.common.ErrorResponse;
import org.easytech.blogs.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<ErrorResponse>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        logger.warn("业务异常: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getCode(), 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getCode()).body(result);
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<ErrorResponse>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("资源不存在: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            404, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.notFound();
        result.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理数据验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<ErrorResponse>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        logger.warn("数据验证异常: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            400, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.badRequest(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理未授权异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result<ErrorResponse>> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        logger.warn("未授权访问: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            401, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.unauthorized();
        result.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    /**
     * 处理禁止访问异常
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Result<ErrorResponse>> handleForbiddenException(
            ForbiddenException ex, HttpServletRequest request) {
        logger.warn("禁止访问: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            403, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.forbidden();
        result.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Result<ErrorResponse>> handleFileUploadException(
            FileUploadException ex, HttpServletRequest request) {
        logger.warn("文件上传异常: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            400, 
            ex.getMessage(), 
            request.getRequestURI()
        );
        
        Result<ErrorResponse> result = Result.badRequest(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("参数验证失败: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            400, 
            "参数验证失败", 
            request.getRequestURI()
        );
        errorResponse.setValidationErrors(validationErrors);
        
        Result<ErrorResponse> result = Result.badRequest("参数验证失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<ErrorResponse>> handleBindException(
            BindException ex, HttpServletRequest request) {
        logger.warn("参数绑定异常: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            400, 
            "参数绑定失败", 
            request.getRequestURI()
        );
        errorResponse.setValidationErrors(validationErrors);
        
        Result<ErrorResponse> result = Result.badRequest("参数绑定失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<ErrorResponse>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        logger.warn("缺少请求参数: {}", ex.getMessage());

        String message = String.format("缺少必需的请求参数: %s", ex.getParameterName());
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<ErrorResponse>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("参数类型不匹配: {}", ex.getMessage());

        String message = String.format("参数 %s 的值 %s 类型不正确", ex.getName(), ex.getValue());
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<ErrorResponse>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.warn("HTTP消息不可读: {}", ex.getMessage());

        String message = "请求体格式错误或不可读";
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            ex.getMessage(),
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理不支持的HTTP方法异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<ErrorResponse>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.warn("不支持的HTTP方法: {}", ex.getMessage());

        String message = String.format("不支持的HTTP方法: %s", ex.getMethod());
        ErrorResponse errorResponse = new ErrorResponse(
            405,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.error(405, message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }

    /**
     * 处理不支持的媒体类型异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<ErrorResponse>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        logger.warn("不支持的媒体类型: {}", ex.getMessage());

        String message = String.format("不支持的媒体类型: %s", ex.getContentType());
        ErrorResponse errorResponse = new ErrorResponse(
            415,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.error(415, message);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(result);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<ErrorResponse>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("找不到处理器: {}", ex.getMessage());

        String message = String.format("找不到路径: %s", ex.getRequestURL());
        ErrorResponse errorResponse = new ErrorResponse(
            404,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.notFound();
        result.setMessage(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<ErrorResponse>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        logger.warn("文件上传大小超限: {}", ex.getMessage());

        String message = "上传文件大小超过限制";
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            ex.getMessage(),
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理数据完整性违反异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<ErrorResponse>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        logger.error("数据完整性违反: {}", ex.getMessage());

        String message = "数据操作违反完整性约束";
        // 可以根据具体的约束违反类型提供更友好的错误信息
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "数据已存在，不能重复添加";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "数据关联约束违反，无法执行操作";
        }

        ErrorResponse errorResponse = new ErrorResponse(
            400,
            message,
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<ErrorResponse>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        logger.error("运行时异常: ", ex);

        String message = "系统运行异常";
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            message,
            ex.getMessage(),
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<ErrorResponse>> handleException(
            Exception ex, HttpServletRequest request) {
        logger.error("未处理的异常: ", ex);

        String message = "系统内部错误";
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            message,
            ex.getMessage(),
            request.getRequestURI()
        );

        Result<ErrorResponse> result = Result.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
