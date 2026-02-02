//package com.prj.ai_agent.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.prj.ai_agent.dto.NoteDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ChatGptService {
//
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    @Value("${openai.api.url}")
//    private String apiUrl;
//
//    @Value("${openai.model}")
//    private String model;
//
//    private final ObjectMapper objectMapper;
//
//    public NoteDto summarize(String userInput) {
//        log.info("🚀 GPT-4o-mini에게 지식 탐색 요청 중: {}", userInput);
//
//        String systemPrompt = """
//                너는 '대한민국 최고의 IT 지식 큐레이터'야.
//                사용자의 질문에 대해 정확하고 풍부한 내용을 상세하게 설명해줘.
//
//                [필수 규칙]
//                1. 반드시 한국어로만 답변하고, 한자(漢字)나 일본어 조사는 절대 사용하지 마.
//                2. 기술 용어는 '한글(영어)' 형태로 작성해.
//                3. 내용은 최소 1,500자 이상 전문가 수준으로 상세히 풀어줘.
//                """;
//
//        String userPrompt = """
//                [사용자 질문]: %s
//
//                [작성 지침]
//                - 섹션 구분: [ 1. 개념 정의 ] / [ 2. 상세 설명 ] / [ 3. 실무 사례 ] / [ 4. 핵심 요약 ]
//                - 형식 준수: 아래 [TITLE]과 [SUMMARY] 태그를 반드시 포함할 것.
//
//                ---형식 시작---
//                [TITLE]
//                (주제 제목)
//
//                [SUMMARY]
//                (본문 내용)
//                ---형식 끝---
//                """.formatted(userInput);
//
//        Map<String, Object> requestBody = Map.of(
//                "model", model,
//                "messages", List.of(
//                        Map.of("role", "system", "content", systemPrompt),
//                        Map.of("role", "user", "content", userPrompt)
//                ),
//                "temperature", 0.3
//        );
//
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(apiKey);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//            String response = restTemplate.postForObject(apiUrl, entity, String.class);
//
//            return parseResponse(response);
//        } catch (Exception e) {
//            log.error("❌ OpenAI 호출 실패", e);
//            return null;
//        }
//    }
//
//    private NoteDto parseResponse(String jsonResponse) {
//        try {
//            JsonNode root = objectMapper.readTree(jsonResponse);
//            String text = root.path("choices").get(0).path("message").path("content").asText();
//
//            String title = extractTagValue(text, "[TITLE]", "[SUMMARY]");
//            String summary = extractTagValue(text, "[SUMMARY]", null);
//
//            return new NoteDto(title.isEmpty() ? "지식 노트" : title, summary.isEmpty() ? text : summary);
//        } catch (Exception e) {
//            log.error("❌ 파싱 에러", e);
//            return null;
//        }
//    }
//
//    private String extractTagValue(String text, String startTag, String endTag) {
//        try {
//            int startIndex = text.indexOf(startTag);
//            if (startIndex == -1) return "";
//            startIndex += startTag.length();
//            int endIndex = (endTag != null) ? text.indexOf(endTag, startIndex) : text.length();
//            return text.substring(startIndex, endIndex).trim();
//        } catch (Exception e) { return ""; }
//    }
//}