package com.prj.ai_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prj.ai_agent.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final ObjectMapper objectMapper;

    public NoteDto summarize(String userInput) {
        log.info("🚀 Groq(Llama 3)에게 지식 탐색 요청 중: {}", userInput);

        // 1. System Message: 페르소나 + 언어 규칙 + 출력 구조 정의
        String systemPrompt = """
            너는 '한국어 기술 블로그 작가'이자 'IT 전문 지식인'이다.
            
            [핵심 규칙]
            1. 모든 답변은 오직 '한국어'로만 작성하며 한자(漢字)는 절대 사용하지 않는다.
            2. 기술 용어는 '한글(영어)' 형태로 작성한다.
            3. 답변은 다음 4가지 섹션을 반드시 포함하여 상세히 작성한다:
               - [ 1. 개념 정의 ] / [ 2. 상세 설명 ] / [ 3. 실무 사례 ] / [ 4. 핵심 요약 ]
            4. 마크다운 기호(###, **)는 절대 사용하지 말고 [ 제목 ]과 줄바꿈으로만 가독성을 높인다.

            [출력 형식 가이드]
            답변 시 아래의 형식을 엄격히 준수할 것:
            ---
            [TITLE]
            (여기에 주제 제목 작성)
            
            [SUMMARY]
            (여기에 1~4번 섹션을 포함한 상세 본문 작성)
            ---
            """;

        // 2. User Message: 순수하게 질문 내용만 전달
        // 이제 User Role은 "이 질문에 대해 알려줘"라는 트리거 역할만 수행합니다.
        String userPrompt = "사용자 질문: " + userInput;

        // 🔥 Groq 요청 바디 구성 (system 역할 추가 및 temperature 하향)
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.3 // 0.7에서 0.3으로 낮춰 창의성보다는 정확도와 규칙 준수에 집중
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            return parseResponse(response);
        } catch (Exception e) {
            log.error("❌ Groq 호출 실패", e);
            return null;
        }
    }

    // 기존 parseResponse 및 extractTagValue 로직은 동일하게 유지
    private NoteDto parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("choices").get(0)
                    .path("message").path("content").asText();

            log.info("📝 Groq 응답 수신 완료");

            String title = extractTagValue(text, "[TITLE]", "[SUMMARY]");
            String summary = extractTagValue(text, "[SUMMARY]", null);

            return new NoteDto(
                    title.isEmpty() ? "요약 노트" : title,
                    summary.isEmpty() ? text : summary
            );

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
        } catch (Exception e) {
            return "";
        }
    }
}