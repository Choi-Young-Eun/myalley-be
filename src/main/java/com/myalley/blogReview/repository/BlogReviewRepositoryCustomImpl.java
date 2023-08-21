package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import com.myalley.exhibition.dto.response.ExhibitionBlogDto;
import com.myalley.member.dto.MemberBlogDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.myalley.blogReview.domain.QBlogBookmark.blogBookmark;
import static com.myalley.blogReview.domain.QBlogLikes.blogLikes;
import static com.myalley.blogReview.domain.QBlogReview.blogReview;
import static com.myalley.exhibition.domain.QExhibition.exhibition;
import static com.myalley.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class BlogReviewRepositoryCustomImpl implements BlogReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Transactional
    @Override
    public void updateBlogStatus(Long blogId) {
        queryFactory.update(blogReview)
                .set(blogReview.isDeleted,true)
                .set(blogReview.likeCount,0)
                .set(blogReview.bookmarkCount,0)
                .where(blogReview.id.eq(blogId))
                .execute();
    }

    @Transactional
    @Override
    public void updateBlogViewCount(Long blogId, Integer viewCount) {
        queryFactory.update(blogReview)
                        .set(blogReview.viewCount,viewCount)
                        .where(blogReview.id.eq(blogId))
                        .execute();
    }

    @Override
    public List<BlogReview> findRemovedByIdList(Long memberId, List<Long> blogId) {
        return queryFactory.selectFrom(blogReview)
                .where(blogReview.isDeleted.isTrue(),
                        blogReview.id.in(blogId),
                        blogReview.member.memberId.eq(memberId))
                .fetch();
    }

    @Transactional
    @Override
    public void deleteListPermanently(List<Long> blogIdList) {
        queryFactory.delete(blogReview)
                .where(blogReview.id.in(blogIdList))
                .execute();
    }

    @Override
    public BlogDetailResponseDto findDetailedByBlogId(Long blogId, Long memberId){
        BlogDetailResponseDto blogDetailDto = queryFactory.select(Projections.fields(BlogDetailResponseDto.class,
                blogReview.id,
                blogReview.viewDate,
                blogReview.createdAt,
                blogReview.title,
                blogReview.content,
                blogReview.time,
                blogReview.likeCount,
                blogReview.viewCount,
                blogReview.bookmarkCount,
                blogReview.transportation,
                blogReview.revisit,
                blogReview.congestion,
                blogLikes.isDeleted.as("likeStatus"),
                blogBookmark.isDeleted.as("bookmarkStatus"),
                        Projections.fields(MemberBlogDto.class,
                                member.memberId,
                                member.nickname,
                                member.memberImage).as("memberInfo"),
                        Projections.fields(ExhibitionBlogDto.class,
                                exhibition.id,
                                exhibition.title,
                                exhibition.posterUrl,
                                exhibition.duration,
                                exhibition.space,
                                exhibition.type).as("exhibitionInfo")))
                .from(blogReview).leftJoin(blogLikes)
                .on(blogLikes.blog.id.eq(blogId), blogLikes.member.memberId.eq(memberId))
                .leftJoin(blogBookmark)
                .on(blogBookmark.blog.id.eq(blogId), blogBookmark.member.memberId.eq(memberId))
                .join(member)
                .on(member.memberId.eq(blogReview.member.memberId))
                .join(exhibition)
                .on(exhibition.id.eq(blogReview.exhibition.id))
                .where(blogReview.id.eq(blogId))
                .fetchOne();
        if(blogDetailDto == null) {
            throw new CustomException(BlogReviewExceptionType.BLOG_NOT_FOUND);
        }
        blogDetailDto.updateViewCount();
        blogDetailDto.transformLikeAndBookmarkStatus();
        return blogDetailDto;
    }
}