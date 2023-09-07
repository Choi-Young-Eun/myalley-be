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
        BlogLikes likes = likesRepository.findLikesLogByMemberIdAndBlogId(member.getMemberId(), blogReview.getId());
        if(likes == null)
            likes = BlogLikes.builder().blog(blogReview).member(member).build();
        likes.changeLikesStatus();
        likesRepository.save(likes);
        return !likes.getIsDeleted();
    }

    public BlogListResponseDto findMyLikedBlogReviews(Member member, Integer pageNo){
        if(pageNo != null)
            return likesRepository.findAllByMemberId(pageNo.longValue()-1, member.getMemberId());
        else
            return likesRepository.findAllByMemberId(0L, member.getMemberId());
    }

    @Transactional
    public void removeBlogLikesByBlogReview(Long blogId){
        likesRepository.deleteAllByBlogId(blogId);
    }
}
