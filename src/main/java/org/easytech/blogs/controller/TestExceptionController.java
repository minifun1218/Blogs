package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.exception.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 异常处理测试控制器
 * 仅用于测试全局异常处理器，生产环境应删除
 */
@RestController
@RequestMapping("/test/exception")
@CrossOrigin
public class TestExceptionController {

    /**
     * 测试业务异常
     */
    @GetMapping("/business")
    public Result<String> testBusinessException() {
        throw new BusinessException("这是一个业务异常测试");
    }

    /**
     * 测试资源不存在异常
     */
    @GetMapping("/not-found")
    public Result<String> testResourceNotFoundException() {
        throw new ResourceNotFoundException("用户", "id", 999L);
    }

    /**
     * 测试数据验证异常
     */
    @GetMapping("/validation")
    public Result<String> testValidationException() {
        throw new ValidationException("用户名", "不能为空");
    }

    /**
     * 测试未授权异常
     */
    @GetMapping("/unauthorized")
    public Result<String> testUnauthorizedException() {
        throw new UnauthorizedException("您没有访问权限");
    }

    /**
     * 测试禁止访问异常
     */
    @GetMapping("/forbidden")
    public Result<String> testForbiddenException() {
        throw new ForbiddenException("禁止访问该资源");
    }

    /**
     * 测试文件上传异常
     */
    @GetMapping("/file-upload")
    public Result<String> testFileUploadException() {
        throw new FileUploadException("文件上传失败");
    }

    /**
     * 测试运行时异常
     */
    @GetMapping("/runtime")
    public Result<String> testRuntimeException() {
        throw new RuntimeException("这是一个运行时异常");
    }

    /**
     * 测试空指针异常
     */
    @GetMapping("/null-pointer")
    public Result<String> testNullPointerException() {
        String str = null;
        return Result.success(str.length() + ""); // 故意触发空指针异常
    }

    /**
     * 测试参数验证异常
     */
    @PostMapping("/param-validation")
    public Result<String> testParamValidation(@Valid @RequestBody TestRequest request) {
        return Result.success("参数验证通过");
    }

    /**
     * 测试缺少请求参数异常
     */
    @GetMapping("/missing-param")
    public Result<String> testMissingParam(@RequestParam String requiredParam) {
        return Result.success("参数: " + requiredParam);
    }

    /**
     * 测试参数类型不匹配异常
     */
    @GetMapping("/type-mismatch")
    public Result<String> testTypeMismatch(@RequestParam Integer number) {
        return Result.success("数字: " + number);
    }

    /**
     * 测试数组越界异常
     */
    @GetMapping("/array-index")
    public Result<String> testArrayIndexOutOfBounds() {
        int[] array = {1, 2, 3};
        return Result.success("值: " + array[10]); // 故意触发数组越界
    }

    /**
     * 测试除零异常
     */
    @GetMapping("/divide-zero")
    public Result<String> testDivideByZero() {
        int result = 10 / 0; // 故意触发除零异常
        return Result.success("结果: " + result);
    }

    /**
     * 测试请求体
     */
    public static class TestRequest {
        @NotBlank(message = "姓名不能为空")
        private String name;

        @NotNull(message = "年龄不能为空")
        private Integer age;

        // getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
