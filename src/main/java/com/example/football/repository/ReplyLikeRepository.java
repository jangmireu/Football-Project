package com.example.football.repository;

import com.example.football.entity.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    boolean existsByReplyIdAndUserId(Long replyId, Long userId);
    void deleteByReplyIdAndUserId(Long replyId, Long userId);
}
