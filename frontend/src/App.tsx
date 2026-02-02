import { ChatWindow } from "./features/chat/components/ChatWindow";
import styles from "./App.module.css";
import "./styles/theme.css"; // 테마 파일 임포트

function App() {
  return (
    <div className={styles.layout}>
      <aside className={styles.sidebar}>
        {/* 추후 대화 기록 리스트 등이 들어갈 공간 */}
        <div style={{ marginBottom: "20px", fontWeight: "bold" }}>
          {" "}
          + 새로운 대화
        </div>
      </aside>
      <main className={styles.main}>
        <ChatWindow />
      </main>
    </div>
  );
}

export default App;
