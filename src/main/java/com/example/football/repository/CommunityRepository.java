package com.example.football.repository;

import com.example.football.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {

    /**
     * 페이징과 정렬을 지원하는 기본 메서드
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 게시글 페이지
     */
    @Override
    Page<CommunityPost> findAll(Pageable pageable);
}
