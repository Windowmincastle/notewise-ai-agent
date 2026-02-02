import { useState } from "react";
import type { ChatMessage, NoteDto } from "../../../types";
import { chatService } from "../services/chatService";

export const useChat = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([]); // 대화 내역
  const [isLoading, setIsLoading] = useState(false); // 로딩 상태

  const sendMessage = async (text: string): Promise<NoteDto | undefined> => {
    if (!text.trim()) return;

    // 1. 사용자의 메시지를 화면(상태)에 즉시 추가
    const userMsg: ChatMessage = { role: "user", parts: [{ text }] };
    setMessages((prev) => [...prev, userMsg]);
    setIsLoading(true);

    try {
      // 2. 백엔드 API 호출 (현재까지의 대화 이력을 포함해서 보냄)
      const data = await chatService.ask(text, messages);

      // 3. AI의 응답(NoteDto)을 메시지 형식으로 변환하여 추가
      const aiResponseText = `[TITLE]: ${data.title}\n\n${data.summary}`;
      const aiMsg: ChatMessage = {
        role: "model",
        parts: [{ text: aiResponseText }],
      };

      setMessages((prev) => [...prev, aiMsg]);
      return data; // 성공 시 NoteDto 반환 (나중에 노션 저장 버튼 등에 활용)
    } catch (error) {
      console.error("채팅 중 오류 발생:", error);
      alert(
        "교수님이 답변 중 사레가 걸리셨나 봐요. 잠시 후 다시 시도해 주세요.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  return { messages, sendMessage, isLoading };
};
