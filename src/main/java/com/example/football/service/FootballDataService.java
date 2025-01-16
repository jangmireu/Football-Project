package com.example.football.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class FootballDataService {

    @Value("${football.api.url}")
    private String apiUrl;

    @Value("${football.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getScheduledPremierLeagueMatches() {
        // 현재 날짜와 7일 후 날짜를 UTC 기준으로 설정
        LocalDate today = LocalDate.now(ZoneOffset.UTC);  // UTC로 변환
        LocalDate sevenDaysLater = today.plusDays(7);    // 7일 후 날짜

        // API 요청 URL
        String url = String.format("%s/matches", apiUrl);

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);

        // 요청 파라미터 설정
        Map<String, String> params = new HashMap<>();
        params.put("dateFrom", today.format(formatter));        // 오늘 날짜 (UTC)
        params.put("dateTo", sevenDaysLater.format(formatter)); // 7일 후 날짜 (UTC)
        params.put("competitions", "PL");                      // 프리미어리그 필터링
        params.put("status", "SCHEDULED");                     // 예정된 경기 상태 필터링

        // HTTP 요청 생성 및 전송
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url + "?dateFrom={dateFrom}&dateTo={dateTo}&competitions={competitions}&status={status}",
            HttpMethod.GET,
            entity,
            String.class,
            params
        );

        // API 응답 반환
        return response.getBody();
    }
}
