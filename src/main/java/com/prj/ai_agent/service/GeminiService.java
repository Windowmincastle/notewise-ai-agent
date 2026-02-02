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
//@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper;

    public NoteDto summarize(String userInput) {
        log.info("🤖 AI에게 지식 탐색 요청 중: {}", userInput);

        String requestUrl = apiUrl + "?key=" + apiKey;

        String prompt = """
                너는 '지식을 명쾌하게 전달하는 IT 전문가 혹은 교수'야. 
                사용자의 질문에 대해 군더더기 없이 깔끔하고 가독성 좋게 설명해줘.
                
                [사용자 질문]: %s
                
                [작성 지침]
                1. **톤앤매너**: 친절하고 차분한 어조로, 핵심 위주로 상세하게 설명해.
                2. **가독성 최우선**: 
                   - 특수 마크다운 기호(###, **)는 절대 사용하지 마.
                   - 섹션 구분은 `[ 제목 ]` 형태를 사용하고, 문단 사이에는 충분한 줄바꿈을 넣어.
                   - 복잡한 내용은 번호(1, 2, 3)나 기호(-)를 활용해 단계적으로 풀어서 써줘.
                3. **내용 구성**:
                   - 주제에 대한 명확한 정의
                   - 상세한 원리 및 특징 설명
                   - 실무 예시 또는 코드 예시 (필요한 경우 포함)
                   - 마지막에 핵심 내용을 한눈에 보기 좋게 요약
                
                ---형식 시작---
                [TITLE]
                (주제를 명확히 나타내는 제목)
                
                [SUMMARY]
                (위 지침을 준수한 본문 내용)
                ---형식 끝---
                """.formatted(userInput);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        try {
            RestClient restClient = RestClient.create();
            String response = restClient.post()
                    .uri(requestUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseResponse(response, userInput);
        } catch (Exception e) {
            log.error("Gemini 호출 실패", e);
            return null;
        }
    }

    // JSON 파싱 안 함! -> 직접 텍스트 자르기 (훨씬 튼튼함)
    private NoteDto parseResponse(String jsonResponse, String originalInput) {
        try {
            // 1. 구글 응답에서 'text' 알맹이만 꺼내기 (여기는 JSON 구조가 맞음)
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // 디버깅용 로그 (잘라내기 전 원본 텍스트 확인)
            log.info("📝 AI 원본 응답 텍스트:\n{}", text);

            // 2. [태그]를 기준으로 데이터 잘라내기
            String title = extractTagValue(text, "[TITLE]", "[SUMMARY]");
            String summary = extractTagValue(text, "[SUMMARY]", null);

            // 3. 데이터 다듬기 (공백 제거 등)
            if (title.isEmpty()) title = "제목 없음";
            if (summary.isEmpty()) summary = "요약 내용을 찾을 수 없습니다.";



            // 4. DTO 생성 및 반환
            NoteDto noteDto = new NoteDto();
            noteDto.setTitle(title);
            noteDto.setSummary(summary);

            return noteDto;

        } catch (Exception e) {
            log.error("❌ 데이터 추출 중 에러 발생", e);
            return null;
        }
    }

    // 텍스트 사이의 내용을 발라내는 도우미 메서드
    private String extractTagValue(String text, String startTag, String endTag) {
        try {
            int startIndex = text.indexOf(startTag);
            if (startIndex == -1) return "";

            startIndex += startTag.length();

            int endIndex;
            if (endTag != null) {
                endIndex = text.indexOf(endTag, startIndex);
            } else {
                endIndex = text.length(); // 끝 태그가 없으면 끝까지
            }

            if (endIndex == -1) endIndex = text.length();

            return text.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "";
        }
    }

}