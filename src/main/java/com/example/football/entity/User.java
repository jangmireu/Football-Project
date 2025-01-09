package com.example.football.entity;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email; // 이메일 필드 추가
    
    private int points;
    
    @Column(name = "last_login_date")
    private LocalDate lastLoginDate;

    // Getters and Setters
    
    // 생성자, Getter, Setter
    public User() {
        this.points = 1000; // 회원가입 시 초기 포인트 설정
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public LocalDate getLastLoginDate() { return lastLoginDate; } // Getter 추가
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }
	public boolean isPresent() {
		return this.username != null && !this.username.isEmpty();
	}
}
