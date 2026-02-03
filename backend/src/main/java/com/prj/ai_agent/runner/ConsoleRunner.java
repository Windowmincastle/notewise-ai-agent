package com.prj.ai_agent.runner;

import com.prj.ai_agent.dto.NoteDto;
import com.prj.ai_agent.service.GeminiService;
import com.prj.ai_agent.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {

    private final GeminiService geminiService;
    private final WebhookService webhookService;

    // 대화 맥락을 저장할 메모리 리스트 (최근 5턴 = 메시지 10개 유지)
    private final List<Map<String, Object>> conversationContext = new ArrayList<>();

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n============================================");
        System.out.println("AI CHAT 상태 (대화 맥락 유지 모드)   ");
        System.out.println("============================================");

        while (true) {
            // 1. 질문 입력 받기
            System.out.print("\n질문/입력 (종료: q) >> ");
            String input = scanner.nextLine();

            if ("q".equalsIgnoreCase(input.trim())) {
                System.out.println("프로그램을 종료.");
                System.exit(0);
            }

            if (input.trim().isEmpty()) continue;

            try {
                // 대화 이력이 10개(5턴) 이상이면 가장 오래된 '질문-답변' 쌍을 제거
                // summarize 내부에서 유저 질문 1개, AI 답변 1개가 추가되므로 10개일 때 미리 2개를 비우기.
                while (conversationContext.size() >= 10) {
                    conversationContext.remove(0); // 가장 오래된 유저 질문 삭제
                    conversationContext.remove(0); // 그에 대한 AI 답변 삭제
                    log.info("오래된 대화 맥락을 정리 완료. (최근 5턴 유지)");
                }

                // AI에게 답변 받아오기 (현재 질문과 이전 대화 맥락 전달)
                NoteDto result = geminiService.summarize(input, conversationContext);

                if (result != null) {
                    // 터미널 출력 및 검토
                    System.out.println("\n--------------------------------------------------");
                    System.out.println("📢 [AI 교수님 강의 내용]");
                    System.out.println("제목: " + result.getTitle());
                    System.out.println("내용:\n" + result.getSummary());
                    System.out.println("--------------------------------------------------");

                    // 저장 여부 묻기
                    System.out.print("💾 위 내용을 노션에 저장하시겠습니까? (y/n) >> ");
                    String saveChoice = scanner.nextLine();

                    if ("y".equalsIgnoreCase(saveChoice.trim())) {
                        webhookService.sendToNotion(result);
                        System.out.println("노션 저장 완료! 다음 질문을 입력하세요.");
                    } else {
                        System.out.println("저장하지 않았습니다. 대화를 계속 이어갈 수 있습니다.");
                    }

                } else {
                    System.out.println("AI가 응답하지 않았습니다.");
                }

            } catch (Exception e) {
                log.error("처리 중 오류 발생", e);
                System.out.println("에러 발생: " + e.getMessage());
            }
        }
    }
}