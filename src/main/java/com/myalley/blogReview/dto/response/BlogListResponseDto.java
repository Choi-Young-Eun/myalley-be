package com.myalley.blogReview.dto.response;

import com.myalley.common.dto.pagingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlogListResponseDto {
    private List<BlogListDto> blogInfo;
    private pagingDto pageInfo;
}
