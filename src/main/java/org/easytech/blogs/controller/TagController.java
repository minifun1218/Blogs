package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.Tag;
import org.easytech.blogs.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tags")
@CrossOrigin
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 创建标签（管理员功能）
     */
    @PostMapping
    public Result<Tag> createTag(@RequestBody CreateTagRequest request) {
        try {
            Tag tag = tagService.createTag(request.getName(), request.getColor());
            return Result.success("标签创建成功", tag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新标签（管理员功能）
     */
    @PutMapping("/{id}")
    public Result<Tag> updateTag(@PathVariable Long id, @RequestBody UpdateTagRequest request) {
        try {
            Tag tag = tagService.updateTag(id, request.getName(), request.getColor());
            return Result.success("标签更新成功", tag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除标签（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        try {
            boolean success = tagService.deleteTag(id);
            return success ? Result.success("删除成功", null) : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagDetail(@PathVariable Long id) {
        Tag tag = tagService.getById(id);
        if (tag == null) {
            return Result.notFound();
        }
        return Result.success(tag);
    }

    /**
     * 查询所有标签
     */
    @GetMapping
    public Result<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public Result<List<Tag>> getHotTags(@RequestParam(defaultValue = "20") Integer limit) {
        List<Tag> tags = tagService.getHotTags(limit);
        return Result.success(tags);
    }

    /**
     * 根据文章ID查询标签
     */
    @GetMapping("/post/{postId}")
    public Result<List<Tag>> getTagsByPostId(@PathVariable Long postId) {
        List<Tag> tags = tagService.getTagsByPostId(postId);
        return Result.success(tags);
    }

    /**
     * 根据名称查询标签
     */
    @GetMapping("/name/{name}")
    public Result<Tag> getTagByName(@PathVariable String name) {
        Tag tag = tagService.findByName(name);
        if (tag == null) {
            return Result.notFound();
        }
        return Result.success(tag);
    }

    /**
     * 检查标签名称是否存在
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkTagName(@RequestParam String name) {
        boolean exists = tagService.existsByName(name);
        return Result.success(exists);
    }

    /**
     * 批量查询标签
     */
    @PostMapping("/batch")
    public Result<List<Tag>> getTagsByNames(@RequestBody BatchQueryRequest request) {
        List<Tag> tags = tagService.getTagsByNames(request.getNames());
        return Result.success(tags);
    }

    /**
     * 更新标签使用次数（管理员功能）
     */
    @PutMapping("/{id}/update-use-count")
    public Result<Void> updateUseCount(@PathVariable Long id) {
        boolean success = tagService.updateUseCount(id);
        return success ? Result.success("更新成功", null) : Result.error("更新失败");
    }

    // 请求参数类
    public static class CreateTagRequest {
        private String name;
        private String color;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class UpdateTagRequest {
        private String name;
        private String color;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }

    public static class BatchQueryRequest {
        private List<String> names;

        // getters and setters
        public List<String> getNames() { return names; }
        public void setNames(List<String> names) { this.names = names; }
    }
}
