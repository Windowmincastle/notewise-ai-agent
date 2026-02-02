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

        // 🔥 Groq(OpenAI) 스타일의 프롬프트 구성
        String prompt = """
                너는 'IT 전문 지식인이자 친절한 기술 블로그 작가'야. 
                사용자의 질문에 대해 풍부한 내용을 담아 상세하고 친절하게 설명해줘.
                
                [사용자 질문]: %s
                
                   - 절대 한자(漢字)를 섞어 쓰지 마. (예: 任何 -> 어떤, 必須 -> 필수)
                [작성 지침 - 반드시 준수!]
                1. **언어 설정**: 반드시 한국어로만 답변해. 
                   - 기술 용어는 '한글(영어)' 형태로 작성해.
                2. **상세도**: 질문에 대해 최소 1000자 이상의 충분한 분량으로 상세하게 설명해. 
                   - "간단하게"라고 질문해도 전문가로서 깊이 있는 내용을 포함해줘.
                3. **구조화**: 
                   - [ 1. 개념 정의 및 배경 ]
                   - [ 2. 핵심 원리 및 상세 설명 ]
                   - [ 3. 실무 활용 사례 및 예시 코드 ]
                   - [ 4. 장단점 및 주의사항 ]
                   - [ 5. 한 줄 핵심 요약 ]
                4. **가독성**: 마크다운 기호(###, **)는 절대 쓰지 말고, [ 제목 ]과 줄바꿈으로만 구분해.
                
                ---형식 시작---
                [TITLE]
                (질문을 관통하는 매력적인 제목)
                
                [SUMMARY]
                (위의 지침 1~4번을 모두 반영한 상세한 본문 내용)
                ---형식 끝---
                """.formatted(userInput);

        // 🔥 Groq 전용 JSON 바디 구성 (OpenAI 호환 규격)
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7
        );

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey); // Groq은 Bearer 인증을 사용합니다.

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            return parseResponse(response);
        } catch (Exception e) {
            log.error("❌ Groq 호출 실패", e);
            return null;
        }
    }

    private NoteDto parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("choices").get(0)
                    .path("message").path("content").asText();

            log.info("📝 Groq 응답 수신 완료");

            String title = "";
            String summary = "";

            // 1. 제목 추출 시도 ([TITLE] 또는 [제목])
            if (text.contains("[TITLE]")) {
                title = extractTagValue(text, "[TITLE]", "["); // 다음 태그 전까지
            } else if (text.contains("[제목]")) {
                title = extractTagValue(text, "[제목]", "[");
            }

            // 2. 제목 이후의 모든 내용을 Summary로 취급
            // 제목 태그가 끝나는 지점을 찾습니다.
            int summaryStartIndex = -1;
            if (text.contains("[SUMMARY]")) {
                summaryStartIndex = text.indexOf("[SUMMARY]") + "[SUMMARY]".length();
            } else {
                // [제목]이나 [TITLE]이 끝나는 지점 다음부터 모두 본문으로 간주
                int titleIndex = text.indexOf("]");
                if (titleIndex != -1) {
                    summaryStartIndex = text.indexOf("\n", titleIndex);
                }
            }

            if (summaryStartIndex != -1 && summaryStartIndex < text.length()) {
                summary = text.substring(summaryStartIndex).trim();
            } else {
                summary = text; // 파싱 실패 시 전체 출력
            }

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