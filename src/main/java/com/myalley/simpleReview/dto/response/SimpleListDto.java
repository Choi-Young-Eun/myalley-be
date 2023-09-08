package com.myalley.simpleReview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myalley.exhibition.dto.response.ExhibitionSimpleReviewDto;
import com.myalley.member.dto.MemberBlogDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SimpleListDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate viewDate;
    private Integer rate;
    private String content;
    private String time;
    private String congestion;
    private MemberBlogDto memberInfo;
    private ExhibitionSimpleReviewDto exhibitionInfo;
}
