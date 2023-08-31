package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogBookmark;
import com.myalley.blogReview.domain.BlogReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlogBookmarkRepository extends JpaRepository<BlogBookmark, Long>, BlogBookmarkRepositoryCustom{
    @Query(value="select * from blog_bookmark bb where bb.member_id=?1 and bb.blog_id=?2", nativeQuery = true)
    Optional<BlogBookmark> selectBookmark(Long memberId, Long blogId);
    void deleteAllByBlog(BlogReview blogReview);
}
