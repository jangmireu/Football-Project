package com.example.football.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.football.entity.Badge;
import com.example.football.repository.BadgeRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    public DataLoader(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (badgeRepository.count() == 0) { // 데이터 중복 방지
            badgeRepository.save(new Badge(1L, "금색 훈장입니다.", "images/badges/gold_badge.png", "Gold Badge", 1000));
            badgeRepository.save(new Badge(2L, "은색 훈장입니다.", "images/badges/silver_badge.png", "Silver Badge", 800));
            badgeRepository.save(new Badge(3L, "동색 훈장입니다.", "images/badges/bronze_badge.png", "Bronze Badge", 500));
            badgeRepository.save(new Badge(4L, "다이아몬드 훈장입니다.", "images/badges/diamond_badge.svg", "Diamond Badge", 1500));
        }
    }
}
