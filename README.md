# 📝 NoteWise Project
### AI Agentic Workflow for Automated Knowledge Archiving

> **NoteWise**는 Gemini AI와 React,Spring Boot, Make 자동화를 결합하여, 대화 내용을 Notion 지식 베이스로 즉시 구조화&저장하는 **AI 에이전틱 워크플로우**입니다.

<br/>
<br/>

## 📢 프로젝트 소개

### 1. 개발 배경
평소 개발 학습과 프로젝트를 진행하며 하루에도 기술을 검색하고 학습합니다. 하지만 이 과정에서 두 가지 결정적인 불편함을 느꼈습니다.

1.  **복사 & 붙여넣기의 비효율**: AI가 생성한 통찰력 있는 코드나 개념 설명을 다시 Notion에 옮겨 적는 과정이 너무 번거로웠고, 귀찮음으로 인해 유실되는 지식이 너무 많음.
2.  **AI의 행동 제약**: LLM은 자체적으로 웹훅을 호출하거나 외부 시스템과 통신할 수 없으며, 그러한 동작은 항상 LLM 외부의 런타임이나 애플리케이션 계층에서 수행.

<br/>
<br/>

### 2. 프로젝트 목표
**NoteWise**는 이러한 불편함을 없애고자 시작되었습니다.
* **Knowledge Archiving Automation**: 사용자가 AI와 대화하는 것만으로, 학습 내용과 개발 산출물이 자동으로 정리되어 데이터베이스에 쌓입니다.
* **Bridge the Gap**: Spring Boot 서버를 구축하여 AI의 텍스트 응답을 실질적인 '데이터'로 변환하고, Make(Webhook)를 통해 Notion API를 트리거합니다.
* **Context Continuity**: 단발성 질문이 아닌, 대화의 맥락을 유지하며 지식을 고도화할 수 있는 환경을 제공합니다.

> **Note**: 본 프로젝트는 RAG(검색 증강 생성) 구축 전 단계로, 데이터의 자동 적재와 에이전틱 워크플로우 구현에 초점을 맞추었습니다.

<br/>
<br/>

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.9-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)

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
<br/>
<br/>
<br/>

## 📌 주요 기능

| 기능 | 설명 |
| :--- | :--- |
| **💬 Smart Chat Interface** | React 19 기반의 채팅 UI. |
| **⚡ Notion Webhook Trigger** | 핵심 기능. AI가 정리한 내용을 서버가 받아 Make의 Webhook URL로 전송, Notion DB에 즉시 저장합니다. |
| **🧠 Archivist Persona** | AI에게 단순 챗봇이 아닌 'IT 전문가' 페르소나를 부여하여, 나중에 찾아보기 쉬운 형태(요약, 태그 등)로 답변하도록 튜닝. |
| **🔄 End-to-End Workflow** | `질문 -> 분석 -> 구조화 -> 적재`의 전 과정을 사용자의 추가 개입 없이 한 번의 대화로 완료. |

<br/>
<br/>
<br/>
<br/>

## ⚙️ 시스템 아키텍처

<img width="2816" height="1536" alt="System Architecture" src="https://github.com/user-attachments/assets/d4103abf-700c-4894-a8d0-cec5d60c5922" />

<br/>
<br/>




1.  **User Input (React)**: 사용자가 NoteWise 채팅 인터페이스를 통해 질문하거나 저장을 요청합니다.
2.  **Context Management (Spring Boot)**: 서버는 Sliding Window 기법으로 대화 맥락을 최적화하여 Gemini API에 전달합니다.
3.  **Intelligence (Gemini API)**: AI는 사용자 의도를 파악하고, Notion에 저장하기 적합한 형태(JSON/Markdown)로 내용을 구조화합니다.
4.  **Action Trigger (Server -> Make)**: 서버는 AI의 응답을 감지하고, 사전에 정의된 **Make Webhook**으로 데이터를 전송합니다.
5.  **Automated Archiving (Make -> Notion)**: Make 워크플로우가 실행되며 Notion API를 통해 데이터베이스에 페이지를 자동 생성합니다.

<br/>
<br/>

## 📱 실행 화면

### 화면 [1]
<img width="1920" height="1080" alt="1" src="https://github.com/user-attachments/assets/eb5b59a4-1d7a-4452-9eeb-789d7d50668b" />
<br/>

### 화면 [2]
<img width="1920" height="1080" alt="2" src="https://github.com/user-attachments/assets/1120a663-0012-48ab-9001-49635ed134b5" />
<br/>

### 화면 [3]
<img width="1920" height="1080" alt="3" src="https://github.com/user-attachments/assets/556c6ef3-961c-4eb2-86cb-151644c11f53" />
<br/>

### 화면 [4]
<img width="1920" height="1080" alt="4" src="https://github.com/user-attachments/assets/6f654e55-5efb-4f17-9335-b12e0e4732ac" />
<br/>

### 화면 [5]
<img width="1909" height="984" alt="5" src="https://github.com/user-attachments/assets/61bb2a5c-4394-4d92-b90e-6996f02f1b95" />
<br/>

### 화면 [6]
<img width="1920" height="1080" alt="6" src="https://github.com/user-attachments/assets/d6cf6bac-1499-49d6-a7b9-037e8bbed4ca" />
<br/>

### 화면 [7]
<img width="1920" height="1080" alt="7" src="https://github.com/user-attachments/assets/3b5f8c15-52d8-4111-9f75-3eb56c706379" />
<br/>

### 화면 [8]
<img width="1920" height="1080" alt="8" src="https://github.com/user-attachments/assets/c17911b6-fd2e-49c7-bcd4-a5b9039206dc" />
<br/>

### 화면 [9]
<img width="1915" height="987" alt="9" src="https://github.com/user-attachments/assets/f52e23f8-781a-4b9e-afd5-6e81e2484b33" />

<br/>
<br/>
<br/>

## 💡 기술적 도전 및 회고

### 1. Server-Side Orchestration을 통한 Action 실행 구현
LLM은 본질적으로 텍스트를 생성하는 추론 엔진일 뿐, 외부 시스템과 통신하거나 실제 로직을 수행할 수 있는 실행 환경이 없습니다.
* **해결:** Spring Boot 서버를 AI의 추론 결과를 실제 동작으로 연결하는 실행 계층으로 구축했습니다.
    * **Structured Output:** AI가 자연어가 아닌, 시스템이 처리 가능한 구조화된 데이터(JSON)를 반환하도록 프롬프트 엔지니어링을 적용했습니다.
    * **API Triggering:** 서버는 AI의 응답 페이로드를 파싱하여 저장 요청임을 식별하고, 사전에 정의된 **Make Webhook**으로 요청을 전송합니다.
    * 이를 통해 **LLM의 의도 파악 능력**과 **서버의 로직 실행 능력**을 결합하여, 실질적인 업무 자동화를 수행하는 **Agentic Workflow**를 완성했습니다.

<br/>

### 2. Sliding Window를 통한 Context 최적화
API를 사용할 때 전체 대화 내역을 매번 전송하는 것은 비용 증가와 응답 속도 저하를 야기합니다.
* **해결:** Java의 `List` 자료구조를 활용해 최근 N개의 대화 턴(Turn)만 유지하는 **Sliding Window** 알고리즘을 적용했습니다. 이를 통해 현재 주제에 대한 논리적 연속성은 유지하면서도 시스템 리소스를 효율적으로 관리했습니다.

<br/>

### 3. 데이터 자산화
기존에는 휘발되던 개발 고민과 문제 해결 과정들이 NoteWise를 통해 자동으로 축적되기 시작했습니다.
* **성과:** 별도의 정리 시간 없이도 Notion에 쌓인 데이터들은 향후 저만의 **Personal Knowledge Base**가 되어, 추후 RAG 시스템이나 개인화된 AI 모델을 구축하는 데 귀중한 자산이 될 것입니다.
