package com.example.football.controller;

import com.example.football.entity.Match;
import com.example.football.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LiveScoreController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 모든 경기를 반환하는 REST 엔드포인트 (테스트 용도)
     */
    @GetMapping("/live-scores")
    public List<Match> getLiveScores() {
        return matchService.getMatches();
    }

    /**
     * WebSocket으로 실시간 스코어 데이터를 전송 (5초 간격)
     */
    @Scheduled(fixedRate = 10000) // 5초마다 실행
    public void sendLiveScores() {
        List<Match> matches = matchService.getMatches();

        if (matches != null && !matches.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/live-scores", matches);
            System.out.println("Live scores sent: " + matches);
        } else {
            System.out.println("No matches available to send.");
        }
    }
}
