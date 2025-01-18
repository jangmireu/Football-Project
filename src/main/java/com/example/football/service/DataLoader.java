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
            badgeRepository.save(new Badge(1L, "동색 뱃지입니다.", "images/badges/bronze_badge.png", "Bronze Badge", 10000));
            badgeRepository.save(new Badge(2L, "은색 뱃지입니다", "images/badges/silver_badge.png", "Silver Badge", 30000));
            badgeRepository.save(new Badge(3L, "금색 뱃지입니다.", "images/badges/gold_badge.png", "Gold Badge", 50000));
            badgeRepository.save(new Badge(4L, "다이아몬드 뱃지입니다.", "images/badges/diamond_badge.svg", "Diamond Badge", 100000));
            badgeRepository.save(new Badge(5L, "레전드 뱃지입니다.", "images/badges/diamond_badge.svg", "Legend Badge", 2000000000));
        }
    }
}
