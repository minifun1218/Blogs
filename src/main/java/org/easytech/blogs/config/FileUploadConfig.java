package org.easytech.blogs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传配置类
 */
@Configuration
@ConfigurationProperties(prefix = "blog.upload")
public class FileUploadConfig {

    /**
     * 上传路径
     */
    private String path = "./uploads/";

    /**
     * 最大文件大小（字节）
     */
    private Long maxSize = 10485760L; // 10MB

    /**
     * 允许的文件类型
     */
    private String allowedTypes = "jpg,jpeg,png,gif,mp4,avi,pdf,doc,docx,txt";

    /**
     * 获取允许的文件类型列表
     */
    public List<String> getAllowedTypesList() {
        return Arrays.asList(allowedTypes.split(","));
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isAllowedType(String fileExtension) {
        return getAllowedTypesList().contains(fileExtension.toLowerCase());
    }

    /**
     * 根据文件扩展名获取文件类型
     */
    public Integer getFileType(String fileExtension) {
        String ext = fileExtension.toLowerCase();
        if (Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(ext)) {
            return 1; // 图片
        } else if (Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv").contains(ext)) {
            return 2; // 视频
        } else if (Arrays.asList("pdf", "doc", "docx", "txt", "md", "rtf").contains(ext)) {
            return 3; // 文档
        } else {
            return 4; // 其他
        }
    }

    // getters and setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
}
