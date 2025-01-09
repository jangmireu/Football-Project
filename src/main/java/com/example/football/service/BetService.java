package com.example.football.service;

import com.example.football.entity.Bet;
import com.example.football.entity.User;
import com.example.football.repository.BetRepository;
import com.example.football.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class BetService {

    private final BetRepository betRepository;
    private final UserRepository userRepository;

    @Autowired
    public BetService(BetRepository betRepository, UserRepository userRepository) {
        this.betRepository = betRepository;
        this.userRepository = userRepository;
    }

    // 배팅 저장
    @Transactional
    public Bet placeBet(Long userId, String matchIdentifier, int amount, String choice)
    {
        System.out.println("User ID: " + userId);
        System.out.println("Match Identifier: " + matchIdentifier);
        System.out.println("Bet Amount: " + amount);
        System.out.println("Bet Choice: " + choice);

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 ID가 잘못되었습니다."));
        System.out.println("User Found: " + user.getUsername());

        if (user.getPoints() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        // 사용자 포인트 차감
        user.setPoints(user.getPoints() - amount);
        userRepository.save(user);

        System.out.println("Updated User Points: " + user.getPoints());

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
            session.setAttribute("loggedInUser", user);
        
        // 배팅 데이터 저장
        Bet bet = new Bet();
        bet.setUser(user);
        bet.setMatchIdentifier(matchIdentifier);
        bet.setAmount(amount);
        bet.setChoice(choice);
        bet.setPlacedAt(java.time.LocalDateTime.now());

        Bet savedBet = betRepository.save(bet);
        System.out.println("Bet Saved: " + savedBet.getId());
        return savedBet;
    }
    public void updateBetWithClaimedPoints(Bet bet) {
        bet.setPointsClaimed(true); // 포인트 지급 상태로 업데이트
        betRepository.save(bet); // 배팅 상태 저장
    }
    
   /*
    public void addPointsToUser(Long userId, int points) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        user.setPoints(user.getPoints() + points); // 기존 포인트에 추가
        userRepository.save(user); // 사용자 포인트 저장
    }*/
    // 배팅 ID로 배팅 정보 조회
    public Bet getBetById(Long betId) {
        return betRepository.findById(betId).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 배팅 ID입니다."));
    }

    // 사용자 배팅 조회
    public List<Bet> getUserBets(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 ID가 잘못되었습니다."));
        return betRepository.findByUser(user);
    }

    // 특정 경기 배팅 조회
    public List<Bet> getBetsByUserIdAndMatchIdentifier(Long userId, String matchIdentifier) {
        return betRepository.findByUserIdAndMatchIdentifier(userId, matchIdentifier);
    }
    
    // 특정 경기와 사용자에 대한 중복 배팅 확인
    public boolean isBetPlaced(Long userId, String matchIdentifier) {
        return betRepository.existsByUser_IdAndMatchIdentifier(userId, matchIdentifier);
    }
}
