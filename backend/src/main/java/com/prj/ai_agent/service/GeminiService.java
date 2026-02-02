package com.prj.ai_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.ai_agent.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper;

    public NoteDto summarize(String userInput, List<Map<String, Object>> chatHistory) {
        log.info("🤖 Gemini 2.5 Flash-Lite (Context Mode) - Input: {}", userInput);

        String requestUrl = apiUrl + "?key=" + apiKey;

        // 1. System Instruction (Professor Persona)
        String systemPrompt = """
            [Persona]
            You are a highly experienced IT Expert and a dedicated Professor. 
            Your mission is to guide students and knowledge seekers by providing kind, 
            encouraging, and very detailed explanations. 
            You excel at breaking down complex concepts into easy-to-understand pedagogical lessons.

            [Strict Language Rules]
            1. Output Language: Write the final response in KOREAN.
            2. Language Purity: NEVER use Chinese characters (Hanja) or Japanese particles (e.g., つ의).
            3. Technical Terms: Use the format 'Korean(English)'. Example: 가상화(Virtualization).
            4. Tone: Kind, academic yet accessible, and professional. 
            5. No Markdown: Avoid symbols like '###' or '**'. Use [ Title ] and line breaks instead.
            
                [Response Structure & Format]
                Every response MUST follow this structure:
                - [ 1. 개념 정의 및 배경 ] / [ 2. 핵심 원리 ] / [ 3. 실무 사례 ] / [ 4. 핵심 요약 ]
                
                You MUST wrap your response with these tags for parsing:
                [TITLE]
                (Catchy Title in Korean)
                [SUMMARY]
                (Detailed body content in Korean)
            """;

        // 2. User Prompt 구성 및 대화 이력(chatHistory)에 추가
        String userPrompt = "Professor, please explain this topic: " + userInput;
        // 현재 질문을 대화 이력에 넣습니다.
        chatHistory.add(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt))));

        // 3. JSON Body Construction (전체 chatHistory를 contents에 넣음)
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))
                ),
                "contents", chatHistory
        );

        try {
            RestClient restClient = RestClient.create();
            String response = restClient.post()
                    .uri(requestUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // 4. AI 답변을 이력에 추가하기 위해 텍스트만 먼저 추출
            JsonNode root = objectMapper.readTree(response);
            String aiText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // 모델의 답변도 대화 이력에 저장 (다음 대화의 맥락이 됨)
            chatHistory.add(Map.of("role", "model", "parts", List.of(Map.of("text", aiText))));

            // 5. 기존 파싱 메서드 호출하여 NoteDto 반환
            return parseResponse(response, userInput);

        } catch (Exception e) {
            log.error("❌ Gemini API Call Failed", e);
            return null;
        }
    }

    // 기존 파싱 로직 (질문자님 코드 그대로 유지)
    private NoteDto parseResponse(String jsonResponse, String originalInput) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            String title = extractTagValue(text, "[TITLE]", "[SUMMARY]");
            String summary = extractTagValue(text, "[SUMMARY]", null);

            if (title.isEmpty()) title = "제목 없음";
            if (summary.isEmpty()) summary = "요약 내용을 찾을 수 없습니다.";

            NoteDto noteDto = new NoteDto();
            noteDto.setTitle(title);
            noteDto.setSummary(summary);

            return noteDto;
        } catch (Exception e) {
            log.error("❌ 데이터 추출 중 에러 발생", e);
            return null;
        }
    }

    private String extractTagValue(String text, String startTag, String endTag) {
        try {
            int startIndex = text.indexOf(startTag);
            if (startIndex == -1) return "";
            startIndex += startTag.length();
            int endIndex = (endTag != null) ? text.indexOf(endTag, startIndex) : text.length();
            if (endIndex == -1) endIndex = text.length();
            return text.substring(startIndex, endIndex).trim();
        } catch (Exception e) { return ""; }
    }
}