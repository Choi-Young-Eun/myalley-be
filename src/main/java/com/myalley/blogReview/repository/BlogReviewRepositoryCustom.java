package com.myalley.blogReview.repository;


import com.myalley.blogReview.domain.BlogReview;

import java.util.List;

public interface BlogReviewRepositoryCustom {
    void updateBlogStatus(Long blogId);
    List<BlogReview> findRemovedByIdList(Long memberId, List<Long> blogId);
    void deleteListPermanently(List<Long> blogIdList);
}
