package com.example.football.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.football.entity.User;
import com.example.football.repository.UserRepository;
import org.springframework.util.StringUtils;

@Controller
public class SignupController {

    private final UserRepository userRepository;

    @Autowired
    public SignupController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/signup")
    public String signupForm() {
        return "signup"; // signup.html 렌더링
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        // 필수 입력 값 확인
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(email)) {
            redirectAttributes.addFlashAttribute("message", "모든 필드를 입력해주세요.");
            return "redirect:/signup";
        }

        // 중복 아이디 확인
        if (userRepository.findByUsername(username) != null) {
            redirectAttributes.addFlashAttribute("message", "중복된 아이디입니다.");
            return "redirect:/signup";
        }

        try {
            // User 객체 생성 후 데이터베이스에 저장
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPoints(1000); // 초기 포인트 설정

            userRepository.save(user);

            // 성공 메시지를 RedirectAttributes에 저장
            redirectAttributes.addFlashAttribute("successMessage", "회원가입 완료! 1000포인트 지급됩니다!");
            return "redirect:/matches/list"; // 메인 페이지로 이동
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "회원가입에 실패했습니다. 다시 시도해주세요.");
            return "redirect:/signup";
        }
    }
}
