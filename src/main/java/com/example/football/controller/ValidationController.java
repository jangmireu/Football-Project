package com.example.football.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.football.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ValidationController {

    private final UserRepository userRepository;

    @Autowired
    public ValidationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 아이디 중복 확인
    @GetMapping("/check-username")
    public Map<String, String> checkUsername(@RequestParam("username") String username) {
        boolean exists = userRepository.findByUsername(username) != null;
        Map<String, String> response = new HashMap<>();
        if (exists) {
            response.put("message", "중복된 아이디입니다.");
        } else {
            response.put("message", "사용 가능한 아이디입니다.");
        }
        return response;
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public Map<String, String> checkNickname(@RequestParam("nickname") String nickname) {
        boolean exists = userRepository.findByNickname(nickname) != null;
        Map<String, String> response = new HashMap<>();
        if (exists) {
            response.put("message", "중복된 닉네임입니다.");
        } else {
            response.put("message", "사용 가능한 닉네임입니다.");
        }
        return response;
    }
}
