package com.example.football.service;

import com.example.football.dto.ChatLogDTO;
import com.example.football.entity.ChatLog;
import com.example.football.entity.User;
import com.example.football.repository.ChatLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatLogService {

    @Autowired
    private ChatLogRepository chatLogRepository;

    public List<ChatLogDTO> getChatLogs(Long matchId) {
        List<ChatLog> chatLogs = chatLogRepository.findByMatchIdOrderByTimestampAsc(matchId);
        return chatLogs.stream()
                .map(log -> new ChatLogDTO(
                        log.getId(),
                        log.getUser().getNickname(),
                        log.getContent(),
                        log.getUser().getBadge() != null ? log.getUser().getBadge().getName() : null
                ))
                .collect(Collectors.toList());
    }

    public ChatLog saveChatLog(Long matchId, User user, String content) {
        ChatLog chatLog = new ChatLog();
        chatLog.setMatchId(matchId);
        chatLog.setUser(user);
        chatLog.setContent(content);
        if (user.getBadge() != null) {
            chatLog.setBadge(user.getBadge().getName());
        }
        return chatLogRepository.save(chatLog);
    }
}
