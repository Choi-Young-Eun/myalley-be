package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogBookmark;
import com.myalley.blogReview.domain.BlogReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogBookmarkRepository extends JpaRepository<BlogBookmark, Long>, BlogBookmarkRepositoryCustom{
    void deleteAllByBlog(BlogReview blogReview);
}
