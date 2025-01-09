package com.example.football.controller;

import com.example.football.entity.PastMatch;
import com.example.football.service.PastMatchService;
import com.example.football.service.StandingsService;
import com.example.football.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/past-matches")
public class PastMatchController {

    private final PastMatchService pastMatchService;
    private final StandingsService standingsService;
    private final UserService userService;

    @Autowired
    public PastMatchController(PastMatchService pastMatchService, StandingsService standingsService, UserService userService) {
        this.pastMatchService = pastMatchService;
        this.standingsService = standingsService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String getPastMatchesAndStandings(Model model, HttpSession session) {
        // 과거 경기 데이터를 새로 업데이트
        pastMatchService.updatePastMatches();

        List<PastMatch> pastMatches = pastMatchService.getPastMatches();
        model.addAttribute("matches", pastMatches);  // 과거 경기 목록
        model.addAttribute("standings", standingsService.getStandings());  // 스탠딩 데이터

        // 세션에서 사용자 포인트 가져오기
        try {
            Long userId = userService.getCurrentUserId();  // 현재 로그인된 사용자 ID 가져오기
            int points = userService.getUserPoints(userId);  // 해당 사용자의 포인트 가져오기
            model.addAttribute("userPoints", points);  // 포인트를 뷰에 전달
        } catch (IllegalStateException e) {
            // 로그인되지 않은 경우 포인트를 0으로 설정
            model.addAttribute("userPoints", 0);  // 기본값 0으로 설정
        }

        return "past-matches";  // 과거 경기 목록 페이지로 이동
    }
}
