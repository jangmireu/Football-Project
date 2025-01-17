package com.example.football.controller;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final List<FaqEntry> faqList = new ArrayList<>();

    public ChatbotController() {
        // FAQ 데이터 추가 (키워드와 답변)
        faqList.add(new FaqEntry(List.of("배팅", "시간" ,"언제"), "경기 시작시간 10분 전까지 배팅이 가능합니다."));
        faqList.add(new FaqEntry(List.of("포인트", "받","결과"), "결과예측 성공시 결과확인 페이지에서 배팅결과 확인 버튼을 누른후 포인트를 받을 수 있습니다."));
        faqList.add(new FaqEntry(List.of("유니폼","언제", "구매","배송","발송"), "공식 스토어에서 구매 후 직접 발송하기 때문에 영업일 7~14일 정도 소요됩니다."));
        faqList.add(new FaqEntry(List.of("유니폼", "반품","취소","사이즈"), "저희 사이트에서 제공되는 유니폼은 포인트를 사용해 구매하시는 특별 상품으로, 반품이 불가합니다. 구매 전에 사이즈와 디자인을 신중히 확인해 주시기 바랍니다."));
    }

    @PostMapping
    public ResponseEntity<String> getResponse(@RequestBody Map<String, String> request) {
        String question = request.get("question").toLowerCase(); // 입력 질문 소문자로 변환
        String[] words = question.split("\\s+"); // 질문을 단어 단위로 분리

        // 가장 적합한 답변 찾기
        String bestResponse = "죄송합니다. 해당 질문에 대한 답변을 찾을 수 없습니다.";
        int maxMatchCount = 0;

        for (FaqEntry entry : faqList) {
            int matchCount = 0;
            for (String keyword : entry.keywords) {
                if (Arrays.stream(words).anyMatch(word -> word.contains(keyword))) {
                    matchCount++;
                }
            }
            if (matchCount > maxMatchCount) {
                maxMatchCount = matchCount;
                bestResponse = entry.response;
            }
        }

        return ResponseEntity.ok(bestResponse);
    }

    // FAQ 항목 클래스
    private static class FaqEntry {
        List<String> keywords;
        String response;

        FaqEntry(List<String> keywords, String response) {
            this.keywords = keywords;
            this.response = response;
        }
    }
}
