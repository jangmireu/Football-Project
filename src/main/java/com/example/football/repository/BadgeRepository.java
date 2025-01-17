package com.example.football.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.football.entity.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
