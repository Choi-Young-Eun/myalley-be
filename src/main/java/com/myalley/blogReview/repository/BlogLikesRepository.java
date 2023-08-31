package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.domain.BlogLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlogLikesRepository extends JpaRepository<BlogLikes, Long>, BlogLikesRepositoryCustom {
    @Query(value="select * from blog_likes bl where bl.member_id=?1 and bl.blog_id=?2", nativeQuery = true)
    Optional<BlogLikes> selectLike(Long memberId, Long blogId);
    void deleteAllByBlog(BlogReview blogReview);
}
