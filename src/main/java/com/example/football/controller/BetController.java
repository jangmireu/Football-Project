package com.example.football.controller;

import com.example.football.entity.Bet;
import com.example.football.service.BetService;
import com.example.football.service.PastMatchService;
import com.example.football.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bet")
public class BetController {

    private final BetService betService;
    private final UserService userService;
    private final PastMatchService pastMatchService; // PastMatchService 의존성
    @Autowired
    public BetController(BetService betService, UserService userService,PastMatchService pastMatchService) {
        this.betService = betService;
        this.userService = userService;
        this.pastMatchService = pastMatchService; // 의존성 주입
    }

    @PostMapping
    public Map<String, Object> placeBet(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Received request: " + request);

            Long userId = userService.getCurrentUserId(); // 로그인된 사용자 ID
            System.out.println("User ID: " + userId);

            String matchIdentifier = request.get("matchIdentifier").toString();
           
            int betAmount = Integer.parseInt(request.get("betAmount").toString());
            String betChoice = request.get("betChoice").toString();

            System.out.println("Match Identifier: " + matchIdentifier);
          
            System.out.println("Bet Amount: " + betAmount);
            System.out.println("Bet Choice: " + betChoice);
            
            // 중복 배팅 확인
            if (betService.isBetPlaced(userId, matchIdentifier)) {
                throw new IllegalArgumentException("이미 해당 경기에 배팅하셨습니다.");
            }
            
            // 필수 필드 검증
            if (matchIdentifier.isEmpty()) {
                throw new IllegalArgumentException("경기 식별자가 누락되었습니다.");
            }
            if (betAmount <= 0) {
                throw new IllegalArgumentException("유효하지 않은 배팅 금액입니다.");
            }

            Bet bet = betService.placeBet(userId, matchIdentifier, betAmount, betChoice);

            response.put("success", true);
            response.put("message", "배팅이 성공적으로 저장되었습니다.");
            response.put("bet", bet);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    
    @GetMapping("/result/{matchId}")
    public Map<String, Object> getBetResults(@PathVariable("matchId") Long matchId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 현재 로그인된 사용자 ID 가져오기
            Long userId = userService.getCurrentUserId(); // 로그인된 사용자 ID 확인
            if (userId == null) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }

            // matchId를 문자열로 변환하여 matchIdentifier 생성
            String matchIdentifier = String.valueOf(matchId);

            // 로그인된 사용자와 matchIdentifier에 해당하는 배팅 데이터 조회
            List<Bet> bets = betService.getBetsByUserIdAndMatchIdentifier(userId, matchIdentifier);

            if (bets.isEmpty()) {
                // 배팅 기록이 없는 경우
                response.put("success", false);
                response.put("message", "해당 경기의 배팅 기록이 없습니다.");
            } else {
                // PAST_MATCH 테이블에서 해당 경기의 실제 결과 가져오기
                String matchResult = pastMatchService.getMatchWinner(matchId); // 실제 경기 결과 조회
                List<Map<String, Object>> betResults = new ArrayList<>();

                // 배팅 데이터 순회하여 결과 생성
                for (Bet bet : bets) {
                	  String mappedChoice = bet.getChoice();
                      System.out.println("Choice: " + mappedChoice);
                      System.out.println("Match Result: " + matchResult);

                      boolean won = matchResult.trim().equalsIgnoreCase(mappedChoice.trim());
                      System.out.println("Won: " + won);

                      int returnedPoints = won ? (int) (bet.getAmount() * 1.5) : 0;

                      Map<String, Object> betResult = new HashMap<>();
                      betResult.put("betId", bet.getId()); // betId 추가
                      betResult.put("amount", bet.getAmount());
                      betResult.put("choice", mappedChoice);
                      betResult.put("won", won);
                      betResult.put("returnedPoints", returnedPoints);
                      betResults.add(betResult);
                  }

                  response.put("success", true);
                  response.put("bets", betResults);
                  response.put("matchResult", matchResult);
              }
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "배팅 데이터를 조회하는 중 오류가 발생했습니다.");
        }
        return response;
    }
    
    @PostMapping("/claim-points/{betId}")
    public Map<String, Object> claimPoints(@PathVariable("betId") Long betId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 로그인된 사용자 ID 가져오기
            Long userId = userService.getCurrentUserId(); // 로그인된 사용자 ID 확인
            if (userId == null) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }

            // 해당 배팅 ID로 배팅 정보 조회
            Bet bet = betService.getBetById(betId);

            if (bet == null) {
                response.put("success", false);
                response.put("message", "유효하지 않은 배팅 ID입니다.");
                return response;
            }

            // 이미 포인트가 지급된 경우 처리
            if (bet.getPointsClaimed() != null && bet.getPointsClaimed()) {
                response.put("success", false);
                response.put("message", "이미 포인트가 지급되었습니다.");
                return response;
            }

            // 포인트 지급 (배팅 결과와 상관없이 무조건 지급)
            int returnedPoints = (int) (bet.getAmount() * 1.5); // 배팅 금액의 1.5배 포인트 지급
            userService.addPointsToUser(userId, returnedPoints); // 사용자에게 포인트 지급

            // 포인트 지급 여부 업데이트
            bet.setPointsClaimed(true); // 포인트 지급 완료 표시
            betService.updateBetWithClaimedPoints(bet); // 배팅 상태 업데이트

            response.put("success", true);
            response.put("message", "포인트가 지급되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "포인트 지급에 실패했습니다: " + e.getMessage());
        }
        return response;
    }




}
