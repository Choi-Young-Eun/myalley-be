package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.myalley.blogReview.domain.QBlogReview.blogReview;

@Repository
@RequiredArgsConstructor
public class BlogReviewRepositoryCustomImpl implements BlogReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public void updateBlogStatus(Long blogId) {
        queryFactory.update(blogReview)
                .where(blogReview.id.eq(blogId))
                .set(blogReview.isDeleted,true)
                .set(blogReview.likeCount,0)
                .set(blogReview.bookmarkCount,0)
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
}
