package com.example.football.repository;

import com.example.football.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {
    // 페이지네이션 지원 메서드
    Page<CommunityPost> findAll(Pageable pageable);
}
