package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogLikes;
import com.myalley.blogReview.dto.response.BlogListDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.blogReview.dto.response.ImageDto;
import com.myalley.common.dto.pagingDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.myalley.blogReview.domain.QBlogLikes.blogLikes;

@Repository
@RequiredArgsConstructor
public class BlogLikesRepositoryCustomImpl implements BlogLikesRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final Long LIST_MY_PAGE = 6L;

    @Override
    public BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId) {
        List<BlogListDto> listDto2 = queryFactory.select(Projections.fields(BlogListDto.class,
                        blogLikes.blog.id,
                        blogLikes.blog.title,
                        blogLikes.blog.viewDate,
                        blogLikes.blog.viewCount,
                        blogLikes.blog.member.nickname.as("writer"),
                        Projections.fields(ImageDto.class,
                                blogLikes.blog.displayImage.id,  blogLikes.blog.displayImage.url).as("imageInfo")))
                .from(blogLikes)
                .where(blogLikes.member.memberId.eq(memberId),
                        blogLikes.isDeleted.isFalse())
                .orderBy(blogLikes.updatedAt.desc())
                .limit(LIST_MY_PAGE)
                .offset(pageNo*LIST_MY_PAGE)
                .fetch();

        Integer totalCount = queryFactory.select(blogLikes.count()).from(blogLikes)
                .where(blogLikes.member.memberId.eq(memberId), blogLikes.isDeleted.isFalse())
                .fetchOne().intValue();
        pagingDto pagingDto = new pagingDto(pageNo.intValue()+1, listDto2.size(),
                    totalCount, totalCount/LIST_MY_PAGE.intValue()+1);

        BlogListResponseDto responseDto = new BlogListResponseDto();
        responseDto.setBlogInfo(listDto2);
        responseDto.setPageInfo(pagingDto);
        return responseDto;
    }

    @Override
    public BlogLikes findLikesLogByMemberIdAndBlogId(Long memberId, Long blogId) {
        return queryFactory.selectFrom(blogLikes)
                .where(blogLikes.member.memberId.eq(memberId), blogLikes.blog.id.eq(blogId))
                .fetchOne();
    }

    @Transactional
    @Override
    public void deleteAllByBlogId(Long blogId) {
        queryFactory.delete(blogLikes)
                .where(blogLikes.blog.id.eq(blogId))
                .execute();
    }
}
