package com.psychology.assistant.service;

import com.psychology.assistant.common.PageResult;
import com.psychology.assistant.common.exception.BusinessException;
import com.psychology.assistant.mapper.ArticleMapper;
import com.psychology.assistant.model.dto.ArticleSaveRequest;
import com.psychology.assistant.model.entity.ArticleRow;
import com.psychology.assistant.model.entity.Category;
import com.psychology.assistant.util.JsonArrayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    public PageResult<ArticleRow> getArticles(String title, String category, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = articleMapper.countArticles(title, category, status);
        List<ArticleRow> list = articleMapper.selectArticles(title, category, status, pageSize, offset);
        return new PageResult<ArticleRow>(list, total, page, pageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public ArticleRow getArticleDetail(Long id) {
        articleMapper.increaseReadCount(id);
        ArticleRow row = articleMapper.selectArticleDetail(id);
        if (row == null) {
            throw new BusinessException(404, "文章不存在");
        }
        return row;
    }

    public List<Category> getCategories() {
        return articleMapper.selectCategories();
    }

    @Transactional(rollbackFor = Exception.class)
    public ArticleRow createArticle(ArticleSaveRequest request) {
        ArticleMapper.ArticleInsertRecord record = new ArticleMapper.ArticleInsertRecord();
        record.setCategoryId(request.getCategoryId());
        record.setTitle(request.getTitle());
        record.setCoverUrl(request.getCoverUrl());
        record.setSummary(request.getSummary());
        record.setContent(request.getContent());
        record.setReadCount(request.getReadCount() == null ? 0 : request.getReadCount());
        record.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        record.setCreatedBy(request.getCreatedBy() == null ? 1L : request.getCreatedBy());
        articleMapper.insertArticle(record);
        syncTags(record.getId(), request.getTags());
        return getArticleDetail(record.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public ArticleRow updateArticle(Long id, ArticleSaveRequest request) {
        ArticleMapper.ArticleUpdateRecord record = new ArticleMapper.ArticleUpdateRecord();
        record.setId(id);
        record.setCategoryId(request.getCategoryId());
        record.setTitle(request.getTitle());
        record.setCoverUrl(request.getCoverUrl());
        record.setSummary(request.getSummary());
        record.setContent(request.getContent());
        record.setReadCount(request.getReadCount() == null ? 0 : request.getReadCount());
        record.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        articleMapper.updateArticle(record);
        syncTags(id, request.getTags());
        return getArticleDetail(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        int affectedRows = articleMapper.deleteArticle(id);
        if (affectedRows == 0) {
            throw new BusinessException(404, "文章不存在");
        }
    }

    private void syncTags(Long articleId, List<String> tags) {
        articleMapper.deleteArticleTags(articleId);
        if (tags == null) {
            return;
        }

        for (String tag : tags) {
            if (tag == null || tag.trim().isEmpty()) {
                continue;
            }
            String name = tag.trim();
            articleMapper.upsertTag(name);
            Long tagId = articleMapper.selectTagId(name);
            if (tagId != null) {
                articleMapper.insertArticleTag(articleId, tagId);
            }
        }
    }

    public ArticleRow parseTags(ArticleRow row) {
        row.setTags(String.join(",", JsonArrayUtil.parseJsonArray(row.getTags())));
        return row;
    }
}
