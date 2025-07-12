package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.FileUpload;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件上传Mapper接口
 */
@Mapper
public interface FileUploadMapper extends BaseMapper<FileUpload> {

    /**
     * 根据用户查询文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    IPage<FileUpload> selectFilesByUserId(Page<FileUpload> page, @Param("userId") Long userId);

    /**
     * 根据文件类型查询文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE file_type = #{fileType} AND is_deleted = 0 ORDER BY create_time DESC")
    IPage<FileUpload> selectFilesByType(Page<FileUpload> page, @Param("fileType") Integer fileType);

    /**
     * 根据关联对象查询文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE related_type = #{relatedType} AND related_id = #{relatedId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<FileUpload> selectFilesByRelated(@Param("relatedType") Integer relatedType, @Param("relatedId") Long relatedId);

    /**
     * 统计文件数量
     */
    @Select("SELECT COUNT(*) FROM tb_file_upload WHERE file_type = #{fileType} AND status = #{status} AND is_deleted = 0")
    Long countFiles(@Param("fileType") Integer fileType, @Param("status") Integer status);

    /**
     * 统计用户文件总大小
     */
    @Select("SELECT COALESCE(SUM(file_size), 0) FROM tb_file_upload WHERE user_id = #{userId} AND is_deleted = 0")
    Long sumFileSizeByUserId(@Param("userId") Long userId);

    /**
     * 查询临时文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE status = 0 AND create_time < #{beforeTime} AND is_deleted = 0")
    List<FileUpload> selectTempFiles(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量删除临时文件
     */
    @Update("UPDATE tb_file_upload SET is_deleted = 1 WHERE status = 0 AND create_time < #{beforeTime}")
    int deleteTempFiles(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 更新文件状态
     */
    @Update("UPDATE tb_file_upload SET status = #{status} WHERE id = #{id}")
    int updateFileStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据文件路径查询文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE file_path = #{filePath} AND is_deleted = 0")
    FileUpload selectByFilePath(@Param("filePath") String filePath);

    /**
     * 查询用户最近上传的文件
     */
    @Select("SELECT * FROM tb_file_upload WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<FileUpload> selectRecentFilesByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
}
