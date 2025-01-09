package com.example.football.controller;

import com.example.football.entity.User;
import com.example.football.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 페이지로 이동
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String email,
                         Model model, RedirectAttributes redirectAttributes) {

        // 중복된 아이디 체크
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "이미 존재하는 아이디입니다. 다른 아이디를 사용해주세요.");
            return "signup";
        }

        // 중복이 아닌 경우 사용자 저장
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 비밀번호 암호화는 필수적입니다.
        user.setEmail(email); // 이메일 설정
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/matches/list"; // 회원가입 완료 후 경기 목록 페이지로 이동
    }

    // 현재 로그인한 사용자의 이름 반환
    @GetMapping("/api/current-username")
    @ResponseBody
    public String getCurrentUsername(HttpSession session) {
        String username = (String) session.getAttribute("loggedInUsername");
        if (username != null) {
            return username;
        }
        return "Anonymous"; // 로그인하지 않은 경우 기본값
    }
}
