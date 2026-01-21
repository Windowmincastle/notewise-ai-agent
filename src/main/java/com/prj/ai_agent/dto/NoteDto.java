package com.prj.ai_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // 🔥 추가: public NoteDto() { } 생성
@AllArgsConstructor // 🔥 추가: 모든 필드를 받는 생성자 생성
@Data
@Builder
public class NoteDto {
    private String title;
    private String summary;
}