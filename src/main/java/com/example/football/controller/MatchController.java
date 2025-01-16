package com.example.football.controller;

import com.example.football.entity.Match;
import com.example.football.entity.Standing;
import com.example.football.repository.MatchRepository;
import com.example.football.service.MatchService;
import com.example.football.service.StandingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepository;
    private final StandingsService standingsService; // 추가된 StandingsService

    @Autowired
    public MatchController(MatchService matchService, 
                           MatchRepository matchRepository, 
                           StandingsService standingsService) {
        this.matchService = matchService;
        this.matchRepository = matchRepository;
        this.standingsService = standingsService;
    }

    @GetMapping("/list")
    public String getMatchesAndStandings(Model model) {
        // DB에서 데이터 가져오기
    	model.addAttribute("matches", matchRepository.findAllByOrderByUtcDateAsc());
        model.addAttribute("standings", standingsService.getStandings());
        return "matches";
    }

    @GetMapping("/details/{id}")
    public String getMatchDetails(@PathVariable("id") Long id, Model model) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid match ID: " + id));
        model.addAttribute("match", match);
        return "matchDetails";
    }
   

    

}
