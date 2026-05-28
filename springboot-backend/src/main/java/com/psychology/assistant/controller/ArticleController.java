package com.psychology.assistant.controller;

import com.psychology.assistant.common.ApiResponse;
import com.psychology.assistant.common.PageResult;
import com.psychology.assistant.model.dto.ArticleSaveRequest;
import com.psychology.assistant.model.entity.ArticleRow;
import com.psychology.assistant.model.vo.ArticleVO;
import com.psychology.assistant.service.ArticleService;
import com.psychology.assistant.util.JsonArrayUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping
    public ApiResponse<PageResult<ArticleVO>> list(@RequestParam(defaultValue = "") String title,
                                                   @RequestParam(defaultValue = "") String category,
                                                   @RequestParam(defaultValue = "") String status,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<ArticleRow> result = articleService.getArticles(title, category, status, page, pageSize);
        List<ArticleVO> articleVOList = new ArrayList<ArticleVO>();
        for (ArticleRow row : result.getList()) {
            articleVOList.add(toArticleVO(row));
        }
        return ApiResponse.success(new PageResult<ArticleVO>(articleVOList, result.getTotal(), result.getPage(), result.getPageSize()));
    }

    @GetMapping("/categories")
    public ApiResponse<Object> categories() {
        return ApiResponse.success(articleService.getCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<Object> detail(@PathVariable Long id) {
        return ApiResponse.success(toArticleVO(articleService.getArticleDetail(id)));
    }

    @PostMapping
    public ApiResponse<Object> create(@RequestBody ArticleSaveRequest request) {
        return ApiResponse.success("创建成功", toArticleVO(articleService.createArticle(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Object> update(@PathVariable Long id, @RequestBody ArticleSaveRequest request) {
        return ApiResponse.success("更新成功", toArticleVO(articleService.updateArticle(id, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ApiResponse.success("删除成功", true);
    }

    private ArticleVO toArticleVO(ArticleRow row) {
        ArticleVO vo = new ArticleVO();
        vo.setId(row.getId());
        vo.setTitle(row.getTitle());
        vo.setCategory(row.getCategory());
        vo.setSummary(row.getSummary());
        vo.setReadCount(row.getReadCount());
        vo.setStatus(row.getStatus());
        vo.setUpdatedAt(row.getUpdatedAt());
        vo.setContent(row.getContent());
        vo.setTags(JsonArrayUtil.parseJsonArray(row.getTags()));
        return vo;
    }
}
