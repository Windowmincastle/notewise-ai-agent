import { ChatWindow } from "./features/chat/components/ChatWindow";
import styles from "./App.module.css";
import "./styles/theme.css";

function App() {
  return (
    <div className={styles.layout}>
      <main className={styles.main}>
        <ChatWindow />
      </main>
    </div>
  );
}

export default App;
