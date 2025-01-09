package com.example.football.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 배팅한 사용자

    @Column(nullable = false)
    private String matchIdentifier; // 경기 식별자 (예: 경기 ID, 팀명 등)

    
    @Column(nullable = false)
    private int amount; // 배팅 금액

    @Column(nullable = false)
    private String choice; // 선택한 결과 (WIN, DRAW, LOSE)

    @Column
    private Boolean isCorrect; // 배팅 결과 적중 여부

    @Column(nullable = false)
    private LocalDateTime placedAt; // 배팅 시간
    
    private Boolean pointsClaimed = false;  // 포인트 지급 여부

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMatchIdentifier() {
        return matchIdentifier;
    }

    public void setMatchIdentifier(String matchIdentifier) {
        this.matchIdentifier = matchIdentifier;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }
    
    public Boolean getPointsClaimed() {
        return pointsClaimed;
    }

    public void setPointsClaimed(Boolean pointsClaimed) {
        this.pointsClaimed = pointsClaimed;
    }
    
}
