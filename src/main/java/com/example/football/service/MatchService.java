package com.example.football.service;

import com.example.football.dto.ApiResponse;
import com.example.football.dto.MatchResponse;
import com.example.football.entity.Match;
import com.example.football.repository.MatchRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final MatchRepository matchRepository;

    public MatchService(RestTemplate restTemplate, MatchRepository matchRepository) {
        this.restTemplate = restTemplate;
        this.matchRepository = matchRepository;
    }
    
    private final Map<String, String> teamNameMap = Map.ofEntries(
            Map.entry("Manchester City FC", "맨시티"),
            Map.entry("Liverpool FC", "리버풀"),
            Map.entry("Arsenal FC", "아스널"),
            Map.entry("Chelsea FC", "첼시"),
            Map.entry("Tottenham Hotspur FC", "토트넘"),
            Map.entry("Manchester United FC", "맨유"),
            Map.entry("Newcastle United FC", "뉴캐슬"),
            Map.entry("Brighton & Hove Albion FC", "브라이튼"),
            Map.entry("Aston Villa FC", "아스톤빌라"),
            Map.entry("Fulham FC", "풀럼"),
            Map.entry("Brentford FC", "브렌트퍼드"),
            Map.entry("Crystal Palace FC", "크리스탈 팰리스"),
            Map.entry("Wolverhampton Wanderers FC", "울버햄튼"),
            Map.entry("West Ham United FC", "웨스트햄"),
            Map.entry("Nottingham Forest FC", "노팅엄"),
            Map.entry("AFC Bournemouth", "본머스"),
            Map.entry("Leicester City FC", "레스터"),
            Map.entry("Everton FC", "에버턴"),
            Map.entry("Southampton FC", "사우샘프턴"),
            Map.entry("Ipswich Town FC", "입스위치")
        );

    // DB에서 경기 데이터 가져오기
    public List<Match> getMatches() {
        return matchRepository.findAll(); // 현재 DB에 저장된 데이터를 반환
    }

    // API에서 경기 데이터 갱신
    @Transactional
    public void updateMatches() {
    	ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    	String dateFrom = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dateTo = now.plusDays(7).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        String url = apiUrl + "/matches?competitions=PL&dateFrom=" + dateFrom + "&dateTo=" + dateTo;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, ApiResponse.class
        );

        List<Match> matches = response.getBody().getMatches().stream()
                .map(this::toMatchEntity)
                .collect(Collectors.toList());

        // 기존 데이터 삭제 후 새 데이터 저장
        matchRepository.deleteAll();
        matchRepository.saveAll(matches);
    }

    private Match toMatchEntity(MatchResponse matchResponse) {
        Match match = new Match();
        match.setId(matchResponse.getId());
        match.setUtcDate(matchResponse.getUtcDate());
        match.setStatus(matchResponse.getStatus());
        
        
        // 홈 팀 데이터 설정
        if (matchResponse.getHomeTeam() != null) {
            match.setHomeTeam(matchResponse.getHomeTeam().getName());
            match.setHomeTeamCrest(matchResponse.getHomeTeam().getCrest());
            match.setHomeTeamShortName(matchResponse.getHomeTeam().getShortName());
            match.setHomeTeamTla(matchResponse.getHomeTeam().getTla());

            // 한글 이름 매핑
            String homeKoreanName = teamNameMap.getOrDefault(matchResponse.getHomeTeam().getName(), "매핑되지 않음");
            match.setHomeTeamKoreanName(homeKoreanName);
        }

        // 원정 팀 데이터 설정
        if (matchResponse.getAwayTeam() != null) {
            match.setAwayTeam(matchResponse.getAwayTeam().getName());
            match.setAwayTeamCrest(matchResponse.getAwayTeam().getCrest());
            match.setAwayTeamShortName(matchResponse.getAwayTeam().getShortName());
            match.setAwayTeamTla(matchResponse.getAwayTeam().getTla());
            
            
            
            // 한글 이름 매핑
            String awayKoreanName = teamNameMap.getOrDefault(matchResponse.getAwayTeam().getName(), "매핑되지 않음");
            match.setAwayTeamKoreanName(awayKoreanName);
        }
        
        // 점수 정보 설정
        if (matchResponse.getScore() != null && matchResponse.getScore().getFullTime() != null) {
            match.setHomeScore(matchResponse.getScore().getFullTime().getHome());
            match.setAwayScore(matchResponse.getScore().getFullTime().getAway());
        } else {
            match.setHomeScore(null);
            match.setAwayScore(null);
        }

        // 경기 상태 설정
        match.setStatus(matchResponse.getStatus() != null ? matchResponse.getStatus() : "Unknown");

        return match;
    }
    
    public List<Match> getPastMatches() {
        return matchRepository.findAllByOrderByUtcDateAsc() // 모든 경기 시간순 정렬
                .stream()
                .filter(match -> "FINISHED".equalsIgnoreCase(match.getStatus())) // 종료된 경기만 필터링
                .collect(Collectors.toList());
    }
 // MatchService.java
    public Match getMatchById(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 경기를 찾을 수 없습니다."));
    }





 // 애플리케이션 초기화 시 실행
    @PostConstruct
    public void initMatchData() {
        System.out.println("경기 초기 데이터를 로드합니다...");
        updateMatches(); // 경기 데이터 초기화
    }
    }

  

