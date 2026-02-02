import client from "../../../api/client";
import type { NoteDto, ChatMessage, ChatRequest } from "../../../types";

export const chatService = {
  ask: async (message: string, history: ChatMessage[]): Promise<NoteDto> => {
    const requestData: ChatRequest = { message, history };
    const response = await client.post<NoteDto>("/chat", requestData);
    return response.data;
  },

  saveToNotion: async (note: NoteDto): Promise<string> => {
    const response = await client.post<string>("/chat/save", note);
    return response.data;
  },
};
