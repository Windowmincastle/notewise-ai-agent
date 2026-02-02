import React, { useState, useRef, useEffect } from "react";
import { useChat } from "../hooks/useChat";
import styles from "../styles/Chat.module.css";
import { Send } from "lucide-react";

export const ChatWindow = () => {
  const [inputText, setInputText] = useState("");
  const { messages, sendMessage, isLoading } = useChat();
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const handleSend = async () => {
    if (!inputText.trim() || isLoading) return;
    setInputText("");
    await sendMessage(inputText);
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const hasMessages = messages.length > 0;

  return (
    <div className={styles.container}>
      {!hasMessages && (
        <div className={styles.welcomeScreen}>
          <div className={styles.welcomeContent}>
            <h1 className={styles.welcomeTitle}>민성 님, 또 보니 반가워요.</h1>
            <p className={styles.welcomeSubtitle}>무엇이든 편하게 물어보세요</p>
          </div>
        </div>
      )}

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
                <div className={styles.messageText}>{msg.parts[0].text}</div>
              </div>
              {msg.role === "model" && !isLoading && (
                <button className={styles.actionButton}>Notion에 저장</button>
              )}
            </div>
          ))}
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
          <div ref={messagesEndRef} />
        </div>
      )}

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
