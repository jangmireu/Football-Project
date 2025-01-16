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
    private String email;
    
    @Column(unique = true) // 닉네임 중복 방지
    private String nickname;
    
    private String address;
    private String name;       // 이름
    private String birthDate;  // 생년월일
    private String phone;
    
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
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public LocalDate getLastLoginDate() { return lastLoginDate; } // Getter 추가
    public void setLastLoginDate(LocalDate lastLoginDate) { this.lastLoginDate = lastLoginDate; }
	public boolean isPresent() {
		return this.username != null && !this.username.isEmpty();
	}
}
