package com.example.football.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.football.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostId(Long postId);//게시글 ID로 답글 조회
}
