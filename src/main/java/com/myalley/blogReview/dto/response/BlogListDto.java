package com.myalley.blogReview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BlogListDto {
    private Long id;
    private String title;
    private String writer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate viewDate;
    private Integer viewCount;
    private ImageDto imageInfo;

}
