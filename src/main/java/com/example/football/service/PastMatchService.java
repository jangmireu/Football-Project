package com.example.football.service;

import com.example.football.dto.PastMatchApiResponse;
import com.example.football.dto.MatchResponse;
import com.example.football.entity.PastMatch;
import com.example.football.entity.Score.FullTime;
import com.example.football.repository.PastMatchRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class PastMatchService {

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final PastMatchRepository pastMatchRepository;

    public PastMatchService(RestTemplate restTemplate, PastMatchRepository pastMatchRepository) {
        this.restTemplate = restTemplate;
        this.pastMatchRepository = pastMatchRepository;
    }
    
    public String getMatchWinner(Long matchId) {
        return pastMatchRepository.findById(matchId)
                .map(pastMatch -> pastMatch.getMatchWinner())
                .orElseThrow(() -> new IllegalArgumentException("경기를 찾을 수 없습니다: matchId=" + matchId));
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
    public PastMatch getPastMatchById(Long id) {
        return pastMatchRepository.findById(id).orElse(null);  // ID로 찾고, 없으면 null 반환
    }
    

    // 과거 경기 데이터를 API에서 가져오기
    @Transactional
    public void updatePastMatches() {
    	ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime nineDaysAgo = now.minusDays(9);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateFrom = nineDaysAgo.format(formatter); // 9일 전 날짜
        String dateTo = now.format(formatter); // 현재 날짜

        // URL 생성
        String url = apiUrl + "/matches?competitions=PL&dateFrom=" + dateFrom + "&dateTo=" + dateTo;

        // API 데이터 가져오기
        PastMatchApiResponse apiResponse = fetchPastMatchData(url);

        if (apiResponse.getMatches() == null) {
            throw new IllegalStateException("API에서 경기를 가져올 수 없습니다.");
        }

        // 과거 경기를 PastMatch 엔티티로 변환
        List<PastMatch> pastMatches = apiResponse.getMatches().stream()
                .map(this::toPastMatchEntity)
                .collect(Collectors.toList());

        // 기존 데이터 삭제 후 새 데이터 저장
        pastMatchRepository.deleteAll();
        pastMatchRepository.saveAll(pastMatches);
    }
    public List<PastMatch> getPastMatches() {
        // PastMatch 데이터를 데이터베이스에서 가져와서 반환
        return pastMatchRepository.findAllByOrderByUtcDateDesc();  // 최근 경기부터 나열
    }

    // API 응답을 처리하는 메소드
    private PastMatchApiResponse fetchPastMatchData(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PastMatchApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, PastMatchApiResponse.class
        );

        return response.getBody();  // API 응답을 PastMatchApiResponse 객체로 반환
    }

    // PastMatch 엔티티로 변환
    private PastMatch toPastMatchEntity(MatchResponse matchResponse) {
        PastMatch pastMatch = new PastMatch();
        pastMatch.setId(matchResponse.getId());
        pastMatch.setUtcDate(matchResponse.getUtcDate());
        pastMatch.setStatus(matchResponse.getStatus());

        // 홈팀 데이터
        if (matchResponse.getHomeTeam() != null) {
            pastMatch.setHomeTeam(matchResponse.getHomeTeam().getName());
            pastMatch.setHomeTeamCrest(matchResponse.getHomeTeam().getCrest());
            pastMatch.setHomeTeamShortName(matchResponse.getHomeTeam().getShortName());
            pastMatch.setHomeTeamTla(matchResponse.getHomeTeam().getTla());
            
            String homeTeamKoreanName = teamNameMap.get(matchResponse.getHomeTeam().getName());
            if (homeTeamKoreanName != null) {
                pastMatch.setHomeTeamKoreanName(homeTeamKoreanName);
            } else {
                pastMatch.setHomeTeamKoreanName(matchResponse.getHomeTeam().getName()); // 기본값으로 영어 이름 설정
            }
        }

        // 원정팀 데이터
        if (matchResponse.getAwayTeam() != null) {
            pastMatch.setAwayTeam(matchResponse.getAwayTeam().getName());
            pastMatch.setAwayTeamCrest(matchResponse.getAwayTeam().getCrest());
            pastMatch.setAwayTeamShortName(matchResponse.getAwayTeam().getShortName());
            pastMatch.setAwayTeamTla(matchResponse.getAwayTeam().getTla());
            
            String awayTeamKoreanName = teamNameMap.get(matchResponse.getAwayTeam().getName());
            if (awayTeamKoreanName != null) {
                pastMatch.setAwayTeamKoreanName(awayTeamKoreanName);
            } else {
                pastMatch.setAwayTeamKoreanName(matchResponse.getAwayTeam().getName()); // 기본값으로 영어 이름 설정
            }
        }

        // 점수 데이터 (Score)
        if (matchResponse.getScore() != null && matchResponse.getScore().getFullTime() != null) {
            FullTime fullTime = matchResponse.getScore().getFullTime();
            if (fullTime.getHome() != null) {
                pastMatch.setHomeScore(fullTime.getHome());
            }
            if (fullTime.getAway() != null) {
                pastMatch.setAwayScore(fullTime.getAway());
            }


         // API에서 제공하는 승자 정보 설정
            String matchWinner = matchResponse.getScore().getWinner();
            if (matchWinner != null) {
                pastMatch.setMatchWinner(matchWinner); // HOME_TEAM, AWAY_TEAM, DRAW
            } else {
                // 승자가 없는 경우 (무승부 혹은 처리되지 않은 경우) - 예시로 무승부 처리
                if (pastMatch.getHomeScore() != null && pastMatch.getAwayScore() != null) {
                    if (pastMatch.getHomeScore().equals(pastMatch.getAwayScore())) {
                        pastMatch.setMatchWinner("DRAW");  // 무승부 처리
                    } else if (pastMatch.getHomeScore() > pastMatch.getAwayScore()) {
                        pastMatch.setMatchWinner("HOME_TEAM"); // 홈팀 승리
                    } else {
                        pastMatch.setMatchWinner("AWAY_TEAM"); // 원정팀 승리
                    }
                }
            }

        }

        return pastMatch;
    }

}
