package org.easytech.blogs.common;

import lombok.Data;

import java.util.Objects;

/**
 * 统一响应结果类
 */
@Data
public class Result<T> {
    // ---------- 默认常量 ----------
    private static final int    CODE_SUCCESS       = 200;
    private static final String MSG_SUCCESS        = "操作成功";
    private static final int    CODE_ERROR         = 500;
    private static final String MSG_ERROR          = "操作失败";
    private static final int    CODE_BAD_REQUEST   = 400;
    private static final int    CODE_UNAUTHORIZED  = 401;
    private static final String MSG_UNAUTHORIZED   = "未授权访问";
    private static final int    CODE_FORBIDDEN     = 403;
    private static final String MSG_FORBIDDEN      = "禁止访问";
    private static final int    CODE_NOT_FOUND     = 404;
    private static final String MSG_NOT_FOUND      = "资源不存在";

    private Integer code;
    private String  message;
    private T       data;
    private Long    timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    // --------- 成功 ---------
    public static <T> Result<T> success() {
        return new Result<>(CODE_SUCCESS, MSG_SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(CODE_SUCCESS, MSG_SUCCESS, data);
    }

    public static <T> Result<T> success(String message, T data) {
        String msg = (message == null || message.isBlank()) ? MSG_SUCCESS : message;
        return new Result<>(CODE_SUCCESS, msg, data);
    }

    public static <T> Result<T> success(String message) {
        String msg = (message == null || message.isBlank()) ? MSG_SUCCESS : message;
        return new Result<>(CODE_SUCCESS, msg, null);
    }

    // --------- 失败 ---------
    public static <T> Result<T> error() {
        return new Result<>(CODE_ERROR, MSG_ERROR);
    }

    public static <T> Result<T> error(String message) {
        String msg = (message == null || message.isBlank()) ? MSG_ERROR : message;
        return new Result<>(CODE_ERROR, msg, null);
    }

    public static <T> Result<T> error(int code, String message) {
        String msg = (message == null || message.isBlank()) ? MSG_ERROR : message;
        return new Result<>(code, msg, null);
    }

    // --------- 参数错误 ---------
    public static <T> Result<T> badRequest(String message) {
        String msg = (message == null || message.isBlank()) ? MSG_ERROR : message;
        return new Result<>(CODE_BAD_REQUEST, msg, null);
    }

    // --------- 未授权 ---------
    public static <T> Result<T> unauthorized() {
        return new Result<>(CODE_UNAUTHORIZED, MSG_UNAUTHORIZED);
    }

    // --------- 禁止访问 ---------
    public static <T> Result<T> forbidden() {
        return new Result<>(CODE_FORBIDDEN, MSG_FORBIDDEN);
    }

    // --------- 资源不存在 ---------
    /** 不带自定义消息 */
    public static <T> Result<T> notFound() {
        return new Result<>(CODE_NOT_FOUND, MSG_NOT_FOUND);
    }

    /** 带自定义消息（blank 或 null 时降为默认） */
    public static <T> Result<T> notFound(String message) {
        String msg = (message == null || message.isBlank()) ? MSG_NOT_FOUND : message;
        return new Result<>(CODE_NOT_FOUND, msg, null);
    }

    // --------- 工具方法 ---------
    /** 是否成功（code==200） */
    public boolean isSuccess() {
        return Objects.equals(this.code, CODE_SUCCESS);
    }
}
