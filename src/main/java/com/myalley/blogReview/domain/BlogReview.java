package com.myalley.blogReview.domain;

import com.myalley.blogReview.dto.request.BlogRequestDto;
import com.myalley.exhibition.domain.Exhibition;
import com.myalley.common.domain.BaseTime;
import com.myalley.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name="blog_review")
@NoArgsConstructor
@Getter
public class BlogReview extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;
    @Column(nullable = false)
    private LocalDate viewDate;
    @Column(nullable = false)
    private String time;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 3000)
    private String content;
    @Column(nullable = false)
    private Integer likeCount = 0;
    @Column(nullable = false)
    private Integer viewCount = 0;
    @Column(nullable = false)
    private Integer bookmarkCount = 0;

    private String transportation;
    private String revisit;
    private String congestion;
    private Boolean isDeleted=false;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    @OneToMany(mappedBy = "blog")
    private List<BlogImage> images = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "display_image_id")
    private BlogImage displayImage;

    @Builder
    public BlogReview(String title, String content, LocalDate viewDate, String time, String transportation,
                      String revisit, String congestion, Member member, Exhibition exhibition){
        this.title = title;
        this.content = content;
        this.viewDate = viewDate;
        this.time = time;
        this.transportation = transportation;
        this.revisit = revisit;
        this.congestion = congestion;
        this.member = member;
        this.exhibition = exhibition;
    }

    public void updateReview(BlogRequestDto target){
        this.title= target.getTitle();
        this.content = target.getContent();
        this.viewDate = LocalDate.parse(target.getViewDate());
        this.time = target.getTime();
        if(target.getCongestion() != null)
            this.congestion = target.getCongestion();
        if(target.getRevisit() != null)
            this.revisit = target.getRevisit();
        if(target.getTransportation() != null)
            this.transportation = target.getTransportation();
    }

    public void setImage(BlogImage image){
        this.images.add(image);
    }
    public void setDisplayImage(BlogImage image) { this.displayImage=image; }

    //좋아요 관리
    public void likesCountUp(){ this.likeCount++; }
    public void likesCountDown(){ this.likeCount--; }
    
    //북마크 관리
    public void bookmarkCountUp(){ this.bookmarkCount++; }
    public void bookmarkCountDown(){ this.bookmarkCount--; }
}
