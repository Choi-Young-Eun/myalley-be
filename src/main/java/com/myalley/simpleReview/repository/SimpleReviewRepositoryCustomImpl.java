package com.myalley.simpleReview.repository;

import com.myalley.common.dto.pagingDto;
import com.myalley.exception.CustomException;
import com.myalley.exception.SimpleReviewExceptionType;
import com.myalley.exhibition.dto.response.ExhibitionSimpleReviewDto;
import com.myalley.member.dto.MemberBlogDto;
import com.myalley.simpleReview.dto.response.SimpleListDto;
import com.myalley.simpleReview.dto.response.SimpleListResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.myalley.exhibition.domain.QExhibition.exhibition;
import static com.myalley.member.domain.QMember.member;
import static com.myalley.simpleReview.domain.QSimpleReview.simpleReview;

@Repository
@RequiredArgsConstructor
public class SimpleReviewRepositoryCustomImpl implements SimpleReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    private Long LIST_BASIC = 10L;
    private Long LIST_MY_PAGE = 5L;

    @Override
    public SimpleListResponseDto findPagedSimpleReviewsByExhibitionId(Long pageNo, String orderType, Long exhibitionId) {
        List<SimpleListDto> listDto = queryFactory.select(Projections.fields(SimpleListDto.class,
                        simpleReview.id, simpleReview.viewDate, simpleReview.rate, simpleReview.content,
                        simpleReview.time, simpleReview.congestion,
                        Projections.fields(MemberBlogDto.class, member.memberId, member.nickname, member.memberImage)
                                .as("memberInfo")))
                .from(simpleReview)
                .innerJoin(simpleReview.member, member)
                .where(simpleReview.exhibition.id.eq(exhibitionId))
                .orderBy(orderSpecifierList(orderType))
                .limit(LIST_BASIC)
                .offset(pageNo*LIST_BASIC)
                .fetch();

        Integer totalCount = queryFactory.select(simpleReview.count()).from(simpleReview)
                .where(simpleReview.exhibition.id.eq(exhibitionId)).fetchOne().intValue();
        Integer totalPage;
        if(totalCount % LIST_BASIC.intValue() == 0)
            totalPage = totalCount/LIST_BASIC.intValue();
        else
            totalPage = totalCount/LIST_BASIC.intValue()+1;
        pagingDto pageInfo = new pagingDto(pageNo.intValue()+1, listDto.size(), totalCount, totalPage);

        SimpleListResponseDto dto = new SimpleListResponseDto(listDto, pageInfo);
        return dto;
    }

    @Override
    public SimpleListResponseDto findPagedSimpleReviewsByMemberId(Long pageNo, Long memberId) {
        List<SimpleListDto> listDto = queryFactory.select(Projections.fields(SimpleListDto.class,
                        simpleReview.id, simpleReview.viewDate, simpleReview.rate, simpleReview.content,
                        simpleReview.time, simpleReview.congestion,
                        Projections.fields(ExhibitionSimpleReviewDto.class, exhibition.id, exhibition.title)
                                .as("exhibitionInfo")))
                .from(simpleReview)
                .innerJoin(simpleReview.exhibition, exhibition)
                .where(simpleReview.member.memberId.eq(memberId))
                .orderBy(simpleReview.id.desc())
                .limit(LIST_MY_PAGE)
                .offset(pageNo*LIST_MY_PAGE)
                .fetch();

        Integer totalCount = queryFactory.select(simpleReview.count()).from(simpleReview)
                .where(simpleReview.member.memberId.eq(memberId)).fetchOne().intValue();
        Integer totalPage;
        if(totalCount % LIST_MY_PAGE.intValue() == 0)
            totalPage = totalCount/LIST_MY_PAGE.intValue();
        else
            totalPage = totalCount/LIST_MY_PAGE.intValue()+1;
        pagingDto pageInfo = new pagingDto(pageNo.intValue()+1, listDto.size(), totalCount, totalPage);

        SimpleListResponseDto dto = new SimpleListResponseDto(listDto, pageInfo);
        return dto;
    }

    private OrderSpecifier[] orderSpecifierList(String orderType){
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(orderType == null || orderType.equals("Recent")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, simpleReview.id));
        }else if(orderType.equals("StarScore")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, simpleReview.rate));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, simpleReview.id));
        }else
            throw new CustomException(SimpleReviewExceptionType.SIMPLE_BAD_REQUEST);

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
