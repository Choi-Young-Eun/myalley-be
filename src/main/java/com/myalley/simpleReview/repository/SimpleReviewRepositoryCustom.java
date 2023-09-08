package com.myalley.simpleReview.repository;

import com.myalley.simpleReview.dto.response.SimpleListResponseDto;

public interface SimpleReviewRepositoryCustom {
    SimpleListResponseDto findPagedSimpleReviewsByExhibitionId(Long pageNo, String orderType, Long exhibitionId);
    SimpleListResponseDto findPagedSimpleReviewsByMemberId(Long pageNo, Long memberId);
    void deleteAllByExhibitionId(Long exhibitionId);
}
