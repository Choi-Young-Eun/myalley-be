package com.myalley.blogReview.service;

import com.myalley.blogReview.domain.BlogImage;
import com.myalley.blogReview.domain.BlogReview;
import com.myalley.blogReview.dto.response.ImageDto;
import com.myalley.blogReview.repository.BlogImageRepository;
import com.myalley.exception.BlogReviewExceptionType;
import com.myalley.exception.CustomException;
import com.myalley.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogImageService {
    private final BlogImageRepository blogImageRepository;
    private final S3Service s3Service;
    private final long BASIC_IMAGE_ID=1;

    public BlogImage uploadFileList(List<MultipartFile> images, BlogReview blogReview) throws IOException {
        if (images != null) {
            for (MultipartFile image : images) {
                addBlogImage(image, blogReview);
            }
            return blogImageRepository.findOneByBlogReviewId(blogReview.getId());
        }
        return blogImageRepository.findById(BASIC_IMAGE_ID).get();
    }

    public void uploadFile(BlogReview blogReview, Member member, MultipartFile image) throws IOException {
        if(blogImageRepository.countByBlog(blogReview) >= 3)
            throw new CustomException(BlogReviewExceptionType.IMAGE_BAD_REQUEST_OVER);
        if(blogReview.getMember().getMemberId()!= member.getMemberId())
            throw new CustomException(BlogReviewExceptionType.IMAGE_FORBIDDEN);
        if(image.isEmpty())
            throw new CustomException(BlogReviewExceptionType.IMAGE_BAD_REQUEST_EMPTY);
        addBlogImage(image, blogReview);
    }

    public void addBlogImage(MultipartFile image, BlogReview blogReview) throws IOException {
        String[] information = s3Service.uploadBlogImage(image);
        BlogImage newImage=BlogImage.builder()
                .fileName(information[0])
                .url(information[1])
                .build();
        newImage.setBlog(blogReview);
        blogImageRepository.save(newImage);
    }

    public void removeBlogImage(BlogReview blogReview, Member member, Long imageId){
        if(blogReview.getMember().getMemberId()!= member.getMemberId())
            throw new CustomException(BlogReviewExceptionType.IMAGE_FORBIDDEN);
        BlogImage foundImage = validateBlogImage(blogReview,imageId);
        s3Service.deleteBlogImage(foundImage.getFileName());
        blogImageRepository.delete(foundImage);
    }

    public void removeBlogImagesByBlogReviewList(List<Long> targetList) {
        List<BlogImage> blogImageList = blogImageRepository.findAllByBlogReviewIdList(targetList);
        if (!CollectionUtils.isEmpty(blogImageList)) {
            for (BlogImage blogImage : blogImageList) {
                blogImageRepository.delete(blogImage);
                s3Service.deleteBlogImage(blogImage.getFileName());
            }
        }
    }

    public List<ImageDto> findAllBlogImagesByBlogReviewId(Long blogId){
        return blogImageRepository.findAllByBlogReviewId(blogId);
    }

    private BlogImage validateBlogImage(BlogReview blogReview, Long imageId){
        BlogImage image = blogImageRepository.findByIdAndBlog(imageId,blogReview).orElseThrow(() -> {
            throw new CustomException(BlogReviewExceptionType.IMAGE_NOT_FOUND);
        });
        return image;
    }
}
