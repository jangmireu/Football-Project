package com.example.football.repository;

import com.example.football.entity.PastMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PastMatchRepository extends JpaRepository<PastMatch, Long> {
	List<PastMatch> findAllByOrderByUtcDateDesc();
    // String 타입으로 주어진 날짜 범위 내에서 'FINISHED' 상태인 경기를 조회
    List<PastMatch> findByUtcDateBetweenAndStatus(String startDate, String endDate, String status);
}

