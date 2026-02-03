import axios from "axios";

const client = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
  // 요청 타임아웃 설정 (3분)
  timeout: 180000,
});

// 인터셉터 설정.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API 통신 오류:", error.response?.data || error.message);
    return Promise.reject(error);
  },
);

export default client;
