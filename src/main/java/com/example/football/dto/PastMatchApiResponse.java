package com.example.football.dto;

import com.example.football.dto.MatchResponse;
import java.util.List;

public class PastMatchApiResponse {
    private List<MatchResponse> matches;  // 과거 경기 목록

    public List<MatchResponse> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchResponse> matches) {
        this.matches = matches;
    }
}
