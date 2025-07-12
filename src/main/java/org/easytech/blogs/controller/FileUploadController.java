package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.config.FileUploadConfig;
import org.easytech.blogs.entity.FileUpload;
import org.easytech.blogs.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public Result<FileUpload> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "relatedType", required = false) Integer relatedType,
            @RequestParam(value = "relatedId", required = false) Long relatedId) {
        try {
            FileUpload uploadedFile = fileUploadService.uploadFile(file, userId, relatedType, relatedId);
            return Result.success("文件上传成功", uploadedFile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/upload-multiple")
    public Result<List<FileUpload>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "relatedType", required = false) Integer relatedType,
            @RequestParam(value = "relatedId", required = false) Long relatedId) {
        try {
            List<FileUpload> uploadedFiles = fileUploadService.uploadFiles(files, userId, relatedType, relatedId);
            return Result.success("文件上传成功", uploadedFiles);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 图片上传（专用于富文本编辑器）
     */
    @PostMapping("/upload-image")
    public Result<FileUpload> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            // 检查是否为图片文件
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.badRequest("只能上传图片文件");
            }
            
            FileUpload uploadedFile = fileUploadService.uploadFile(file, userId, null, null);
            return Result.success("图片上传成功", uploadedFile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            FileUpload fileUpload = fileUploadService.getById(fileId);
            if (fileUpload == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(fileUploadConfig.getPath() + fileUpload.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileUpload.getMimeType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + fileUpload.getOriginalName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 文件预览（主要用于图片）
     */
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<Resource> previewFile(@PathVariable Long fileId) {
        try {
            FileUpload fileUpload = fileUploadService.getById(fileId);
            if (fileUpload == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(fileUploadConfig.getPath() + fileUpload.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileUpload.getMimeType()))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    public Result<Void> deleteFile(@PathVariable Long fileId,
                                  @RequestHeader(value = "User-Id", required = false) Long userId) {
        boolean success = fileUploadService.deleteFile(fileId, userId);
        if (success) {
            return Result.success("文件删除成功");
        } else {
            return Result.error("文件删除失败");
        }
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{fileId}")
    public Result<FileUpload> getFileDetail(@PathVariable Long fileId) {
        FileUpload fileUpload = fileUploadService.getById(fileId);
        if (fileUpload == null) {
            return Result.notFound();
        }
        return Result.success(fileUpload);
    }

    /**
     * 根据用户查询文件
     */
    @GetMapping("/user/{userId}")
    public Result<PageResult<FileUpload>> getFilesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<FileUpload> page = new Page<>(current, size);
        PageResult<FileUpload> result = PageResult.of(fileUploadService.getFilesByUserId(page, userId));
        return Result.success(result);
    }

    /**
     * 根据文件类型查询文件
     */
    @GetMapping("/type/{fileType}")
    public Result<PageResult<FileUpload>> getFilesByType(
            @PathVariable Integer fileType,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<FileUpload> page = new Page<>(current, size);
        PageResult<FileUpload> result = PageResult.of(fileUploadService.getFilesByType(page, fileType));
        return Result.success(result);
    }

    /**
     * 根据关联对象查询文件
     */
    @GetMapping("/related")
    public Result<List<FileUpload>> getFilesByRelated(
            @RequestParam Integer relatedType,
            @RequestParam Long relatedId) {
        List<FileUpload> files = fileUploadService.getFilesByRelated(relatedType, relatedId);
        return Result.success(files);
    }

    /**
     * 更新文件状态
     */
    @PutMapping("/{fileId}/status")
    public Result<Void> updateFileStatus(@PathVariable Long fileId, @RequestParam Integer status) {
        boolean success = fileUploadService.updateFileStatus(fileId, status);
        return success ? Result.success("状态更新成功", null) : Result.error("状态更新失败");
    }

    /**
     * 获取文件统计信息
     */
    @GetMapping("/stats")
    public Result<FileStatsResponse> getFileStats(
            @RequestParam(required = false) Integer fileType,
            @RequestParam(required = false) Integer status) {
        Long count = fileUploadService.getFileCount(fileType, status);
        FileStatsResponse stats = new FileStatsResponse();
        stats.setCount(count);
        return Result.success(stats);
    }

    /**
     * 获取用户文件大小统计
     */
    @GetMapping("/size/user/{userId}")
    public Result<Long> getUserFileSize(@PathVariable Long userId) {
        Long totalSize = fileUploadService.getTotalFileSize(userId);
        return Result.success(totalSize);
    }

    /**
     * 清理临时文件（管理员功能）
     */
    @PostMapping("/clean-temp")
    public Result<Integer> cleanTempFiles(@RequestParam(defaultValue = "24") int hours) {
        int cleanedCount = fileUploadService.cleanTempFiles(hours);
        return Result.success("清理完成", cleanedCount);
    }

    /**
     * 获取上传配置信息
     */
    @GetMapping("/config")
    public Result<UploadConfigResponse> getUploadConfig() {
        UploadConfigResponse config = new UploadConfigResponse();
        config.setMaxSize(fileUploadConfig.getMaxSize());
        config.setAllowedTypes(fileUploadConfig.getAllowedTypesList());
        return Result.success(config);
    }

    // 响应类
    public static class FileStatsResponse {
        private Long count;

        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    public static class UploadConfigResponse {
        private Long maxSize;
        private List<String> allowedTypes;

        public Long getMaxSize() { return maxSize; }
        public void setMaxSize(Long maxSize) { this.maxSize = maxSize; }
        public List<String> getAllowedTypes() { return allowedTypes; }
        public void setAllowedTypes(List<String> allowedTypes) { this.allowedTypes = allowedTypes; }
    }
}
