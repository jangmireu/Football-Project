package com.example.football.service;

import com.example.football.dto.StandingsResponse;
import com.example.football.entity.Standing;
import com.example.football.repository.StandingRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StandingsService {

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final StandingRepository standingRepository;

    public StandingsService(RestTemplate restTemplate, StandingRepository standingRepository) {
        this.restTemplate = restTemplate;
        this.standingRepository = standingRepository;
    }

    // 팀 이름을 한글로 매핑
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

    // DB에서 순위 데이터 가져오기
    public List<Standing> getStandings() {
        return standingRepository.findAll();
    }

    // API에서 리그 순위 갱신
    @Transactional
    public void updateStandings() {
        String url = apiUrl + "/competitions/PL/standings";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<StandingsResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, StandingsResponse.class
        );

        // API 응답에서 Standing 데이터 변환
        List<Standing> standings = response.getBody().getStandings().stream()
                .flatMap(standingResponse -> standingResponse.getTable().stream())
                .map(this::toStandingEntity)
                .collect(Collectors.toList());

        // 기존 데이터 삭제 후 새 데이터 저장
        standingRepository.deleteAll();
        standingRepository.saveAll(standings);
    }

    private Standing toStandingEntity(StandingsResponse.TableEntry tableEntry) {
        Standing standing = new Standing();
        standing.setPosition(tableEntry.getPosition());
        standing.setTeamName(tableEntry.getTeam().getName());
        standing.setCrestUrl(tableEntry.getTeam().getCrest());
        standing.setPoints(tableEntry.getPoints());
        standing.setTla(tableEntry.getTeam().getTla());

        // 한글 이름 매핑 및 디버깅 로그 추가
        String koreanName = teamNameMap.getOrDefault(tableEntry.getTeam().getName(), "매핑되지 않음");
        standing.setKoreanTeamName(koreanName);
        System.out.println("매핑된 팀 이름: " + koreanName);
        
        return standing;
    }

    // 애플리케이션 초기화 시 실행
    @PostConstruct
    public void initStandingsData() {
        System.out.println("리그 순위 초기 데이터를 로드합니다...");
        updateStandings();
    }
}
