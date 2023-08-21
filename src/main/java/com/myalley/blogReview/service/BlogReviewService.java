package com.myalley.blogReview.service;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.request.BlogRequestDto;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.exhibition.domain.Exhibition;
import com.myalley.exhibition.service.ExhibitionService;
import com.myalley.member.domain.Member;

import com.myalley.blogReview.repository.BlogReviewRepository;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogReviewService {
    private final BlogReviewRepository blogReviewRepository;
    private final ExhibitionService exhibitionService;
    private final BlogImageService blogImageService;
    private final BlogBookmarkService bookmarkService;
    private final BlogLikesService likesService;

    public static final String BASIC_LIST = "basic";
    public static final String SELF_LIST = "self";

    @Transactional
    public void createBlog(BlogRequestDto blogRequestDto, Member member, Long exhibitionId,
                           List<MultipartFile> images) throws IOException {
        if(images != null && images.size()>3)
            throw new CustomException(BlogReviewExceptionType.IMAGE_BAD_REQUEST_OVER);
        BlogReview blogReview = BlogReview.builder()
                .title(blogRequestDto.getTitle())
                .content(blogRequestDto.getContent())
                .viewDate(LocalDate.parse(blogRequestDto.getViewDate()))
                .time(blogRequestDto.getTime())
                .congestion(blogRequestDto.getCongestion())
                .transportation(blogRequestDto.getTransportation())
                .revisit(blogRequestDto.getRevisit())
                .member(member)
                .exhibition(exhibitionService.validateExistExhibition(exhibitionId)).build();
        BlogReview newBlog = blogReviewRepository.save(blogReview);
        //0620) 아래에서 만약 이미지가 없다면 null을 반환, 있어서 엔티티로 다 만들었다면 제일 첫번째로 만든 이미지를 반환하여 Blog의 대표 이미지 컬럼에 등록
        blogImageService.uploadFileList(images, newBlog);

    }

    @Transactional
    public BlogDetailResponseDto findBlogReviewByBlogId(Long blogId, Long memberId){
        BlogDetailResponseDto blogDetailDto = blogReviewRepository.findDetailedByBlogId(blogId, memberId);
        blogDetailDto.setImageInfo(blogImageService.findAllBlogImagesByBlogReviewId(blogDetailDto.getId()));
        blogReviewRepository.updateBlogViewCount(blogDetailDto.getId(), blogDetailDto.getViewCount());
        return blogDetailDto;
    }

    public BlogListResponseDto findPagedBlogReviews(Integer pageNo, String orderType, String word){
        PageRequest pageRequest;
        Page<BlogReview> blogReviewList;
        if(pageNo == null)
            pageNo = 0;
        else
            pageNo--;
        if(orderType!=null && orderType.equals("ViewCount")) {
            pageRequest = PageRequest.of(pageNo, 9, Sort.by("viewCount").descending()
                    .and(Sort.by("id").descending()));
        } else if(orderType==null || orderType.equals("Recent")){
            pageRequest = PageRequest.of(pageNo, 9, Sort.by("id").descending());
        } else{
            throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);
        }
        if(word != null)
            blogReviewList = blogReviewRepository.findAllByTitleContaining(word,pageRequest);
        else
            blogReviewList = blogReviewRepository.findAll(pageRequest);
        return BlogListResponseDto.blogOf(blogReviewList,BASIC_LIST);
    }

    public BlogListResponseDto findMyBlogReviews(Member member, Integer pageNo) {
        PageRequest pageRequest;
        if(pageNo == null)
            pageRequest = PageRequest.of(0, 6, Sort.by("id").descending());
        else
            pageRequest = PageRequest.of(pageNo-1, 6, Sort.by("id").descending());
        Page<BlogReview> myBlogReviewList = blogReviewRepository.findAllByMember(member,pageRequest);
        return BlogListResponseDto.blogOf(myBlogReviewList,SELF_LIST);
    }

    //@Where 삭제하고 일단 임시로 IsDeleted 조건 추가한 메서드 사용하는 걸로 변경함!
    //고민할 부분 : QueryDSL을 적용하면 PageRequest 생성하는 부분을 없앨 수 있을 것 같은데 흠
    //-> 고민하는 이유 : 너무 지저분해 보여서 ㅠ.ㅠ 간결한 코드, 클린 코드가 뭘까? - 뭐가 더 나은 건지 잘 모르겠음
    //-> 변경하는 경우 예상 시나리오 : pageNo이랑 orderType을 인자로 넘기고 QueryDSL 이용해서 조건을 다르게 주는 걸로 할까?
    public BlogListResponseDto findPagedBlogReviewsByExhibitionId(Long exhibitionId, Integer pageNo, String orderType) {
        PageRequest pageRequest;
        Exhibition exhibition = exhibitionService.validateExistExhibition(exhibitionId);
        if(pageNo == null)
            pageNo = 0;
        else
            pageNo--;
        if(orderType!=null && orderType.equals("ViewCount"))
            pageRequest = PageRequest.of(pageNo, 9, Sort.by("viewCount").descending()
                    .and(Sort.by("id")).descending());
        else if(orderType == null || orderType.equals("Recent"))
            pageRequest = PageRequest.of(pageNo, 9, Sort.by("id").descending());
        else
            throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);

        return BlogListResponseDto.blogOf(blogReviewRepository.findAllByExhibition(exhibition,pageRequest),BASIC_LIST);
        //JPA 수정 후. 앞으로 적용할 버전
        //-> return BlogListResponseDto.blogOf(blogReviewRepository.findAllByExhibitionAndIsDeleted(exhibition, Boolean.FALSE, pageRequest),BASIC_LIST);
    }

    @Transactional
    public void updateBlogReview(BlogRequestDto blogRequestDto, Long blogId, Member member) {
        BlogReview preBlogReview = verifyRequester(blogId,member.getMemberId());
        preBlogReview.updateReview(blogRequestDto);
    }

    @Transactional
    public void removeBlogReview(BlogReview pre){
        bookmarkService.removeBlogBookmarksByBlogReview(pre);
        likesService.removeBlogLikesByBlogReview(pre);
        blogReviewRepository.updateBlogStatus(pre.getId());
    }

    public void removeBlogReviewByMember(Long blogId, Member member){
        BlogReview pre = verifyRequester(blogId,member.getMemberId());
        removeBlogReview(pre);
    }

    public void removeBlogReviewByExhibitionId(Long exhibitionId){
        List<BlogReview> lists = blogReviewRepository.findAllByExhibitionId(exhibitionId);
        if(!CollectionUtils.isEmpty(lists)) {
            for(BlogReview br : lists){
                removeBlogReview(br);
            }
        }
    }

    //1. 존재하는 글인지 확인
    public BlogReview validateBlogReview(Long blogId){
        BlogReview blog = blogReviewRepository.findById(blogId).orElseThrow(() -> {
            throw new CustomException(BlogReviewExceptionType.BLOG_NOT_FOUND);
        });
        return blog;
    }
    
    //2. 작성자인지 확인
    private BlogReview verifyRequester(Long blogId,Long memberId){
        BlogReview review = blogReviewRepository.findById(blogId).orElseThrow(() -> {
           throw new CustomException(BlogReviewExceptionType.BLOG_NOT_FOUND);
        });
        if(!review.getMember().getMemberId().equals(memberId)){
            throw new CustomException(BlogReviewExceptionType.BLOG_FORBIDDEN);
        }
        return review;
    }
}
