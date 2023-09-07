package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogLikes;
import com.myalley.blogReview.dto.response.BlogListResponseDto;

public interface BlogLikesRepositoryCustom {
    BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId);
    BlogLikes findLikesLogByMemberIdAndBlogId(Long memberId, Long blogId);
    void deleteAllByBlogId(Long blogId);
}
