package com.example.football;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.football.service.MatchService;
import com.example.football.service.StandingsService;

@Service
public class ScheduledTasks {

    private final StandingsService standingsService;
    private final MatchService matchService;

    public ScheduledTasks(StandingsService standingsService, MatchService matchService) {
        this.standingsService = standingsService;
        this.matchService = matchService;
    }

    // 리그 순위 갱신 (10분마다)
    @Scheduled(fixedRate = 600000) //  10분(600000ms)마다 실행
    public void updateStandings() {
        standingsService.updateStandings();
        System.out.println("리그 순위 갱신 중...");

    }

    // 경기 데이터 갱신 (10분마다)
    @Scheduled(fixedRate = 60000) // 10분(600000ms)마다 실행
    public void updateMatches() {
        matchService.updateMatches();
        System.out.println("경기 데이터 갱신 중..");

    }
}
