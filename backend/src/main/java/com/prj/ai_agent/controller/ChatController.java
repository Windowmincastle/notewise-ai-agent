package com.prj.ai_agent.controller;

import com.prj.ai_agent.dto.NoteDto;
import com.prj.ai_agent.service.GeminiService;
import com.prj.ai_agent.service.WebhookService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // React 개발 서버 포트 허용
public class ChatController {

    private final GeminiService geminiService;
    private final WebhookService webhookService;
    /**
     * AI와 대화하는 API
     * @param request 사용자의 메시지와 이전 대화 이력
     * @return 요약된 지식 노트(NoteDto)
     */
    @PostMapping
    public ResponseEntity<NoteDto> chat(@RequestBody ChatRequest request) {
        log.info("채팅 요청 수신: {}", request.getMessage());

        // Gemini 서비스를 호출하여 답변 생성 (메시지와 히스토리 전달)
        NoteDto result = geminiService.summarize(request.getMessage(), request.getHistory());

        if (result == null) {
            log.error("AI 응답 생성 실패");
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(result);
    }

    // 요청 데이터를 담을 내부 DTO 클래스
    @Data
    public static class ChatRequest {
        private String message;
        private List<Map<String, Object>> history; // 대화 맥락 유지를 위한 리스트
    }

    // 노션 저장 트리거 API클라이언트에서 보낸 NoteDto 내용을 노션(Webhook)으로 전달.
    @PostMapping("/save")
    public ResponseEntity<String> saveToNotion(@RequestBody NoteDto noteDto) {

        log.info("노션 저장 요청 수신: {}", noteDto.getTitle());

        try {
            webhookService.sendToNotion(noteDto);
            return ResponseEntity.ok("노션에 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            log.error("노션 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("노션 저장에 실패했습니다.");
        }
    }

}