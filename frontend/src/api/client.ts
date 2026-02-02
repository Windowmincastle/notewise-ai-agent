import axios from "axios";

// 1. Axios 인스턴스 생성
const client = axios.create({
  // 백엔드 서버의 API 루트 주소 (Spring Boot 기본 포트)
  baseURL: "http://localhost:8080/api",

  // 모든 요청에 공통으로 들어갈 헤더 설정
  headers: {
    "Content-Type": "application/json",
  },

  // 요청 타임아웃 설정 (3분)
  timeout: 180000,
});

// 2. (선택) 인터셉터 설정 - 에러 공통 처리 등을 할 때 유용합니다.
client.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API 통신 오류:", error.response?.data || error.message);
    return Promise.reject(error);
  },
);

export default client;
