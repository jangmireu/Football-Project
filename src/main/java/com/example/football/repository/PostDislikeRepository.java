package com.example.football.repository;

import com.example.football.entity.PostDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostDislikeRepository extends JpaRepository<PostDislike, Long> {

    // 특정 게시글과 사용자의 싫어요 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시글과 사용자의 싫어요 데이터 삭제
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시글의 모든 싫어요 데이터 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM PostDislike pd WHERE pd.post.id = :postId")
    void deleteByPostId(Long postId);
}
