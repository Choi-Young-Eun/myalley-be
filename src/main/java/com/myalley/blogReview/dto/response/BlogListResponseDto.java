package com.myalley.blogReview.dto.response;

import com.myalley.common.dto.pagingDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BlogListResponseDto {
    private List<BlogListDto> blogInfo;
    private pagingDto pageInfo;

}
