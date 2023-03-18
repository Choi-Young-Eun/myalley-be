package com.myalley.exhibition.service;

import com.myalley.exception.CustomException;
import com.myalley.exception.ExhibitionExceptionType;
import com.myalley.exhibition.domain.Exhibition;
import com.myalley.exhibition.domain.ExhibitionBookmark;
import com.myalley.exhibition.dto.response.BookmarkResponseDto;
import com.myalley.exhibition.repository.ExhibitionBookmarkRepository;
import com.myalley.member.domain.Member;
import com.myalley.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class ExhibitionBookmarkService {

    private final ExhibitionService exhibitionService;
    private final ExhibitionBookmarkRepository bookmarkRepository;
    private final MemberService memberService;

    public BookmarkResponseDto createBookmark(Long memberId, Long exhibitionId) {
        Member member = memberService.validateMember(memberId);
        Exhibition exhibition = exhibitionService.validateExistExhibition(exhibitionId);

         Optional<ExhibitionBookmark> bookmark = bookmarkRepository.findByExhibitionAndMember(exhibition, member);

        if (bookmark.isPresent()) {
            removeBookmark(bookmark.get().getId());
            exhibitionService.bookmarkCountDown(exhibitionId);

            return new BookmarkResponseDto("전시글 북마크 목록에서 삭제되었습니다.", false);
        }

        ExhibitionBookmark exBookmark = ExhibitionBookmark.builder()
                .member(member)
                .exhibition(exhibition)
                .build();

        bookmarkRepository.save(exBookmark);
        exhibitionService.bookmarkCountUp(exhibitionId);

        return new BookmarkResponseDto("전시글 북마크 목록에 추가되었습니다.", true);
    }

    private void removeBookmark(Long bookmarkId) {
        validateBookmark(bookmarkId);
        bookmarkRepository.deleteById(bookmarkId);
    }

    //북마크 추가한 전시글 목록 페이징 조회하기
    public Page<ExhibitionBookmark> findBookmarksByMemberId(Long memberId, int page) {
        PageRequest pageRequest = PageRequest.of(page -1, 8, Sort.by("id").descending());
        Member member = memberService.validateMember(memberId);
        return bookmarkRepository.findAllByMember(member, pageRequest);
    }

    //북마크글 존재여부 확인
    public void validateBookmark(Long bookmarkId) {
        bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new CustomException(ExhibitionExceptionType.EXHIBITION_BOOKMARK_NOT_FOUND));
    }
}
