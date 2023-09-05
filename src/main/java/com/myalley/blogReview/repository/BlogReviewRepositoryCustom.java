package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;

import java.util.List;

public interface BlogReviewRepositoryCustom {
    void updateBlogStatus(Long blogId);
    void updateBlogViewCount(Long blogId, Integer viewCount);
    void deleteListPermanently(List<Long> blogIdList);
    List<Long> findRemovedByIdList(Long memberId, List<Long> blogIdList);
    BlogDetailResponseDto findDetailedByBlogId(Long blogId, Long memberId);
    List<BlogReview> findAllByExhibitionId(Long exhibitionId);
    BlogListResponseDto findPagedBlogReviews(Long pageNo, String orderType, String word, Long exhibitionId);
    BlogListResponseDto findPagedBlogReviewsByMemberId(Long pageNo, Long memberId, boolean deleteMode);
}
