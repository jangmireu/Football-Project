package com.example.football.controller;

import com.example.football.entity.User;
import com.example.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user != null) {
            LocalDate today = LocalDate.now();

            // 하루에 한 번 로그인 보너스 포인트 지급
            if (user.getLastLoginDate() == null || !user.getLastLoginDate().isEqual(today)) {
                user.setPoints(user.getPoints() + 100);
                user.setLastLoginDate(today);
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("message", "로그인 완료! 100포인트가 지급되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("message", "로그인 완료!");
            }

            // 세션에 사용자 정보를 저장
            session.setAttribute("loggedInUser", user); // User 객체 전체 저장
            session.setAttribute("loggedInUsername", user.getUsername()); // 사용자 이름
            session.setAttribute("loggedInUserId", user.getId()); // 사용자 ID
            session.setAttribute("loggedInUserPoints", user.getPoints()); // 사용자 포인트
            session.setAttribute("loggedInNickname", user.getNickname()); // 닉네임 저장

            return "redirect:/matches/list"; // 로그인 후 리다이렉트
        } else {
            // 로그인 실패 시 메시지 설정 및 리다이렉트
            redirectAttributes.addFlashAttribute("message", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "redirect:/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
    	session.invalidate(); // 세션 무효화
        redirectAttributes.addFlashAttribute("message", "로그아웃 되었습니다.");
        return "redirect:/matches/list";
    }
}
