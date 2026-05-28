package com.psychology.assistant.mapper;

import com.psychology.assistant.model.dto.ArticleSaveRequest;
import com.psychology.assistant.model.entity.ArticleRow;
import com.psychology.assistant.model.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select({
        "<script>",
        "SELECT COUNT(*) FROM knowledge_articles a ",
        "INNER JOIN article_categories c ON a.category_id = c.id ",
        "WHERE (#{title} = '' OR a.title LIKE CONCAT('%', #{title}, '%')) ",
        "AND (#{category} = '' OR c.name = #{category}) ",
        "AND (#{status} = '' OR a.status = #{status})",
        "</script>"
    })
    long countArticles(@Param("title") String title, @Param("category") String category, @Param("status") String status);

    @Select({
        "<script>",
        "SELECT a.id, a.title, c.name AS category, a.summary, a.read_count AS readCount, ",
        "CASE a.status WHEN 1 THEN '已发布' ELSE '草稿' END AS status, ",
        "DATE_FORMAT(a.updated_at, '%Y-%m-%d') AS updatedAt, ",
        "COALESCE(JSON_ARRAYAGG(t.name), JSON_ARRAY()) AS tags ",
        "FROM knowledge_articles a ",
        "INNER JOIN article_categories c ON a.category_id = c.id ",
        "LEFT JOIN article_tag_relations atr ON atr.article_id = a.id ",
        "LEFT JOIN article_tags t ON t.id = atr.tag_id ",
        "WHERE (#{title} = '' OR a.title LIKE CONCAT('%', #{title}, '%')) ",
        "AND (#{category} = '' OR c.name = #{category}) ",
        "AND (#{status} = '' OR a.status = #{status}) ",
        "GROUP BY a.id, a.title, c.name, a.summary, a.read_count, a.status, a.updated_at ",
        "ORDER BY a.updated_at DESC LIMIT #{pageSize} OFFSET #{offset}",
        "</script>"
    })
    List<ArticleRow> selectArticles(@Param("title") String title,
                                    @Param("category") String category,
                                    @Param("status") String status,
                                    @Param("pageSize") int pageSize,
                                    @Param("offset") int offset);

    @Select("SELECT a.id, a.title, c.name AS category, a.summary, a.content, a.read_count AS readCount, " +
        "CASE a.status WHEN 1 THEN '已发布' ELSE '草稿' END AS status, " +
        "DATE_FORMAT(a.updated_at, '%Y-%m-%d') AS updatedAt, " +
        "COALESCE(JSON_ARRAYAGG(t.name), JSON_ARRAY()) AS tags " +
        "FROM knowledge_articles a " +
        "INNER JOIN article_categories c ON a.category_id = c.id " +
        "LEFT JOIN article_tag_relations atr ON atr.article_id = a.id " +
        "LEFT JOIN article_tags t ON t.id = atr.tag_id " +
        "WHERE a.id = #{id} " +
        "GROUP BY a.id, a.title, c.name, a.summary, a.content, a.read_count, a.status, a.updated_at")
    ArticleRow selectArticleDetail(@Param("id") Long id);

    @Update("UPDATE knowledge_articles SET read_count = read_count + 1 WHERE id = #{id}")
    int increaseReadCount(@Param("id") Long id);

    @Select("SELECT id, name FROM article_categories WHERE status = 1 ORDER BY sort_no ASC, id ASC")
    List<Category> selectCategories();

    @Insert("INSERT INTO knowledge_articles(category_id, title, cover_url, summary, content, read_count, status, created_by) " +
        "VALUES(#{categoryId}, #{title}, #{coverUrl}, #{summary}, #{content}, #{readCount}, #{status}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertArticle(ArticleInsertRecord record);

    @Update("UPDATE knowledge_articles SET category_id = #{categoryId}, title = #{title}, cover_url = #{coverUrl}, " +
        "summary = #{summary}, content = #{content}, read_count = #{readCount}, status = #{status} WHERE id = #{id}")
    int updateArticle(ArticleUpdateRecord record);

    @Delete("DELETE FROM knowledge_articles WHERE id = #{id}")
    int deleteArticle(@Param("id") Long id);

    @Delete("DELETE FROM article_tag_relations WHERE article_id = #{articleId}")
    int deleteArticleTags(@Param("articleId") Long articleId);

    @Insert("INSERT INTO article_tags(name) VALUES(#{name}) ON DUPLICATE KEY UPDATE name = VALUES(name)")
    int upsertTag(@Param("name") String name);

    @Select("SELECT id FROM article_tags WHERE name = #{name} LIMIT 1")
    Long selectTagId(@Param("name") String name);

    @Insert("INSERT INTO article_tag_relations(article_id, tag_id) VALUES(#{articleId}, #{tagId})")
    int insertArticleTag(@Param("articleId") Long articleId, @Param("tagId") Long tagId);

    class ArticleInsertRecord {
        private Long id;
        private Long categoryId;
        private String title;
        private String coverUrl;
        private String summary;
        private String content;
        private Integer readCount;
        private Integer status;
        private Long createdBy;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getReadCount() { return readCount; }
        public void setReadCount(Integer readCount) { this.readCount = readCount; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public Long getCreatedBy() { return createdBy; }
        public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    }

    class ArticleUpdateRecord extends ArticleInsertRecord {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
