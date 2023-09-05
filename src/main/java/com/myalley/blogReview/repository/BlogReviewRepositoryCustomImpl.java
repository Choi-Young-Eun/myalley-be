package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;
import com.myalley.blogReview.dto.response.BlogListDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.blogReview.dto.response.ImageDto;
import com.myalley.common.dto.pagingDto;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import com.myalley.exhibition.dto.response.ExhibitionBlogDto;
import com.myalley.member.dto.MemberBlogDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    private final Long LIST_BASIC = 9L;
    private final Long LIST_MY_PAGE = 6L;


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

    @Transactional
    @Override
    public void deleteListPermanently(List<Long> blogIdList) {
        queryFactory.delete(blogReview)
                .where(blogReview.id.in(blogIdList))
                .execute();
    }

    @Override
    public List<Long> findRemovedByIdList(Long memberId, List<Long> blogIdList) {
        return queryFactory.select(blogReview.id).from(blogReview)
                .where(deleteMode(true),
                        blogReview.id.in(blogIdList),
                        blogReview.member.memberId.eq(memberId))
                .fetch();
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

    @Override
    public List<BlogReview> findAllByExhibitionId(Long exhibitionId){
        return queryFactory.selectFrom(blogReview)
                .where(blogReview.exhibition.id.eq(exhibitionId),
                        blogReview.isDeleted.isFalse())
                .fetch();
    }

    @Override
    public BlogListResponseDto findPagedBlogReviews(Long pageNo, String orderType, String word, Long exhibitionId) {
        List<BlogListDto> listDto = queryFactory.select(Projections.fields(BlogListDto.class,
                        blogReview.id,
                        blogReview.title,
                        blogReview.viewDate,
                        blogReview.viewCount,
                        blogReview.member.nickname.as("writer"),
                        Projections.fields(ImageDto.class,
                                blogReview.displayImage.id, blogReview.displayImage.url).as("imageInfo")))
                .from(blogReview)
                .where(titleContain(word),
                        exhibitionMode(exhibitionId),
                        blogReview.isDeleted.isFalse())
                .orderBy(orderSpecifierList(orderType))
                .limit(LIST_BASIC)
                .offset(pageNo*LIST_BASIC)
                .fetch();

        Integer totalCount = findCountAllBlogs(exhibitionId, word);
        pagingDto pagingDto = new pagingDto(pageNo.intValue()+1, listDto.size(),
                totalCount, totalCount/LIST_BASIC.intValue()+1);

        BlogListResponseDto responseDto = new BlogListResponseDto();
        responseDto.setBlogInfo(listDto);
        responseDto.setPageInfo(pagingDto);
        return responseDto;
    }

    @Override
    public BlogListResponseDto findPagedBlogReviewsByMemberId(Long pageNo, Long memberId, boolean deleteMode) {
        List<BlogListDto> listDto = queryFactory.select(Projections.fields(BlogListDto.class,
                blogReview.id,
                blogReview.title,
                blogReview.viewDate,
                blogReview.viewCount,
                Projections.fields(ImageDto.class,
                        blogReview.displayImage.id, blogReview.displayImage.url).as("imageInfo")))
                .from(blogReview)
                .where(blogReview.member.memberId.eq(memberId), deleteMode(deleteMode))
                .orderBy(blogReview.id.desc())
                .limit(LIST_MY_PAGE)
                .offset(pageNo*LIST_MY_PAGE)
                .fetch();

        Integer totalCount = queryFactory.select(blogReview.count()).from(blogReview)
                .where(blogReview.member.memberId.eq(memberId), deleteMode(deleteMode)).fetchOne().intValue();
        pagingDto pagingDto = new pagingDto(pageNo.intValue()+1, listDto.size(),
                totalCount, totalCount/LIST_MY_PAGE.intValue()+1);

        BlogListResponseDto responseDto = new BlogListResponseDto();
        responseDto.setBlogInfo(listDto);
        responseDto.setPageInfo(pagingDto);
        return responseDto;
    }

    private OrderSpecifier[] orderSpecifierList(String orderType) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(orderType == null || orderType.equals("Recent")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, blogReview.id));
        }else if(orderType.equals("ViewCount")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, blogReview.viewCount));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, blogReview.id));
        }else{
            throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private Integer findCountAllBlogs(Long exhibitionId, String word) {
        return queryFactory
                .select(blogReview.count()).from(blogReview)
                .where(exhibitionMode(exhibitionId),
                        titleContain(word),
                        blogReview.isDeleted.isFalse())
                .fetchOne().intValue();
    }

    private BooleanExpression titleContain(String word) {
        return StringUtils.hasText(word) ? blogReview.title.contains(word) : null;
    }

    private BooleanExpression exhibitionMode(Long exhibitionId) {
        return exhibitionId != null ? blogReview.exhibition.id.eq(exhibitionId) : null;
    }

    private BooleanExpression deleteMode(Boolean mode) {
        return mode ? blogReview.isDeleted.isTrue() : blogReview.isDeleted.isFalse();
    }
}