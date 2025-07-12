package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.FileUpload;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ForbiddenException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.FileUploadMapper;
import org.easytech.blogs.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 * 实现文件上传相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadMapper fileUploadMapper;

    @Value("${blog.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${blog.upload.max-file-size:10485760}") // 10MB
    private long maxFileSize;

    @Value("${blog.upload.allowed-types:jpg,jpeg,png,gif,mp4,avi,pdf,doc,docx,txt,zip,gzip}")
    private String allowedTypes;

    // 文件类型常量
    private static final Integer FILE_TYPE_IMAGE = 1;
    private static final Integer FILE_TYPE_VIDEO = 2;
    private static final Integer FILE_TYPE_DOCUMENT = 3;
    private static final Integer FILE_TYPE_ARCHIVE = 4;
    private static final Integer FILE_TYPE_OTHER = 0;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileUpload uploadFile(MultipartFile file, Long userId, Integer relatedType, Long relatedId) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new ValidationException("上传文件不能为空");
        }

        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFileName)) {
            throw new ValidationException("文件名不能为空");
        }

        // 验证文件类型
        if (!isFileTypeAllowed(originalFileName)) {
            throw new ValidationException("不支持的文件类型");
        }

        // 验证文件大小
        if (!isFileSizeAllowed(file.getSize())) {
            throw new ValidationException("文件大小超过限制");
        }

        try {
            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + "/" + datePath;
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // 生成唯一文件名
            String uniqueFileName = generateUniqueFileName(originalFileName);
            String filePath = datePath + "/" + uniqueFileName;
            String fullPath = uploadDir + "/" + uniqueFileName;

            // 保存文件
            file.transferTo(new File(fullPath));

            // 创建文件记录
            FileUpload fileUpload = new FileUpload();
            fileUpload.setUserId(userId);
            fileUpload.setOriginalName(originalFileName);
            fileUpload.setFileName(uniqueFileName);
            fileUpload.setFilePath(filePath);
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFileType(getFileTypeByExtension(getFileExtension(originalFileName)));
            fileUpload.setMimeType(file.getContentType());
            fileUpload.setRelatedType(relatedType);
            fileUpload.setRelatedId(relatedId);
            fileUpload.setStatus(1); // 1-正常，0-临时

            int result = fileUploadMapper.insert(fileUpload);
            if (result > 0) {
                log.info("文件上传成功，用户ID: {}, 文件名: {}, 文件大小: {}", userId, originalFileName, file.getSize());
                return fileUpload;
            }
        } catch (IOException e) {
            log.error("文件保存失败，用户ID: {}, 文件名: {}", userId, originalFileName, e);
            throw new BusinessException("文件保存失败");
        } catch (Exception e) {
            log.error("文件上传失败，用户ID: {}, 文件名: {}", userId, originalFileName, e);
            throw new BusinessException("文件上传失败，请稍后重试");
        }

        throw new BusinessException("文件上传失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileUpload> uploadFiles(MultipartFile[] files, Long userId, Integer relatedType, Long relatedId) {
        if (files == null || files.length == 0) {
            throw new ValidationException("上传文件不能为空");
        }

        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        List<FileUpload> uploadResults = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    FileUpload result = uploadFile(file, userId, relatedType, relatedId);
                    uploadResults.add(result);
                } catch (Exception e) {
                    log.error("批量上传文件失败，文件名: {}", file.getOriginalFilename(), e);
                    // 可以选择继续上传其他文件或者全部回滚
                    throw new BusinessException("文件上传失败: " + file.getOriginalFilename());
                }
            }
        }

        return uploadResults;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId, Long userId) {
        if (fileId == null) {
            throw new ValidationException("文件ID不能为空");
        }

        FileUpload fileUpload = fileUploadMapper.selectById(fileId);
        if (fileUpload == null) {
            throw new ResourceNotFoundException("文件不存在");
        }

        // 权限验证：只有文件上传者可以删除文件
        if (userId != null && !fileUpload.getUserId().equals(userId)) {
            throw new ForbiddenException("没有权限删除此文件");
        }

        try {
            // 删除物理文件
            String fullPath = uploadPath + "/" + fileUpload.getFilePath();
            Path filePath = Paths.get(fullPath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // 逻辑删除文件记录
            int result = fileUploadMapper.deleteById(fileId);
            if (result > 0) {
                log.info("文件删除成功，文件ID: {}", fileId);
                return true;
            }
        } catch (IOException e) {
            log.error("删除物理文件失败，文件ID: {}", fileId, e);
            throw new BusinessException("删除物理文件失败");
        } catch (Exception e) {
            log.error("文件删除失败，文件ID: {}", fileId, e);
            throw new BusinessException("文件删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public FileUpload getFileById(Long fileId) {
        if (fileId == null) {
            return null;
        }
        return fileUploadMapper.selectById(fileId);
    }

    @Override
    public FileUpload getFileByPath(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return null;
        }
        return fileUploadMapper.selectByFilePath(filePath);
    }

    @Override
    public IPage<FileUpload> getFilesByUserId(Page<FileUpload> page, Long userId) {
        if (userId == null) {
            return page;
        }
        return fileUploadMapper.selectFilesByUserId(page, userId);
    }

    @Override
    public IPage<FileUpload> getFilesByType(Page<FileUpload> page, Integer fileType) {
        if (fileType == null) {
            return page;
        }
        return fileUploadMapper.selectFilesByType(page, fileType);
    }

    @Override
    public List<FileUpload> getFilesByRelated(Integer relatedType, Long relatedId) {
        if (relatedType == null || relatedId == null) {
            return List.of();
        }
        return fileUploadMapper.selectFilesByRelated(relatedType, relatedId);
    }

    @Override
    public List<FileUpload> getRecentFilesByUserId(Long userId, Integer limit) {
        if (userId == null) {
            return List.of();
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        return fileUploadMapper.selectRecentFilesByUserId(userId, limit);
    }

    @Override
    public Long getTotalFileSizeByUserId(Long userId) {
        if (userId == null) {
            return 0L;
        }
        return fileUploadMapper.sumFileSizeByUserId(userId);
    }

    @Override
    public Long countFiles(Integer fileType, Integer status) {
        return fileUploadMapper.countFiles(fileType, status);
    }

    @Override
    public boolean updateFileStatus(Long fileId, Integer status) {
        if (fileId == null || status == null) {
            throw new ValidationException("参数不能为空");
        }

        try {
            int result = fileUploadMapper.updateFileStatus(fileId, status);
            if (result > 0) {
                log.info("文件状态更新成功，文件ID: {}, 状态: {}", fileId, status);
                return true;
            }
        } catch (Exception e) {
            log.error("文件状态更新失败，文件ID: {}", fileId, e);
            throw new BusinessException("文件状态更新失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanTempFiles(int hours) {
        if (hours <= 0) {
            hours = 24; // 默认清理24小时前的临时文件
        }

        LocalDateTime beforeTime = LocalDateTime.now().minusHours(hours);

        try {
            // 查找需要清理的临时文件
            List<FileUpload> tempFiles = fileUploadMapper.selectTempFiles(beforeTime);

            int deletedCount = 0;
            for (FileUpload fileUpload : tempFiles) {
                try {
                    // 删除物理文件
                    String fullPath = uploadPath + "/" + fileUpload.getFilePath();
                    Path filePath = Paths.get(fullPath);
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                    }
                    deletedCount++;
                } catch (IOException e) {
                    log.warn("删除临时文件失败，文件路径: {}", fileUpload.getFilePath(), e);
                }
            }

            // 批量删除数据库记录
            if (!tempFiles.isEmpty()) {
                fileUploadMapper.deleteTempFiles(beforeTime);
            }

            log.info("清理临时文件完成，清理数量: {}", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理临时文件失败", e);
            throw new BusinessException("清理临时文件失败");
        }
    }

    @Override
    public boolean isFileTypeAllowed(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }

        String extension = getFileExtension(fileName).toLowerCase();
        if (!StringUtils.hasText(extension)) {
            return false;
        }

        String[] allowedTypeArray = allowedTypes.toLowerCase().split(",");
        return Arrays.asList(allowedTypeArray).contains(extension);
    }

    @Override
    public boolean isFileSizeAllowed(long fileSize) {
        return fileSize > 0 && fileSize <= maxFileSize;
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        if (StringUtils.hasText(extension)) {
            return timestamp + "_" + uuid + "." + extension;
        } else {
            return timestamp + "_" + uuid;
        }
    }

    @Override
    public String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }

        return "";
    }

    @Override
    public Integer getFileTypeByExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            return FILE_TYPE_OTHER;
        }

        String ext = extension.toLowerCase();

        // 图片类型
        if (Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp").contains(ext)) {
            return FILE_TYPE_IMAGE;
        }

        // 视频类型
        if (Arrays.asList("mp4", "avi", "mov", "wmv", "flv", "mkv", "webm").contains(ext)) {
            return FILE_TYPE_VIDEO;
        }

        // 文档类型
        if (Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf").contains(ext)) {
            return FILE_TYPE_DOCUMENT;
        }

        // 压缩包类型
        if (Arrays.asList("zip", "rar", "7z", "tar", "gz", "gzip", "bz2").contains(ext)) {
            return FILE_TYPE_ARCHIVE;
        }

        return FILE_TYPE_OTHER;
    }
}