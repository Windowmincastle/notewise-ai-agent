# 📝 NoteWise
### : AI Agentic Workflow for Automated Knowledge Archiving

> **"Brain(AI) needs Body(Server) to act."**
> **NoteWise**는 Gemini AI와 Spring Boot, Make 자동화를 결합하여, 대화 내용을 Notion 지식 베이스로 즉시 구조화하는 **AI 에이전트 서비스**입니다.

<br/>

## 📢 프로젝트 소개

### 1. 개발 배경 (Why NoteWise?)
평소 개발 학습과 프로젝트를 진행하며 하루에도 수십 번씩 AI와 기술적인 토의를 합니다. 하지만 이 과정에서 두 가지 결정적인 한계를 느꼈습니다.

1.  **"복사 & 붙여넣기의 비효율 (Inefficiency)"**: AI가 생성한 통찰력 있는 코드나 개념 설명을 다시 Notion에 옮겨 적는 과정이 너무 번거로웠고, 귀찮음으로 인해 유실되는 지식(Missing Context)이 너무 많았습니다.
2.  **"AI의 행동 제약 (Action Gap)"**: LLM(Gemini)은 똑똑한 '뇌'를 가졌지만, 스스로 외부 시스템(Notion)에 데이터를 저장하는 '손발'이 없습니다. "이거 저장해줘"라고 말하면 실제로 저장하는 것이 아니라, 텍스트로만 대답할 뿐입니다.

### 2. 프로젝트 목표
**NoteWise**는 이러한 간극을 메우기 위해 시작되었습니다.
* **Knowledge Archiving Automation**: 사용자가 AI와 대화하는 것만으로, 학습 내용과 개발 산출물이 자동으로 분류되어 Notion 데이터베이스에 쌓입니다.
* **Bridge the Gap**: Spring Boot 서버를 구축하여 AI의 텍스트 응답을 실질적인 '데이터'로 변환하고, Make(Webhook)를 통해 Notion API를 트리거합니다.
* **Context Continuity**: 단발성 질문이 아닌, 대화의 맥락을 유지하며 지식을 고도화할 수 있는 환경을 제공합니다.

> **Note**: 본 프로젝트는 RAG(검색 증강 생성) 구축 전 단계로, **데이터의 자동 적재(Archiving)**와 **에이전틱 워크플로우(Agentic Workflow)** 구현에 초점을 맞추었습니다.

<br/>

## ⚙️ 시스템 아키텍처

![System Architecture](여기에_아키텍처_이미지_경로를_넣어주세요)

1.  **User Input (React)**: 사용자가 NoteWise 채팅 인터페이스를 통해 질문하거나 저장을 요청합니다.
2.  **Context Management (Spring Boot)**: 서버는 Sliding Window 기법으로 대화 맥락을 최적화하여 Gemini API에 전달합니다.
3.  **Intelligence (Gemini API)**: AI는 사용자 의도를 파악하고, Notion에 저장하기 적합한 형태(JSON/Markdown)로 내용을 구조화합니다.
4.  **Action Trigger (Server -> Make)**: 서버는 AI의 응답을 감지하고, 사전에 정의된 **Make Webhook**으로 데이터를 전송합니다.
5.  **Automated Archiving (Make -> Notion)**: Make 워크플로우가 실행되며 Notion API를 통해 데이터베이스에 페이지를 자동 생성합니다.

<br/>

## 🛠 기술 스택 (Tech Stack)

### Backend
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.9-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC0230?style=for-the-badge&logo=lombok&logoColor=white)

### Frontend
![React](https://img.shields.io/badge/React_19-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript_5.9-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite_7.2-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)
![Lucide React](https://img.shields.io/badge/Lucide_React-F7DF1E?style=for-the-badge&logo=lucide&logoColor=black)

### AI & Automation
![Gemini](https://img.shields.io/badge/Google_Gemini_API-8E75B2?style=for-the-badge&logo=google-gemini&logoColor=white)
![Make](https://img.shields.io/badge/Make_(Integromat)-000000?style=for-the-badge&logo=make&logoColor=white)
![Notion](https://img.shields.io/badge/Notion_API-000000?style=for-the-badge&logo=notion&logoColor=white)

<br/>

## 📌 주요 기능

| 기능 | 설명 |
| :--- | :--- |
| **💬 Smart Chat Interface** | React 19 기반의 부드러운 채팅 UI. Sliding Window 기법을 적용하여 토큰 비용을 아끼면서도 대화의 맥락을 기억합니다. |
| **⚡ Notion Webhook Trigger** | 핵심 기능. AI가 정리한 내용을 서버가 받아 Make의 Webhook URL로 전송, Notion DB에 즉시 저장합니다. |
| **🧠 Archivist Persona** | AI에게 단순 챗봇이 아닌 '전문 지식 관리자' 페르소나를 부여하여, 나중에 찾아보기 쉬운 형태(요약, 태그 등)로 답변하도록 튜닝했습니다. |
| **🔄 End-to-End Workflow** | `질문 -> 분석 -> 구조화 -> 적재`의 전 과정을 사용자의 추가 개입 없이 한 번의 대화로 완료합니다. |

<br/>

## 💡 기술적 도전 및 회고 (Technical Retrospective)

### 1. AI에게 '행동력' 부여하기 (From Chatbot to Agent)
가장 큰 고민은 "AI는 텍스트만 뱉을 뿐, 행동하지 않는다"는 점이었습니다. AI가 아무리 완벽한 요약본을 만들어도, 웹훅을 쏘는 기능은 없기 때문입니다.
* **해결:** 백엔드 서버를 AI의 '신체(Body)'로 정의했습니다. AI가 반환한 데이터를 서버가 가로채서(Intercept), **Make Webhook**으로 `POST` 요청을 보내는 구조를 설계했습니다. 이를 통해 **"AI 모델(Brain) + 백엔드 로직(Body) = 에이전트(Agent)"**가 작동하는 원리를 직접 구현하고 이해했습니다.

### 2. Context 유지와 비용 최적화 (Sliding Window)
API를 사용할 때 전체 대화 내역을 매번 전송하는 것은 비용과 속도 면에서 비효율적이었습니다.
* **해결:** Java의 `List` 자료구조를 활용해 최근 N개의 대화 턴(Turn)만 유지하는 **Sliding Window** 알고리즘을 적용했습니다. 이를 통해 현재 주제에 대한 논리적 연속성은 유지하면서도 시스템 리소스를 효율적으로 관리했습니다.

### 3. 데이터 자산화 (Knowledge Archiving)
기존에는 휘발되던 개발 고민과 문제 해결 과정들이 NoteWise를 통해 자동으로 축적되기 시작했습니다.
* **성과:** 별도의 정리 시간 없이도 Notion에 쌓인 데이터들은 향후 저만의 **Personal Knowledge Base**가 되어, 추후 RAG 시스템이나 개인화된 AI 모델을 구축하는 데 귀중한 자산이 될 것입니다.

<br/>

## 🏃 Getting Started

```bash
# Frontend
cd frontend
npm install
npm run dev

# Backend
cd backend
./gradlew bootRun
