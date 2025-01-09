package com.example.football.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.football.service.FootballDataService;

@RestController
@RequestMapping("/football") 
public class FootballController {

    private final FootballDataService footballDataService;

    public FootballController(FootballDataService footballDataService) {
        this.footballDataService = footballDataService;
    }

    @GetMapping("/matches")
    public String getMatches() {
    	return footballDataService.getScheduledPremierLeagueMatches();
    }
}