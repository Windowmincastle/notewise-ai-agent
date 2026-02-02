import client from '../../../api/client';
import type { NoteDto, ChatMessage, ChatRequest } from '../../../types';

export const chatService = {
  // 1. AI 교수님에게 질문하기
  ask: async (message: string, history: ChatMessage[]): Promise<NoteDto> => {
    const requestData: ChatRequest = { message, history };
    const response = await client.post<NoteDto>('/chat', requestData);
    return response.data;
  },

  // 2. 노션에 지식 노트 저장하기
  saveToNotion: async (note: NoteDto): Promise<string> => {
    const response = await client.post<string>('/chat/save', note);
    return response.data;
  }
};