package com.example.football.repository;

import com.example.football.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandingRepository extends JpaRepository<Standing, Long> {
}
