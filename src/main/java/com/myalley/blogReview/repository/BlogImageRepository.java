package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogImage;
import com.myalley.blogReview.domain.BlogReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogImageRepository extends JpaRepository<BlogImage, Long>, BlogImageRepositoryCustom {
    Optional<BlogImage> findByIdAndBlog(Long imageId, BlogReview blog);
    List<BlogImage> findAllByBlog(BlogReview blog);
    Integer countByBlog(BlogReview blog);
    List<BlogImage> findAllByBlogIn(List<BlogReview> blogReviewList);
}
