package com.example.football.controller;

import com.example.football.entity.User;
import com.example.football.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MypageController {
    private final UserService userService;

    public MypageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        Object loggedInUserId = session.getAttribute("loggedInUserId");
        if (loggedInUserId == null) {
            // 로그인되지 않은 경우, 로그인 페이지로 리다이렉트
            session.setAttribute("alertMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        Long currentUserId = userService.getCurrentUserId();
        User user = userService.getUserById(currentUserId);
        model.addAttribute("user", user);
        return "mypage";
    }

    @PostMapping("/mypage/update")
    public String updateUserInfo(
            @RequestParam String nickname,
            @RequestParam String name, // 이름 필드
            @RequestParam String address,
            @RequestParam String birthDate,
            @RequestParam String phone,
            HttpSession session, // 세션 추가
            Model model) {
        Long currentUserId = userService.getCurrentUserId();
        
        // 사용자 정보 업데이트
        userService.updateUserDetails(currentUserId, nickname, name, address, birthDate, phone);

        // 세션 업데이트: 닉네임 변경 반영
        session.setAttribute("loggedInNickname", nickname);

        model.addAttribute("successMessage", "수정이 완료되었습니다.");
        
        // 최신 사용자 정보 다시 조회
        User user = userService.getUserById(currentUserId);
        model.addAttribute("user", user);

        return "mypage"; // 현재 페이지를 다시 렌더링
    }
}
