package com.example.football.repository;

import com.example.football.entity.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    boolean existsByReplyIdAndUserId(Long replyId, Long userId);

    void deleteByReplyIdAndUserId(Long replyId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReplyLike rl WHERE rl.reply.id = :replyId")
    void deleteByReplyId(Long replyId);
}
