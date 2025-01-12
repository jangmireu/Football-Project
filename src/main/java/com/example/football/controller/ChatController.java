package com.example.football.controller;

import com.example.football.entity.ChatLog;
import com.example.football.service.ChatLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatLogService chatLogService;

    // 채팅 메시지 클래스
    public static class ChatMessage {
        private Long matchId; // 경기 ID 추가
        private String user;
        private String content;

        // Getters and Setters
        public Long getMatchId() {
            return matchId;
        }

        public void setMatchId(Long matchId) {
            this.matchId = matchId;
        }

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

    // WebSocket 메시지 처리 및 저장
    @MessageMapping("/sendMessage/{matchId}")
    @SendTo("/topic/messages/{matchId}")
    public String handleMessage(@DestinationVariable Long matchId, ChatMessage chatMessage) {
        chatLogService.saveChatLog(matchId, chatMessage.getUser(), chatMessage.getContent());
        return chatMessage.getUser() + ": " + chatMessage.getContent();
    }


    // 특정 경기의 채팅 기록 조회
    @GetMapping("/logs/{matchId}")
    @ResponseBody
    public List<ChatLog> getChatLogs(@PathVariable Long matchId) {
        return chatLogService.getChatLogs(matchId);
    }
}
