package com.myalley.simpleReview.service;

import com.myalley.exception.CustomException;
import com.myalley.exception.SimpleReviewExceptionType;
import com.myalley.exhibition.domain.Exhibition;
import com.myalley.exhibition.service.ExhibitionService;
import com.myalley.member.domain.Member;
import com.myalley.simpleReview.domain.SimpleReview;
import com.myalley.simpleReview.dto.request.PostSimpleDto;
import com.myalley.simpleReview.dto.request.PutSimpleDto;
import com.myalley.simpleReview.dto.response.SimpleListResponseDto;
import com.myalley.simpleReview.repository.SimpleReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SimpleReviewService {
    private final SimpleReviewRepository simpleRepository;
    private final ExhibitionService exhibitionService;

    public void createSimpleReview(PostSimpleDto simpleReviewDto, Member member){
        Exhibition exhibition = exhibitionService.validateExistExhibition(simpleReviewDto.getExhibitionId());
        SimpleReview newSimpleReview = SimpleReview.builder()
                .viewDate(simpleReviewDto.getViewDate())
                .time(simpleReviewDto.getTime())
                .congestion(simpleReviewDto.getCongestion())
                .rate(simpleReviewDto.getRate())
                .content(simpleReviewDto.getContent())
                .member(member)
                .exhibition(exhibition)
                .build();
        simpleRepository.save(newSimpleReview);
    }

    @Transactional
    public void updateSimpleReview(Long simpleId, PutSimpleDto simpleReviewDto, Member member){
        SimpleReview pre = verifySimpleReview(simpleId, member);
        pre.updateSimpleReview(simpleReviewDto);
        simpleRepository.save(pre);
    }

    @Transactional
    public void removeSimpleReviewByMember(Long simpleId, Member member){
        SimpleReview target = verifySimpleReview(simpleId, member);
        simpleRepository.delete(target);
    }

    @Transactional
    public void removeSimpleReviewByExhibitionId(Long exhibitionId){
        List<SimpleReview> lists= simpleRepository.findAllByExhibitionId(exhibitionId);
        if(!CollectionUtils.isEmpty(lists)){
            simpleRepository.removeSimpleReviewByIdList(lists.stream().map(SimpleReview::getId).collect(Collectors.toList()));
        }
    }

    public SimpleListResponseDto findPagedSimpleReviewsByExhibitionId(Long exhibitionId, Integer pageNo, String orderType){
        Exhibition exhibition = exhibitionService.validateExistExhibition(exhibitionId);
        return simpleRepository.findPagedSimpleReviewsByExhibitionId(setPageNumber(pageNo), orderType, exhibition.getId());
    }

    public SimpleListResponseDto findMySimpleReviews(Member member, Integer pageNo){
        return simpleRepository.findPagedSimpleReviewsByMemberId(setPageNumber(pageNo), member.getMemberId());
    }

    //1. 존재하는지, 작성자 본인인지 확인
    private SimpleReview verifySimpleReview(Long simpleId, Member member) {
        SimpleReview simpleReview = simpleRepository.findById(simpleId).orElseThrow(() -> {
            throw new CustomException(SimpleReviewExceptionType.SIMPLE_NOT_FOUND);
        });
        if(simpleReview.getMember().getMemberId() != member.getMemberId())
            throw new CustomException(SimpleReviewExceptionType.SIMPLE_FORBIDDEN);
        return simpleReview;
    }

    //2. 목록 조회 시, pageNumber 세팅
    private Long setPageNumber(Integer pageNo){
        if(pageNo == null)
            return 0L;
        else if(pageNo >= 1)
            return pageNo.longValue()-1;
        else //페이지 값으로 허용되는 최솟값은 1
            throw new CustomException(SimpleReviewExceptionType.SIMPLE_BAD_REQUEST);
    }
}
