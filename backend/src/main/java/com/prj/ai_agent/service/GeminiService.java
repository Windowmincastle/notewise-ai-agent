package com.prj.ai_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.ai_agent.config.PromptConstants;
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

        // System Instruction (Persona)
        String systemPrompt = PromptConstants.PROFESSOR_SYSTEM_PROMPT;

        // User Prompt 구성 및 대화 이력(chatHistory)에 추가
        String userPrompt = "Professor, please explain this topic: " + userInput;
        // 현재 질문을 대화 이력에 삽입.
        chatHistory.add(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt))));
        // JSON Body Construction (전체 chatHistory를 contents에 넣음)
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

            // AI 답변을 이력에 추가하기 위해 텍스트만 먼저 추출
            JsonNode root = objectMapper.readTree(response);
            String aiText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // 모델의 답변도 대화 이력에 저장 (다음 대화의 맥락이 됨)
            chatHistory.add(Map.of("role", "model", "parts", List.of(Map.of("text", aiText))));

            // 파싱 메서드 호출하여 NoteDto 반환
            return parseResponse(response, userInput);

        } catch (Exception e) {
            log.error("Gemini API Call Failed", e);
            return null;
        }
    }

    private NoteDto parseResponse(String jsonResponse, String originalInput) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // 정규표현식을 사용하여 태그 사이의 내용을 더 유연하게 추출
            String title = "";
            String summary = "";

            if (text.contains("[TITLE]")) {
                int titleStart = text.indexOf("[TITLE]") + "[TITLE]".length();
                int titleEnd = text.contains("[SUMMARY]") ? text.indexOf("[SUMMARY]") : text.length();
                title = text.substring(titleStart, titleEnd).trim();
            }

            if (text.contains("[SUMMARY]")) {
                int summaryStart = text.indexOf("[SUMMARY]") + "[SUMMARY]".length();
                summary = text.substring(summaryStart).trim();
            }

            // 만약 AI가 태그를 아예 안 줬을 경우를 대비한 방어 로직
            if (title.isEmpty()) {
                // 질문의 앞부분 10글자를 제목으로 자동 생성
                title = originalInput.length() > 15 ? originalInput.substring(0, 15) + "..." : originalInput;
            }

            if (summary.isEmpty()) {
                // 태그가 없으면 전체 텍스트를 본문으로 간주
                summary = text.replace("[TITLE]", "").replace("[SUMMARY]", "").trim();
            }

            NoteDto noteDto = new NoteDto();
            noteDto.setTitle(title);
            noteDto.setSummary(summary);

            return noteDto;
        } catch (Exception e) {
            log.error("파싱 실패", e);
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