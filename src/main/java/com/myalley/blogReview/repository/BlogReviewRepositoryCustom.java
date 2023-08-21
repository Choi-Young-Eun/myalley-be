package com.myalley.blogReview.repository;


import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;

import java.util.List;

public interface BlogReviewRepositoryCustom {

    void updateBlogStatus(Long blogId);
    void updateBlogViewCount(Long blogId, Integer viewCount);
    List<BlogReview> findRemovedByIdList(Long memberId, List<Long> blogId);
    void deleteListPermanently(List<Long> blogIdList);
    BlogDetailResponseDto findDetailedByBlogId(Long blogId, Long memberId);
}
