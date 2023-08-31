package com.myalley.blogReview.service;

import com.myalley.blogReview.domain.BlogBookmark;
import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.blogReview.repository.BlogBookmarkRepository;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import com.myalley.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogBookmarkService {
    private final BlogBookmarkRepository bookmarkRepository;

    public Boolean switchBlogBookmark(BlogReview blogReview, Member member) {
        if(blogReview.getMember().getMemberId() == member.getMemberId())
            throw new CustomException(BlogReviewExceptionType.BOOKMARK_FORBIDDEN);
        BlogBookmark bookmark = bookmarkRepository.selectBookmark(member.getMemberId(), blogReview.getId())
                .orElse(BlogBookmark.builder().blog(blogReview).member(member).build());
        bookmark.changeBookmarkStatus();
        bookmarkRepository.save(bookmark);
        return !bookmark.getIsDeleted();
    }

    public BlogListResponseDto findMyBookmarkedBlogReviews(Member member, Integer pageNo){
        if(pageNo != null)
            return bookmarkRepository.findAllByMemberId(pageNo.longValue()-1, member.getMemberId());
        else
            return bookmarkRepository.findAllByMemberId(0L, member.getMemberId());
    }

    @Transactional
    public void removeBlogBookmarksByBlogReview(BlogReview blogReview){ bookmarkRepository.deleteAllByBlog(blogReview); }
}
