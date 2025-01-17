package com.example.football.dto;

public class ChatLogDTO {
    private Long id;
    private String user; // 사용자 닉네임
	private String content; // 채팅 내용
    private String badge; // 사용자 훈장

    // 생성자
    public ChatLogDTO(Long id, String user, String content, String badge) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.badge = badge;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUser(String user) {
		this.user = user;
	}

    
    public String getUser() {
		return user;
	}
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}