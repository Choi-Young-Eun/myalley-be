package com.myalley.blogReview.domain;

import com.myalley.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name="blog_likes")
@Getter
@NoArgsConstructor
public class BlogLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name="blog_id", nullable = false)
    private BlogReview blog;


    @Builder
    public BlogLikes(Member member, BlogReview blog){
        this.member = member;
        this.blog = blog;
    }

    public void changeLikesStatus() {
        if(isDeleted == null || isDeleted.equals(Boolean.TRUE)) {
            this.isDeleted=Boolean.FALSE;
            this.blog.likesCountUp();
        }
        else {
            this.isDeleted = Boolean.TRUE;
            this.blog.likesCountDown();
        }
        this.updatedAt=LocalDateTime.now();
    }
}
