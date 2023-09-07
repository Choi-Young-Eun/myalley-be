package com.myalley.blogReview.domain;

import com.myalley.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name="blog_bookmark")
@Getter
@NoArgsConstructor
public class BlogBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "b_bookmarks_id")
    private Long id;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "blog_id", nullable = false)
    private BlogReview blog;


    @Builder
    public BlogBookmark(Member member, BlogReview blog){
        this.member = member;
        this.blog = blog;
    }

    public void changeBookmarkStatus(){
        if(isDeleted == null || isDeleted.equals(Boolean.TRUE)){
            this.isDeleted = Boolean.FALSE;
            this.blog.bookmarkCountUp();
        }
        else {
            this.isDeleted = Boolean.TRUE;
            this.blog.bookmarkCountDown();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
