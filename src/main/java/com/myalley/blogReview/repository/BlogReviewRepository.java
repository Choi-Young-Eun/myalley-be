package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogReviewRepository extends JpaRepository<BlogReview, Long>, BlogReviewRepositoryCustom {
    Page<BlogReview> findAllByMember(Member member, Pageable pageable);
    @Query(value="select * from blog_review br where br.member_id=?1 and br.is_deleted=1",nativeQuery = true, countProjection = "blog_id")
    Page<BlogReview> selectRemovedAll(Member member, Pageable pageable);
}
