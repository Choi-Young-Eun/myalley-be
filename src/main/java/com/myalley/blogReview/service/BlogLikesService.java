package com.myalley.blogReview.service;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.domain.BlogLikes;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.blogReview.repository.BlogLikesRepository;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import com.myalley.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogLikesService {
    private final BlogLikesRepository likesRepository;

    public Boolean switchBlogLikes(BlogReview blogReview, Member member) {
        if(blogReview.getMember().getMemberId()==member.getMemberId())
            throw new CustomException(BlogReviewExceptionType.LIKES_BAD_REQUEST);
        BlogLikes like = likesRepository.selectLike(member.getMemberId(),blogReview.getId())
                .orElseGet(() -> BlogLikes.builder().blog(blogReview).member(member).build());
        like.changeLikesStatus();
        likesRepository.save(like);
        return !like.getIsDeleted();
    }

    public BlogListResponseDto findMyLikedBlogReviews(Member member, Integer pageNo){
        if(pageNo != null)
            return likesRepository.findAllByMemberId(pageNo.longValue()-1, member.getMemberId());
        else
            return likesRepository.findAllByMemberId(0L, member.getMemberId());
    }

    @Transactional
    public void removeBlogLikesByBlogReview(BlogReview blogReview){
        likesRepository.deleteAllByBlog(blogReview);
    }
}
