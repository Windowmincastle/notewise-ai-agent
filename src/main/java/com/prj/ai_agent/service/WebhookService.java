package com.prj.ai_agent.service;

import com.prj.ai_agent.dto.NoteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WebhookService {

    @Value("${make.webhook.url}")
    private String webhookUrl;

    public void sendToNotion(NoteDto noteDto) {

        log.info("Make Webhook으로 전송 시도...");
        RestClient restClient = RestClient.create();
        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(noteDto)
                .retrieve()
                .toBodilessEntity();

        log.info("✅ 전송 완료! 노션을 확인하세요.");
    }
}
