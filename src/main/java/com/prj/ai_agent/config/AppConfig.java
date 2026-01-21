package com.prj.ai_agent.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // "ObjectMapper라는 도구를 내가 미리 세팅해둘 테니, 필요한 곳(Service)에서 가져다 써라(@RequiredArgsConstructor)"
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 혹시 Gemini가 우리가 모르는 이상한 필드를 줘도 에러 내지 말고 무시해라! (안전장치)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
