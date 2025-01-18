package com.example.football.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // 사용자 ID

    @ManyToOne
    @JoinColumn(name = "post_id")
    private CommunityPost post;  // 게시글

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
