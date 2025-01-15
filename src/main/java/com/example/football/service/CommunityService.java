package com.example.football.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.football.entity.CommunityPost;
import com.example.football.repository.CommunityRepository;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Autowired
    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    /**
     * 게시글 목록을 가져오는 메서드
     *
     * @param page  페이지 번호
     * @param size  페이지 크기
     * @param sort  정렬 기준 (likes, views, replies)
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
}
