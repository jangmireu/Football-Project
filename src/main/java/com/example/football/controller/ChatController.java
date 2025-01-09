package com.example.football.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // 채팅 메시지 클래스
    public static class ChatMessage {
        private String user;
        private String content;

        // Getters and Setters
        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public String handleMessage(ChatMessage chatMessage) {
        return chatMessage.getUser() + ": " + chatMessage.getContent(); // 사용자 이름과 메시지 반환
    }
}
