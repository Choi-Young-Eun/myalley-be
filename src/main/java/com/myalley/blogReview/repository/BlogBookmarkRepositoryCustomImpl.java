package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogBookmark;
import com.myalley.blogReview.dto.response.BlogListDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.blogReview.dto.response.ImageDto;
import com.myalley.common.dto.pagingDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.myalley.blogReview.domain.QBlogBookmark.blogBookmark;

@Repository
@RequiredArgsConstructor
public class BlogBookmarkRepositoryCustomImpl implements BlogBookmarkRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final Long LIST_MY_PAGE = 6L;

    @Override
    public BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId) {
        List<BlogListDto> listDto = queryFactory.select(Projections.fields(BlogListDto.class,
                        blogBookmark.blog.id,
                        blogBookmark.blog.title,
                        blogBookmark.blog.viewDate,
                        blogBookmark.blog.viewCount,
                        blogBookmark.blog.member.nickname.as("writer"),
                        Projections.fields(ImageDto.class,
                                blogBookmark.blog.displayImage.id,  blogBookmark.blog.displayImage.url).as("imageInfo")))
                .from(blogBookmark)
                .where(blogBookmark.member.memberId.eq(memberId),
                        blogBookmark.isDeleted.isFalse())
                .orderBy(blogBookmark.updatedAt.desc())
                .limit(LIST_MY_PAGE)
                .offset(pageNo*LIST_MY_PAGE)
                .fetch();

        Integer totalCount = queryFactory.select(blogBookmark.count()).from(blogBookmark)
                .where(blogBookmark.member.memberId.eq(memberId))
                .fetchOne().intValue();
        pagingDto pagingDto = new pagingDto(pageNo.intValue()+1, listDto.size(),
                totalCount, totalCount/LIST_MY_PAGE.intValue()+1);

        BlogListResponseDto responseDto = new BlogListResponseDto();
        responseDto.setBlogInfo(listDto);
        responseDto.setPageInfo(pagingDto);
        return responseDto;
    }

    @Override
    public BlogBookmark findBookmarkLogByMemberIdAndBlogId(Long memberId, Long blogId) {
        return queryFactory.selectFrom(blogBookmark)
                .where(blogBookmark.member.memberId.eq(memberId), blogBookmark.blog.id.eq(blogId))
                .fetchOne();
    }
}
