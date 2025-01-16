package com.example.football.controller;

import com.example.football.entity.Match;
import com.example.football.entity.Standing;
import com.example.football.service.MatchService;
import com.example.football.service.StandingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/prediction")
public class PredictionController {

    private final MatchService matchService;
    private final StandingsService standingsService;

    @Autowired
    public PredictionController(MatchService matchService, StandingsService standingsService) {
        this.matchService = matchService;
        this.standingsService = standingsService;
    }

    @GetMapping
    public String getPredictionPage(Model model) {
        // 경기 데이터 및 순위 데이터 가져오기
        List<Match> matches = matchService.getMatches();
        long nowMillis = System.currentTimeMillis();

        matches = matches.stream()
                .filter(match -> !"IN_PLAY".equalsIgnoreCase(match.getStatus())) // 진행 중인 경기 제외
                .filter(match -> !"FINISHED".equalsIgnoreCase(match.getStatus())) // 종료된 경기 제외
                .filter(match -> {
                    // 경기 시작 시간이 null이 아닌 경우 필터링
                    if (match.getUtcDate() != null) {
                        // UTC 날짜를 밀리초로 변환
                        long matchMillis = ZonedDateTime.parse(match.getUtcDate()).toInstant().toEpochMilli();
                        return matchMillis > nowMillis + (10 * 60 * 1000); // 현재 시간보다 10분 이후의 경기만 포함
                    }
                    return false; // utcDate가 null인 경우 제외
                })
                .sorted(Comparator.comparing(Match::getUtcDate)) // 시간순 정렬
                .toList();

        // 순위 데이터 가져오기
        List<Standing> standings = standingsService.getStandings();

        model.addAttribute("matches", matches);
        model.addAttribute("standings", standings);
        return "prediction";
    }
}
