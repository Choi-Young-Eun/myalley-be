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

import javax.transaction.Transactional;
import java.util.List;

import static com.myalley.blogReview.domain.QBlogBookmark.blogBookmark;
import static com.myalley.blogReview.domain.QBlogImage.blogImage;
import static com.myalley.blogReview.domain.QBlogReview.blogReview;
import static com.myalley.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class BlogBookmarkRepositoryCustomImpl implements BlogBookmarkRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public BlogListResponseDto findAllByMemberId(Long pageNo, Long memberId) {
        final Long LIST_MY_PAGE = 6L;

        List<BlogListDto> listDto = queryFactory.select(Projections.fields(BlogListDto.class,
                        blogReview.id,
                        blogReview.title,
                        blogReview.viewDate,
                        blogReview.viewCount,
                        member.nickname.as("writer"),
                        Projections.fields(ImageDto.class,
                                blogImage.id,  blogImage.url).as("imageInfo")))
                .from(blogBookmark)
                .innerJoin(blogBookmark.blog, blogReview)
                .innerJoin(blogReview.member, member)
                .innerJoin(blogReview.displayImage, blogImage)
                .where(blogBookmark.member.memberId.eq(memberId),
                        blogBookmark.isDeleted.isFalse())
                .orderBy(blogBookmark.updatedAt.desc())
                .limit(LIST_MY_PAGE)
                .offset(pageNo*LIST_MY_PAGE)
                .fetch();

        if(listDto.isEmpty())
            return new BlogListResponseDto(listDto, new pagingDto(pageNo.intValue()+1, 0, 0, 0));

        int totalCount = queryFactory.select(blogBookmark.count()).from(blogBookmark)
                .where(blogBookmark.member.memberId.eq(memberId))
                .fetchOne().intValue();
        int totalPage;
        if(totalCount % LIST_MY_PAGE.intValue() == 0)
            totalPage = totalCount/LIST_MY_PAGE.intValue();
        else
            totalPage = totalCount/LIST_MY_PAGE.intValue()+1;
        pagingDto pageInfo = new pagingDto(pageNo.intValue()+1, listDto.size(), totalCount, totalPage);

        return new BlogListResponseDto(listDto, pageInfo);
    }

    @Override
    public BlogBookmark findBookmarkLogByMemberIdAndBlogId(Long memberId, Long blogId) {
        return queryFactory.selectFrom(blogBookmark)
                .where(blogBookmark.member.memberId.eq(memberId), blogBookmark.blog.id.eq(blogId))
                .fetchOne();
    }

    @Transactional
    @Override
    public void deleteAllByBlogId(Long blogId) {
        queryFactory.delete(blogBookmark)
                .where(blogBookmark.blog.id.eq(blogId))
                .execute();
    }
}
