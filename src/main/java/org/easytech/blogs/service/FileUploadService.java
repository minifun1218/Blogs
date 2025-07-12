package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 * 提供文件上传相关的业务逻辑处理
 */
public interface FileUploadService {

    /**
     * 上传文件
     * @param file 上传的文件
     * @param userId 用户ID
     * @param relatedType 关联类型（1-文章，2-用户头像等）
     * @param relatedId 关联对象ID
     * @return 文件上传记录
     */
    FileUpload uploadFile(MultipartFile file, Long userId, Integer relatedType, Long relatedId);

    /**
     * 批量上传文件
     * @param files 上传的文件数组
     * @param userId 用户ID
     * @param relatedType 关联类型
     * @param relatedId 关联对象ID
     * @return 文件上传记录列表
     */
    List<FileUpload> uploadFiles(MultipartFile[] files, Long userId, Integer relatedType, Long relatedId);

    /**
     * 删除文件
     * @param fileId 文件ID
     * @param userId 用户ID（权限验证）
     * @return 删除结果
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 根据ID获取文件信息
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileUpload getFileById(Long fileId);

    /**
     * 根据ID获取文件信息（别名方法）
     * @param fileId 文件ID
     * @return 文件信息
     */
    default FileUpload getById(Long fileId) {
        return getFileById(fileId);
    }

    /**
     * 根据文件路径获取文件信息
     * @param filePath 文件路径
     * @return 文件信息
     */
    FileUpload getFileByPath(String filePath);

    /**
     * 分页查询用户文件
     * @param page 分页参数
     * @param userId 用户ID
     * @return 文件分页列表
     */
    IPage<FileUpload> getFilesByUserId(Page<FileUpload> page, Long userId);

    /**
     * 根据文件类型分页查询文件
     * @param page 分页参数
     * @param fileType 文件类型
     * @return 文件分页列表
     */
    IPage<FileUpload> getFilesByType(Page<FileUpload> page, Integer fileType);

    /**
     * 根据关联对象查询文件列表
     * @param relatedType 关联类型
     * @param relatedId 关联对象ID
     * @return 文件列表
     */
    List<FileUpload> getFilesByRelated(Integer relatedType, Long relatedId);

    /**
     * 获取用户最近上传的文件
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 最近上传的文件列表
     */
    List<FileUpload> getRecentFilesByUserId(Long userId, Integer limit);

    /**
     * 统计用户文件总大小
     * @param userId 用户ID
     * @return 文件总大小（字节）
     */
    Long getTotalFileSizeByUserId(Long userId);

    /**
     * 统计用户文件总大小（别名方法）
     * @param userId 用户ID
     * @return 文件总大小（字节）
     */
    default Long getTotalFileSize(Long userId) {
        return getTotalFileSizeByUserId(userId);
    }

    /**
     * 统计文件数量
     * @param fileType 文件类型，null表示所有类型
     * @param status 文件状态，null表示所有状态
     * @return 文件数量
     */
    Long countFiles(Integer fileType, Integer status);

    /**
     * 统计文件数量（别名方法）
     * @param fileType 文件类型
     * @param status 文件状态
     * @return 文件数量
     */
    default Long getFileCount(Integer fileType, Integer status) {
        return countFiles(fileType, status);
    }

    /**
     * 更新文件状态
     * @param fileId 文件ID
     * @param status 文件状态
     * @return 更新结果
     */
    boolean updateFileStatus(Long fileId, Integer status);

    /**
     * 清理临时文件
     * @param hours 清理多少小时前的临时文件
     * @return 清理的文件数量
     */
    int cleanTempFiles(int hours);

    /**
     * 验证文件类型是否允许
     * @param fileName 文件名
     * @return 是否允许上传
     */
    boolean isFileTypeAllowed(String fileName);

    /**
     * 验证文件大小是否符合限制
     * @param fileSize 文件大小（字节）
     * @return 是否符合限制
     */
    boolean isFileSizeAllowed(long fileSize);

    /**
     * 生成唯一文件名
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    String generateUniqueFileName(String originalFileName);

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 文件扩展名
     */
    String getFileExtension(String fileName);

    /**
     * 根据文件扩展名判断文件类型
     * @param extension 文件扩展名
     * @return 文件类型（1-图片，2-视频，3-文档，4-压缩包，0-其他）
     */
    Integer getFileTypeByExtension(String extension);
}