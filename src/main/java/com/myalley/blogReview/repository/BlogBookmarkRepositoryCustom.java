package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogBookmark;
import com.myalley.blogReview.dto.response.BlogListResponseDto;

public interface BlogBookmarkRepositoryCustom {
    BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId);
    BlogBookmark findBookmarkLogByMemberIdAndBlogId(Long memberId, Long blogId);
}
