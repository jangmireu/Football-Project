package com.example.football.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.football.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // 특정 날짜 이전의 데이터를 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.utcDate < :utcDate")
    void deleteByUtcDateBefore(String utcDate);
    List<Match> findAllByOrderByUtcDateAsc();
    

    // ID로 존재 여부 확인
    boolean existsById(Long id);
}
