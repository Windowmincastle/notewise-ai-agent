export interface NoteDto {
  title: string;
  summary: string;
}

export interface ChatMessage {
  role: "user" | "model";
  parts: { text: string }[];
}

export interface ChatRequest {
  message: string;
  history: ChatMessage[];
}
