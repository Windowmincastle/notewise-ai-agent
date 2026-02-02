import { useState } from "react";
import type { ChatMessage, NoteDto } from "../../../types";
import { chatService } from "../services/chatService";

export const useChat = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [questionHistory, setQuestionHistory] = useState<string[]>([]);

  const sendMessage = async (text: string): Promise<NoteDto | undefined> => {
    if (!text.trim()) return;

    // 질문 히스토리에 추가
    setQuestionHistory((prev) => [...prev, text]);

    // 사용자의 메시지를 화면(상태)에 즉시 추가
    const userMsg: ChatMessage = { role: "user", parts: [{ text }] };
    setMessages((prev) => [...prev, userMsg]);
    setIsLoading(true);

    try {
      const data = await chatService.ask(text, messages);

      // AI 응답 추가
      const aiMsg: ChatMessage = {
        role: "model",
        parts: [
          { text: data.summary }, // index 0: 화면에 보여줄 본문
          { text: data.title }, // index 1: 노션 저장 시 사용할 제목
        ],
      };

      setMessages((prev) => [...prev, aiMsg]);
      return data;
    } catch (error) {
      console.error("채팅 중 오류 발생:", error);
      alert("일시적 에러 발생. 잠시 후 다시 시도해 주세요.");
    } finally {
      setIsLoading(false);
    }
  };

  const saveNote = async (title: string, summary: string) => {
    try {
      const res = await chatService.saveToNotion({ title, summary });
      alert(res);
      return true;
    } catch (err) {
      console.error("노션 저장 실패", err);
      alert("노션 저장에 실패했습니다.");
      return false;
    }
  };

  return { messages, sendMessage, saveNote, isLoading, questionHistory };
};
