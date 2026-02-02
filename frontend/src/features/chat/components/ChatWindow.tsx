import React, { useState, useRef, useEffect } from "react";
import { useChat } from "../hooks/useChat";
import styles from "../styles/Chat.module.css";
import { Send } from "lucide-react";
import type { ChatMessage } from "../../../types";

export const ChatWindow = () => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [inputText, setInputText] = useState("");
  const { messages, sendMessage, saveNote, isLoading, questionHistory } =
    useChat();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 메시지 전송 핸들러
  const handleSend = async () => {
    if (!inputText.trim() || isLoading) return;
    const text = inputText;
    setInputText("");
    await sendMessage(text);
  };

  // handleSend 함수 아래에 추가
  const handleTextareaChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInputText(e.target.value);
    // 자동 높이 조절
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
      textareaRef.current.style.height =
        textareaRef.current.scrollHeight + "px";
    }
  };

  const handleSave = async (msg: ChatMessage) => {
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
      {/* 사이드바 - 질문 히스토리 */}
      <div className={styles.sidebar}>
        <div className={styles.sidebarHeader}>
          <h2 className={styles.sidebarTitle}>질문 기록</h2>
        </div>
        <div className={styles.historyList}>
          {questionHistory.length === 0 ? (
            <div className={styles.emptyHistory}>
              <p>아직 대화가 없습니다</p>
            </div>
          ) : (
            questionHistory.map((question, index) => (
              <div key={index} className={styles.historyCard}>
                <div className={styles.historyText}>{question}</div>
                <div className={styles.historyTime}>
                  {new Date().toLocaleTimeString("ko-KR", {
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* 메인 채팅 영역 */}
      <div className={styles.chatArea}>
        {/* 환영 화면 */}
        {!hasMessages && (
          <div className={styles.welcomeScreen}>
            <div className={styles.welcomeContent}>
              <h1 className={styles.welcomeTitle}>
                민성 님, 또 보니 반가워요.
              </h1>
              <p className={styles.welcomeSubtitle}>
                오늘은 무엇을 학습하고 정리할까요?
              </p>
            </div>
          </div>
        )}

        {/* 대화 리스트 영역 */}
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
                {/* AI 메시지일 때 아바타 표시 */}
                {msg.role === "model" && (
                  <div className={styles.avatar}>
                    <img
                      src="/cute-robot3.png"
                      alt="AI"
                      className={styles.avatarImage}
                    />
                  </div>
                )}

                <div className={styles.messageContent}>
                  <div className={styles.messageBubble}>
                    <div className={styles.messageText}>
                      {msg.parts[0].text}
                    </div>
                  </div>

                  {/* AI 메시지일 때만 [Notion에 저장] 버튼 표시 */}
                  {msg.role === "model" && !isLoading && (
                    <button
                      className={styles.actionButton}
                      onClick={() => handleSave(msg)}
                    >
                      Notion에 저장
                    </button>
                  )}
                </div>
              </div>
            ))}

            {/* 로딩 표시 */}
            {isLoading && (
              <div
                className={`${styles.messageWrapper} ${styles.assistantMessage}`}
              >
                <div className={styles.avatar}>
                  <img
                    src="/cute-robot3.png"
                    alt="AI"
                    className={styles.avatarImage}
                  />
                </div>
                <div className={styles.messageContent}>
                  <div className={styles.messageBubble}>
                    <div className={styles.typingIndicator}>
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>
        )}

        {/* 하단 입력창 영역 */}
        <div className={styles.inputContainer}>
          <div className={styles.inputWrapper}>
            <textarea
              ref={textareaRef}
              className={styles.input}
              value={inputText}
              onChange={handleTextareaChange}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !e.shiftKey) {
                  e.preventDefault();
                  handleSend();
                }
              }}
              placeholder="메시지를 입력하세요..."
              disabled={isLoading}
              rows={1}
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
    </div>
  );
};
