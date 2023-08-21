package com.myalley.blogReview.repository;

import com.myalley.blogReview.dto.response.ImageDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.myalley.blogReview.domain.QBlogImage.blogImage;

@Repository
@RequiredArgsConstructor
public class BlogImageRepositoryCustomImpl implements BlogImageRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ImageDto> findAllByBlogReviewId(Long blogId) {
        List<ImageDto> ImageDtos = queryFactory.select(Projections.fields(ImageDto.class, blogImage.id, blogImage.url))
                .from(blogImage)
                .where(blogImage.blog.id.eq(blogId))
                .fetch();
        return ImageDtos;
    }
}
