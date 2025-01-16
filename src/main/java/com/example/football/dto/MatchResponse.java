package com.example.football.dto;

import com.example.football.entity.Score;
import com.example.football.entity.Team;

public class MatchResponse {
    private Long id;
    private Team homeTeam;
    private Team awayTeam;
    private String utcDate;
    private Score score;
    private String status; // 경기 상태 추가


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getUtcDate() {
        return utcDate;
    }

    public void setUtcDate(String utcDate) {
        this.utcDate = utcDate;
    }
    
    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
