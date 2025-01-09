package com.example.football.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<CommunityPost> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityRepository.findAll(pageable);
    }
}
