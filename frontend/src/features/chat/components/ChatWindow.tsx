import React, { useState, useRef, useEffect } from "react";
import { useChat } from "../hooks/useChat";
import styles from "../styles/Chat.module.css";
import { Send, Mic } from "lucide-react"; // 아이콘 임포트

export const ChatWindow = () => {
  const [inputText, setInputText] = useState("");
  const { messages, sendMessage, isLoading } = useChat();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const handleSend = async () => {
    if (!inputText.trim() || isLoading) return;
    setInputText("");
    await sendMessage(inputText);
  };

  // 메시지가 추가될 때마다 스크롤 하단으로 이동
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const hasMessages = messages.length > 0;

  return (
    <div className={styles.container}>
      {/* 1. 초기 환영 화면 (메시지가 없을 때) */}
      {!hasMessages && (
        <div className={styles.welcomeScreen}>
          <h1 className={styles.welcomeTitle}>민성 님, 또 보니 반가워요.</h1>
        </div>
      )}

      {/* 2. 메시지 리스트 (메시지가 있을 때) */}
      {hasMessages && (
        <div className={styles.messageList}>
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`${styles.messageRow} ${msg.role === "user" ? styles.userRow : styles.aiRow}`}
            >
              <div className={styles.messageContent}>
                <div className={styles.avatar}>
                  {msg.role === "user" ? "👤" : "🤖"}
                </div>
                <div className={styles.text}>{msg.parts[0].text}</div>
              </div>
              {/* 노션 저장 버튼 (AI 응답일 때만) */}
              {msg.role === "model" && !isLoading && (
                <button className={styles.notionButton}>🚀 Notion 저장</button>
              )}
            </div>
          ))}
          {isLoading && (
            <div className={styles.messageRow}>
              <div className={styles.messageContent}>
                <div className={styles.avatar}>🤖</div>
                <div className={styles.text}>...</div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>
      )}

      {/* 3. 하단 입력창 영역 (항상 표시) */}
      <div className={styles.inputContainer}>
        <div className={styles.inputWrapper}>
          <input
            className={styles.input}
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSend()}
            placeholder="무엇이든 물어보세요"
            disabled={isLoading}
          />
          <button className={styles.iconButton}>
            <Mic size={20} />
          </button>
          <button
            className={styles.sendButton}
            onClick={handleSend}
            disabled={isLoading || !inputText.trim()}
          >
            <Send size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};
