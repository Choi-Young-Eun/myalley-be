package com.myalley.blogReview.repository;

import com.myalley.blogReview.domain.BlogLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogLikesRepository extends JpaRepository<BlogLikes, Long>, BlogLikesRepositoryCustom {

}
