package com.myalley.blogReview.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myalley.exhibition.dto.response.ExhibitionBlogDto;
import com.myalley.member.dto.MemberBlogDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BlogDetailResponseDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate viewDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String title;
    private String content;
    private String time;
    private Integer likeCount;
    private Integer viewCount;
    private Integer bookmarkCount;
    private String transportation;
    private String revisit;
    private String congestion;
    private Boolean likeStatus;
    private Boolean bookmarkStatus;
    private List<ImageDto> imageInfo;

    private MemberBlogDto memberInfo;
    private ExhibitionBlogDto exhibitionInfo;


    public void updateViewCount(){
        this.viewCount++;
    }

    public void setImageInfo(List<ImageDto> imageInfo){
        this.imageInfo=imageInfo;
    }

    public void transformLikeAndBookmarkStatus(){
        if(this.getLikeStatus() != null && this.getLikeStatus().equals(false))
            this.likeStatus=true;
        else
            this.likeStatus=false;
        if(this.getBookmarkStatus() != null && this.getBookmarkStatus().equals(false))
            this.bookmarkStatus=true;
        else
            this.bookmarkStatus=false;
    }
}
