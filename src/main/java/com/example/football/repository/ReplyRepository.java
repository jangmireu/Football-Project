package com.example.football.repository;

import com.example.football.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 특정 게시글의 답글 목록을 가져오는 메서드
    List<Reply> findByPostId(Long postId);
}
