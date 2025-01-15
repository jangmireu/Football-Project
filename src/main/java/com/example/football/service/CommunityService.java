package com.example.football.service;

import com.example.football.entity.CommunityPost;
import com.example.football.entity.Reply;
import com.example.football.entity.ReplyLike;
import com.example.football.repository.CommunityRepository;
import com.example.football.repository.ReplyLikeRepository;
import com.example.football.repository.ReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;

    // 생성자 주입
    @Autowired
    public CommunityService(CommunityRepository communityRepository,
                            ReplyRepository replyRepository,
                            ReplyLikeRepository replyLikeRepository) {
        this.communityRepository = communityRepository;
        this.replyRepository = replyRepository;
        this.replyLikeRepository = replyLikeRepository;
    }

    /**
     * 게시글 목록을 가져오는 메서드
     *
     * @param page  페이지 번호
     * @param size  페이지 크기
     * @param sort  정렬 기준 (likes, views, createdAt 등)
     * @return 정렬된 게시글 페이지
     */
    public Page<CommunityPost> getPosts(int page, int size, String sort) {
        Sort sortOption = switch (sort) {
            case "likes" -> Sort.by(Sort.Direction.DESC, "likes");
            case "views" -> Sort.by(Sort.Direction.DESC, "views");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬은 생성일 기준
        };

        Pageable pageable = PageRequest.of(page, size, sortOption);
        return communityRepository.findAll(pageable);
    }

    /**
     * 특정 답글의 좋아요 수를 증가 또는 취소하는 메서드
     *
     * @param replyId 답글 ID
     * @param userId  사용자 ID
     * @return true (좋아요 추가됨), false (좋아요 취소됨)
     */
    @Transactional
    public boolean toggleReplyLike(Long replyId, Long userId) {
        // 사용자가 이미 해당 답글에 좋아요를 눌렀는지 확인
        if (replyLikeRepository.existsByReplyIdAndUserId(replyId, userId)) {
            // 좋아요 취소 로직
            replyLikeRepository.deleteByReplyIdAndUserId(replyId, userId);
            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));
            reply.setLikes(reply.getLikes() - 1); // 좋아요 감소
            replyRepository.save(reply);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가 로직
            ReplyLike replyLike = new ReplyLike();
            replyLike.setReply(replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다.")));
            replyLike.setUserId(userId); // 사용자 ID 설정
            replyLikeRepository.save(replyLike);

            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));
            reply.setLikes(reply.getLikes() + 1); // 좋아요 증가
            replyRepository.save(reply);
            return true; // 좋아요 추가됨
        }
    }

    /**
     * 특정 답글의 좋아요 수를 1 증가시키는 메서드
     *
     * @param replyId 좋아요를 증가시킬 답글의 ID
     */
    @Transactional
    public void increaseReplyLikeCount(Long replyId) {
        // 1) DB에서 해당 reply 조회
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("답글을 찾을 수 없습니다."));

        // 2) 좋아요 수 증가
        reply.setLikes(reply.getLikes() + 1);

        // 3) DB 저장
        replyRepository.save(reply);
    }
}
