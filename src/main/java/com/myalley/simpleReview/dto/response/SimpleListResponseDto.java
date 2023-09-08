package com.myalley.simpleReview.dto.response;

import com.myalley.common.dto.pagingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SimpleListResponseDto {
    private List<SimpleListDto> simpleInfo;
    private pagingDto pageInfo;
}
