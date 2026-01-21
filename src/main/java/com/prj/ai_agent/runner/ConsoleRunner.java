package com.prj.ai_agent.runner;

import com.prj.ai_agent.dto.NoteDto;
import com.prj.ai_agent.service.GeminiService;
import com.prj.ai_agent.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {

    private final GeminiService geminiService;
    private final WebhookService webhookService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n============================================");
        System.out.println("   🤖 AI 지식 비서 (검토 후 저장 모드)   ");
        System.out.println("============================================");

        while (true) {
            // 1. 질문 입력 받기
            System.out.print("\n🙋 질문/입력 (종료: q) >> ");
            String input = scanner.nextLine();

            if ("q".equalsIgnoreCase(input.trim())) {
                System.out.println("👋 프로그램을 종료합니다.");
                System.exit(0);
            }

            if (input.trim().isEmpty()) continue;

            try {
                // 2. AI에게 답변 받아오기
                NoteDto result = geminiService.summarize(input);

                if (result != null) {
                    // 3. 터미널에 먼저 출력해서 확인시켜주기 (검토 단계)
                    System.out.println("\n--------------------------------------------------");
                    System.out.println("📢 [AI 답변]");
                    System.out.println("제목: " + result.getTitle());
                    System.out.println("내용:\n" + result.getSummary()); // AI가 준 답변 전체 출력
                    System.out.println("--------------------------------------------------");

                    // 4. 저장 여부 묻기
                    System.out.print("💾 위 내용을 노션에 저장하시겠습니까? (y/n) >> ");
                    String saveChoice = scanner.nextLine();

                    if ("y".equalsIgnoreCase(saveChoice.trim())) {
                        // 'y'를 눌렀을 때만 웹훅 발사!
                        webhookService.sendToNotion(result);
                        System.out.println("✅ 저장 완료! 다음 질문을 주세요.");
                    } else {
                        // 'n' 또는 다른 키를 누르면 패스
                        System.out.println("❌ 저장하지 않고 넘어갑니다.");
                    }

                } else {
                    System.out.println("⚠️ AI가 응답하지 않았습니다.");
                }

            } catch (Exception e) {
                System.out.println("❌ 에러 발생: " + e.getMessage());
            }
        }
    }
}