package com.myalley.blogReview.repository;

import com.myalley.blogReview.dto.response.BlogListResponseDto;

public interface BlogBookmarkRepositoryCustom {
    BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId);
}
