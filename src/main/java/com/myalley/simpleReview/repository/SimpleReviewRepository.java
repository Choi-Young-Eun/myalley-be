package com.myalley.simpleReview.repository;

import com.myalley.simpleReview.domain.SimpleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SimpleReviewRepository extends JpaRepository<SimpleReview, Long>, SimpleReviewRepositoryCustom{
    @Query(value="select * from simple_review br where br.exhibition_id=?1",nativeQuery = true)
    List<SimpleReview> findAllByExhibitionId(Long exhibitionId);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value="delete from simple_review sr where sr.simple_id in ?1",nativeQuery = true)
    void removeSimpleReviewByIdList(List<Long> simpleIdList);
}
