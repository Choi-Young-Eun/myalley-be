package com.myalley.blogReview.service;

import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.request.BlogRequestDto;
import com.myalley.blogReview.dto.response.BlogDetailResponseDto;
import com.myalley.blogReview.dto.response.BlogListResponseDto;
import com.myalley.exhibition.service.ExhibitionService;
import com.myalley.member.domain.Member;

import com.myalley.blogReview.repository.BlogReviewRepository;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import lombok.RequiredArgsConstructor;
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
        blogReview.setDisplayImage(blogImageService.uploadFileList(images, newBlog));
    }

    public BlogListResponseDto findPagedBlogReviews(Integer pageNo, String orderType, String word) {
        return blogReviewRepository.findPagedBlogReviews(setPageNumber(pageNo), orderType, word, null);
    }

    public BlogListResponseDto findPagedBlogReviewsByExhibitionId(Long exhibitionId, Integer pageNo, String orderType) {
        if(exhibitionId == null)
            throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);
        return blogReviewRepository.findPagedBlogReviews(setPageNumber(pageNo), orderType, null, exhibitionId);
    }

    public BlogListResponseDto findMyBlogReviews(Member member, Integer pageNo, boolean deleteMode) {
        return blogReviewRepository.findPagedBlogReviewsByMemberId(setPageNumber(pageNo),member.getMemberId(),deleteMode);
    }

    @Transactional
    public BlogDetailResponseDto findBlogReviewByBlogId(Long blogId, Long memberId){
        if(memberId == null)
            memberId = 0L;
        BlogDetailResponseDto blogDetailDto = blogReviewRepository.findDetailedByBlogId(blogId, memberId);
        blogDetailDto.setImageInfo(blogImageService.findAllBlogImagesByBlogReviewId(blogDetailDto.getId()));
        blogReviewRepository.updateBlogViewCount(blogDetailDto.getId(), blogDetailDto.getViewCount());
        return blogDetailDto;
    }

    @Transactional
    public void updateBlogReview(BlogRequestDto blogRequestDto, Long blogId, Member member) {
        BlogReview preBlogReview = verifyRequester(blogId,member.getMemberId());
        preBlogReview.updateReview(blogRequestDto);
    }

    @Transactional
    public void removeBlogReview(Long blogId){
        bookmarkService.removeBlogBookmarksByBlogReview(blogId);
        likesService.removeBlogLikesByBlogReview(blogId);
        blogReviewRepository.updateBlogStatus(blogId);
    }

    @Transactional
    public void removeBlogReviewsPermanently(List<Long> blogId, Member member) {
        List<Long> blogIdList = blogReviewRepository.findRemovedByIdList(member.getMemberId(), blogId);
        if(CollectionUtils.isEmpty(blogIdList) || blogIdList.size() != blogId.size())
            throw new CustomException(BlogReviewExceptionType.BLOG_NOT_FOUND);
        blogImageService.removeBlogImagesByBlogReviewList(blogIdList);
        blogReviewRepository.deleteListPermanently(blogIdList);
    }

    public void removeBlogReviewByMember(Long blogId, Member member){
        BlogReview pre = verifyRequester(blogId,member.getMemberId());
        if(pre.getIsDeleted() == Boolean.FALSE)
            removeBlogReview(pre.getId());
        else throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);
    }

    public void removeBlogReviewByExhibitionId(Long exhibitionId){
        List<BlogReview> lists = blogReviewRepository.findAllByExhibitionId(exhibitionId);
        if(!CollectionUtils.isEmpty(lists)) {
            for(BlogReview br : lists){
                removeBlogReview(br.getId());
            }
        }
    }

    //1. 존재하는 글인지 확인
    public BlogReview validateBlogReview(Long blogId){
        BlogReview blog = blogReviewRepository.findById(blogId).orElseThrow(() -> {
            throw new CustomException(BlogReviewExceptionType.BLOG_NOT_FOUND);
        });
        if(blog.getIsDeleted())
            throw new CustomException(BlogReviewExceptionType.BLOG_BAD_REQUEST);
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
    
    //3. 블로그 목록 조회 요청 시 pageNumber 세팅
    private Long setPageNumber(Integer pageNo){
        if(pageNo != null)
            return pageNo.longValue()-1;
        else
            return 0L;
    }
}
