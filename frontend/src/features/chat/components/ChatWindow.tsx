import React, { useState, useRef, useEffect } from "react";
import { useChat } from "../hooks/useChat"; // saveNote가 추가된 useChat 훅
import styles from "../styles/Chat.module.css";
import { Send } from "lucide-react"; // 아이콘 라이브러리
import type { ChatMessage } from "../../../types";

export const ChatWindow = () => {
  const [inputText, setInputText] = useState("");
  // useChat 훅에서 saveNote 함수도 함께 가져옵니다.
  const { messages, sendMessage, saveNote, isLoading } = useChat();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 메시지 전송 핸들러
  const handleSend = async () => {
    if (!inputText.trim() || isLoading) return;
    const text = inputText; // 입력된 텍스트 임시 저장
    setInputText(""); // 입력창 비우기
    await sendMessage(text);
  };

  const handleSave = async (msg: ChatMessage) => {
    // parts[1]에 숨겨둔 제목을 쓰고, 없으면 방어 로직으로 처리
    const title = msg.parts[1]?.text || "제목 없음";
    const summary = msg.parts[0].text;

    if (!summary) {
      alert("저장할 내용이 없습니다.");
      return;
    }

    await saveNote(title, summary);
  };

  // 메시지가 추가될 때마다 스크롤을 맨 아래로 내림
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const hasMessages = messages.length > 0;

  return (
    <div className={styles.container}>
      {/* 1. 환영 화면 (메시지가 없을 때만 표시) */}
      {!hasMessages && (
        <div className={styles.welcomeScreen}>
          <div className={styles.welcomeContent}>
            <h1 className={styles.welcomeTitle}>민성 님, 또 보니 반가워요.</h1>
            <p className={styles.welcomeSubtitle}>무엇이든 편하게 물어보세요</p>
          </div>
        </div>
      )}

      {/* 2. 대화 리스트 영역 */}
      {hasMessages && (
        <div className={styles.messageList}>
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`${styles.messageWrapper} ${
                msg.role === "user"
                  ? styles.userMessage
                  : styles.assistantMessage
              }`}
            >
              <div className={styles.messageBubble}>
                {/* 줄바꿈을 반영하여 텍스트 렌더링 */}
                <div className={styles.messageText}>{msg.parts[0].text}</div>
              </div>

              {/* ✅ AI 메시지일 때만 [Notion에 저장] 버튼 표시 */}
              {msg.role === "model" && !isLoading && (
                <button
                  className={styles.actionButton}
                  onClick={() => handleSave(msg)}
                >
                  Notion에 저장
                </button>
              )}
            </div>
          ))}

          {/* 로딩 표시 (Typing Indicator) */}
          {isLoading && (
            <div
              className={`${styles.messageWrapper} ${styles.assistantMessage}`}
            >
              <div className={styles.messageBubble}>
                <div className={styles.typingIndicator}>
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
          {/* 스크롤 자동 이동을 위한 빈 div */}
          <div ref={messagesEndRef} />
        </div>
      )}

      {/* 3. 하단 입력창 영역 */}
      <div className={styles.inputContainer}>
        <div className={styles.inputWrapper}>
          <input
            className={styles.input}
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSend()}
            placeholder="메시지를 입력하세요..."
            disabled={isLoading}
          />
          <button
            className={styles.sendButton}
            onClick={handleSend}
            disabled={isLoading || !inputText.trim()}
          >
            <Send size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};
