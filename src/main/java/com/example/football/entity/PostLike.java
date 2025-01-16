package com.example.football.entity;

import jakarta.persistence.*;

@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // 어떤 사용자인지 식별

    @ManyToOne
    @JoinColumn(name = "post_id")
    private CommunityPost post;  // 어떤 게시글을 좋아요했는지

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CommunityPost getPost() {
        return post;
    }

    public void setPost(CommunityPost post) {
        this.post = post;
    }
}
