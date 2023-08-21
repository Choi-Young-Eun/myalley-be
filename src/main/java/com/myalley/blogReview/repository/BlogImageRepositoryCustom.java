package com.myalley.blogReview.repository;

import com.myalley.blogReview.dto.response.ImageDto;

import java.util.List;

public interface BlogImageRepositoryCustom {
    List<ImageDto> findAllByBlogReviewId(Long blogId);
}
