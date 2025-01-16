package com.example.football.repository;

import com.example.football.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    // 특정 게시글의 답글 목록을 가져오는 메서드
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId")
    List<Reply> findByPostId(@Param("postId") Long postId);

    // 좋아요순 정렬
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId ORDER BY r.likes DESC")
    List<Reply> findByPostIdOrderByLikesDesc(@Param("postId") Long postId);

    // 최신순 정렬
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId ORDER BY r.createdAt DESC")
    List<Reply> findByPostIdOrderByCreatedAtDesc(@Param("postId") Long postId);

    // 오래된순 정렬
    @Query("SELECT r FROM Reply r WHERE r.post.id = :postId ORDER BY r.createdAt ASC")
    List<Reply> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);

    // 특정 게시글의 댓글 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Reply r WHERE r.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
