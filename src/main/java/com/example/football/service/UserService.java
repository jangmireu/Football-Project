package com.example.football.service;

import com.example.football.entity.User;
import com.example.football.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
	
    private final UserRepository userRepository;
    private final HttpSession session;

    @Autowired
    public UserService(UserRepository userRepository, HttpSession session) {
        this.userRepository = userRepository;
        this.session = session;
    }
    
    // 현재 로그인된 사용자 ID 가져오기
    public Long getCurrentUserId() {
        Object userId = session.getAttribute("loggedInUserId"); // 세션에서 사용자 ID 가져오기
        if (userId == null) {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }
        return (Long) userId;
    }
    

    // 사용자 ID로 사용자 가져오기
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

 // 사용자에게 포인트를 추가하는 메서드
    public void addPointsToUser(Long userId, int points) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found"));
        user.setPoints(user.getPoints() + points);  // 기존 포인트에 추가
        userRepository.save(user);  // 사용자 정보 저장
    }

    // 사용자 포인트 차감
    public void deductPointsFromUser(Long userId, int points) {
        User user = getUserById(userId);
        if (user.getPoints() < points) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        user.setPoints(user.getPoints() - points);
        userRepository.save(user);
    }

    // 사용자 포인트 조회
    public int getUserPoints(Long userId) {
        User user = getUserById(userId);
        return user.getPoints();
    }
    
    public void updateUserDetails(Long userId, String nickname, String name, String address, String birthDate, String phone) {
        User user = getUserById(userId);
        user.setNickname(nickname);
        user.setName(name); // 이름 업데이트
        user.setAddress(address);
        user.setBirthDate(birthDate);
        user.setPhone(phone);
        userRepository.save(user);
    }
}
