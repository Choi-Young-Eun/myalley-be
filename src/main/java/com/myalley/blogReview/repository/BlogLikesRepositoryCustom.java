package com.myalley.blogReview.repository;

import com.myalley.blogReview.dto.response.BlogListResponseDto;

public interface BlogLikesRepositoryCustom {
    BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId);
}
