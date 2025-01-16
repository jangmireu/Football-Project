package com.example.football.repository;

import com.example.football.entity.Bet;
import com.example.football.entity.Match;
import com.example.football.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByUser(User user); // 특정 사용자의 배팅 목록
    List<Bet> findByMatchIdentifier(String matchIdentifier); // matchIdentifier로 검색
    List<Bet> findByUserIdAndMatchIdentifier(Long userId, String matchIdentifier);
    boolean existsByUser_IdAndMatchIdentifier(Long userId, String matchIdentifier);
}
