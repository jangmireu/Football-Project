package com.example.football.dto;

import java.util.List;

public class ApiResponse {
    private List<MatchResponse> matches;

    public List<MatchResponse> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchResponse> matches) {
        this.matches = matches;
    }
}
