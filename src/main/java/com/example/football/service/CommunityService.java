package com.example.football.service;

import com.example.football.entity.CommunityPost;
import com.example.football.entity.Reply;
import com.example.football.repository.CommunityRepository;
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

    // 생성자 주입
    @Autowired
    public CommunityService(CommunityRepository communityRepository,
                            ReplyRepository replyRepository) {
        this.communityRepository = communityRepository;
        this.replyRepository = replyRepository;
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
     * 특정 답글의 좋아요 수를 1 증가시키는 메서드
     * (ReplyService 역할)
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
