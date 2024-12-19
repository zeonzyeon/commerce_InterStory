# 📖 InterStory 📖

## 목차

<div>

- [0.프로젝트 소개](#0-프로젝트-소개)
- [1.프로젝트 목표](#1-프로젝트-목표)
- [2.개발 환경 및 개발 일정](#2-개발-환경-및-개발-일정)
- [3.화면 설계서](#3-화면-설계서)
- [4.시스템 구조도](#4-시스템-구조도)
- [5.ERD](#5-ERD)
- [6.기능 명세서](#6-기능-명세서)
- [7.API 명세서](#7-api-명세서)

</div>

## 0. 프로젝트 소개

### 작가와 독자가 함께 만들어가는 이야기

Interstory는 작가와 독자가 상호작용하며 함께 만들어 나가는 반응형 소설 플랫폼입니다.

작가는 소설을 연재하고 독자들은 댓글을 통해 작품에 반응하며 AI가 이 댓글들을 분석해 작가에게 실시간으로 피드백을 제공합니다.

독자는 흥미로운 콘텐츠를 자유롭게 감상할 수 있을 뿐만 아니라 유료 콘텐츠를 소비하고 구독 서비스, 할인 혜택 등 다양한 커머스 기능을 이용할 수 있습니다.

<br>

## 1. 프로젝트 목표

1. 작가와 독자의 소통 플랫폼 구축
    - 작가와 독자가 댓글과 피드백을 통해 소설을 함께 만들어가는 상호작용형 플랫폼 제공
2. AI 기반 실시간 작품 피드백 시스템 구축
    - 독자들의 댓글을 AI로 분석하여 주요 키워드를 도출하고 작가에게 실시간 피드백 제공
3. 콘텐츠 유료화 시스템 구축
    - 독자들이 포인트 결제나 구독 서비스를 통해 소설의 유료 회차를 열람하고 프리미엄 콘텐츠를 소비할 수 있는 커머스 시스템 제공
4. 사용자 맞춤형 콘텐츠 제공
    - 사용자 행동 기반 협업 필터링 추천 알고리즘을 사용하여 독자에게 취향에 맞는 작품을 추천하고 콘텐츠 접근성을 높이는 개인화된 경험 제공

<br>

## 2. 개발 환경 및 개발 일정

### 개발환경

#### Front-end

<div>
  <img alt="HTML" src ="https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white"/>
  <img alt="CSS" src ="https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white"/>
  <img alt="JavaScript" src ="https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E"/>
  <img alt="Thymeleaf" src ="https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white"/>
</div>

#### Back-end

<div>
  <img alt="Spring" src ="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"/>
  <img alt="Hibernate" src ="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"/>
  <img alt="Java" src ="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"/>
</div>

#### Database

<div>
  <img alt="MySQL" src ="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white"/>
</div>

#### 배포 및 클라우드

<div>
  <img alt="AmazonEC2" src ="https://img.shields.io/badge/Amazon EC2-FF9900.svg?&style=for-the-badge&logo=Amazon EC2&logoColor=white"/>
  <img alt="S3" src ="https://img.shields.io/badge/AWS_S3-569A31?logo=amazons3&logoColor=fff&style=for-the-badge"/>
  <img alt="AmazonRDS" src ="https://img.shields.io/badge/Amazon RDS-527FFF.svg?&style=for-the-badge&logo=Amazon RDS&logoColor=white"/>
  <img alt="redis" src ="https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white"/>
  <img alt="docker" src ="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"/>
</div>

#### CI/CD

<div>
  <img alt="AmazonEC2" src ="https://img.shields.io/badge/Amazon EC2-FF9900.svg?&style=for-the-badge&logo=Amazon EC2&logoColor=white"/>
  <img alt="S3" src ="https://img.shields.io/badge/AWS_S3-569A31?logo=amazons3&logoColor=fff&style=for-the-badge"/>
  <img alt="GitHub Actions" src ="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white"/>
</div>

#### 협업 도구

<div>
    <img alt="github" src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
    <img alt="discord" src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
    <img alt="Figma" src="https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white">
    <img alt="Notion" src="https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white">
</div>

### 개발 일정

- 팀 빌딩 / 도메인 설정 : 2024.11.22.
- 요구사항 분석 및 기능 명세서 작성 : 2024.11.22. ~ 2024.11.28.
- API 명세서 작성 : 2024.11.26. ~ 2024.11.29.
- UI/UX 설계 : 2024.11.26. ~ 2024.12.02.
- DB 설계 : 2024.11.26. ~ 2024.12.05.
- 구현 : 2024.12.05. ~ 2024.12.19.
- 테스트 : 2024.12.13. ~ 2024.12.19.
- 발표 준비 : 2024.12.19.

<br>

## 3. 화면 설계서

[InterStory 화면 설계(figma)](https://www.figma.com/design/mXSWpX1p48UCLlsu02EcRi/%ED%99%94%EB%A9%B4-%EC%84%A4%EA%B3%84%EC%84%9C?node-id=0-1&t=YCbEi94CdYIUqlmr-1)

|                                                                                                              |                                                                                                                 |
|--------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| <img src="https://github.com/user-attachments/assets/870953e6-126f-4b7e-b7c6-b8b33d18c485"> <br/> 메인 페이지     | <img src="https://github.com/user-attachments/assets/5d3817ad-8542-4d7a-9dd1-5c65febb8fd4"> <br/> 로그인 화면        |
| <img src="https://github.com/user-attachments/assets/4e88822a-3c0d-479d-b699-6613db8a44c0"> <br/> 프로필 메뉴     | <img src="https://github.com/user-attachments/assets/6c150708-cb8c-4dd3-8053-6d3c9f811c7e"> <br/> 마이페이지         |
| <img src="https://github.com/user-attachments/assets/f591222d-fe0d-480f-b61c-c05c91d8fa62"> <br/> 구독 모달창     | <img src="https://github.com/user-attachments/assets/9fc5e6e2-1d98-4fab-b1a3-a649c9eee68b"> <br/> 포인트 결제창       |
| <img src="https://github.com/user-attachments/assets/a20d0ea6-193a-41dc-a0cc-404461c34926"> <br/> 연재한 작품 목록  | <img src="https://github.com/user-attachments/assets/ddf83681-71de-46f9-8afe-97478e89133f"> <br/> 연재한 작품의 반응 분석 |
| <img src="https://github.com/user-attachments/assets/29f91af8-366d-4ea2-a153-7ad33d0da784"> <br/> 포인트 내역     | <img src="https://github.com/user-attachments/assets/c7f57f97-b137-469f-b087-8d52dcd82ffc"> <br/> 장바구니          |
| <img src="https://github.com/user-attachments/assets/4b8852cd-b04d-427f-a7fd-a7e17b1f0960"> <br/> 회원 정보 수정   | <img src="https://github.com/user-attachments/assets/66655118-bae1-4230-84ce-da75822f8453"> <br/> 쿠폰 내역         |
| <img src="https://github.com/user-attachments/assets/06956f1a-4e22-41df-b6a4-524976f14ba9"> <br/> 소설 상세 조회   | <img src="https://github.com/user-attachments/assets/f2f78729-27cc-4c16-8001-1fbe07c96a7e"> <br/> 작성한 댓글        |
| <img src="https://github.com/user-attachments/assets/7e50222c-a030-4f68-aebe-41ab53118138"> <br/> 작품 수정      | <img src="https://github.com/user-attachments/assets/449c656d-9cf0-4a10-b1fc-74d2bb336d94"> <br/> 작품 추가         |
| <img src="https://github.com/user-attachments/assets/540f78b7-0557-4cf9-b4ec-8c9a97313405"> <br/> 회차 구매 모달창  | <img src="https://github.com/user-attachments/assets/27d33174-63a1-45f3-bd80-f3754775fc8d"> <br/> 회차 댓글 조회      |
| <img src="https://github.com/user-attachments/assets/3e8d75f3-ae38-48eb-97ba-e82452c233fa"> <br/> 회차 상세 조회   | <img src="https://github.com/user-attachments/assets/ffe3f433-43d2-4d25-9108-c524da496e7c"> <br/> 회차 수정 (소설)    |
| <img src="https://github.com/user-attachments/assets/2876b066-731f-4166-b40a-427486d72e27"> <br/> 회차 작성 (소설) |                                                                                                                 |

## 4. 시스템 구조도

![image](https://github.com/user-attachments/assets/409ef510-c3ca-47b5-bb63-55e40d434f8f)

<br>

## 5. ERD

![image](https://github.com/user-attachments/assets/2ae78964-00fc-47ab-9247-539c7c1d93b9)

<br>

## 6. 기능 명세서

| **기능**          | **상세 기능**            | **설명**                                                       |
|-----------------|----------------------|--------------------------------------------------------------|
| **회원가입**        | 일반 또는 OAuth2로 가입 가능  | 이메일을 ID로 설정하며, 비밀번호는 암호화하여 저장                                |
| **로그인**         | 일반 또는 OAuth2로 로그인 가능 | 세션에 로그인된 사용자 정보를 저장                                          |
| **로그아웃**        |                      | 로그인된 세션을 종료                                                  |
| **탈퇴**          | Soft Delete          | 탈퇴 전 확인 경고를 표시하며, 데이터는 실제 삭제 대신 비활성화                         |
| **소설 관리**       | CRUD                 | 소설 데이터를 생성, 조회, 수정, 삭제                                       |
| **회차 관리**       | CRUD                 | 소설의 개별 회차를 관리 (생성, 조회, 수정, 삭제)                               |
| **공유**          | URL 공유               | 소설 URL을 통해 외부에 공유 가능                                         |
| **댓글 관리**       | CRUD                 | 댓글 작성, 수정, 삭제 가능. 삭제된 댓글은 "삭제된 댓글"로 표시                       |
| **카테고리 분류**     | 태그, 랭킹, 트렌드 기반 분류    | 메이저/커스텀 태그 사용. 단위 시간 내 랭킹 계산. 맞춤형 추천 제공                      |
| **검색 기능**       | 제목, 작가, 태그로 검색 가능    | 모든 소설과 메타 정보를 기반으로 상세 검색                                     |
| **회원 정보 조회**    |                      | 회원 기본 정보(닉네임, 이메일) 조회                                        |
| **회원 정보 수정**    | 비밀번호/닉네임 변경          | 개인정보 수정 가능                                                   |
| **관심/열람 작품 관리** | 관심 작품과 열람 기록 관리      | 관심 목록에 추가된 소설 및 열람 기록 조회                                     |
| **포인트 이용 내역**   |                      | 포인트 사용 내역 확인                                                 |
| **연재 작품 관리**    | 조회수 확인, 수익금 정산       | 조회수에 따라 수익금을 계산. 수익금 정산 기능 제공                                |
| **계좌번호 관리**     | 계좌번호 조회 및 수정         | 기본 계좌번호 설정 가능                                                |
| **작성 댓글 관리**    | 댓글 작성 내역 확인          | 댓글이 작성된 작품으로 바로가기 링크 제공                                      |
| **독자 반응 분석**    | AI를 활용한 댓글 분석        | 최신 회차의 댓글을 앨런 AI로 분석하여 요약, 긍정/부정 퍼센트 제공                      |
| **쿠폰 등록**       |                      | 사용자가 쿠폰을 등록하여 포인트로 사용할 수 있음                                  |
| **구독 서비스**      | 단기 결제 / 정기 결제        | 정기 결제를 통해 할인 혜택 제공. 회차 무제한 열람 가능 (보관 불가)                     |
| **포인트 구매**      | 금액에 따른 할인            | 단기 결제 및 조건부 자동 결제 가능. 카카오/네이버 결제 API 사용                      |
| **장바구니**        | 상품 조회, 삭제, 결제        | 장바구니에 담긴 상품을 확인하고 결제 가능. 헤더에서 장바구니 바로가기 제공                   |
| **회차 구매**       | 포인트로 회차 구매           | 포인트를 사용해 유료 회차를 열람                                           |
| **유저 관리**       | 전체 유저 조회, 비활성화       | 개인정보, 연재 작품, 탈퇴 회원 구분 및 가입/접속 정보 확인. Soft Delete로 유저 비활성화 가능 |
| **작품/회차 관리**    | Soft Delete          | 삭제된 작품/회차는 "삭제된 항목"으로 표시                                     |
| **공지사항 관리**     | CRUD                 | 공지사항 추가, 수정, 삭제 가능                                           |
| **쿠폰 발급**       | CRUD                 | 포인트 결제에서 사용할 쿠폰 발급 가능                                        |
| **AI 댓글 분석**    | 댓글 요약, 긍정/부정 분석      | Redis를 사용해 최신 회차 댓글을 분석. 긍정/부정 비율과 주요 내용을 작가에게 제공            |
| **AI 추천 기능**    | 맞춤형 추천               | 앨런 AI와 Redis를 활용해 독자의 관심사와 행동 기반으로 맞춤형 콘텐츠 추천                |

<br>

## 7. API 명세서

### **회원관리 API /api/auth**

| **서비스명**                  | **메서드** | **uri**                       | **설명**                                |
|---------------------------|---------|-------------------------------|---------------------------------------|
| **signup**                | POST    | `/signup`                     | 회원 가입                                 |
| **login**                 | POST    | `/login`                      | 로그인                                   |
| **loginKakao**            | POST    | `/oauth2/authorization/kakao` | 카카오 로그인 (prefix 예외, 또는 필요에 따라 url 변경) |
| **logout**                | POST    | `/logout`                     | 로그아웃                                  |
| **withdrawal**            | DELETE  | `/withdrawal?userId={userId}` | 탈퇴                                    |
| **emailVerificationSend** | POST    | `/email-verification-send`    | 이메일 인증 - 이메일 전송 및 코드 생성, 레디스 저장       |
| **emailVerification**     | POST    | `/email-verification`         | 인증 코드 확인                              |
| **nicknameVerification**  | POST    | `/nickname-verification`      | 닉네임 중복 확인                             |

### **소설 API /api/novels**

| **서비스명**          | **메서드** | **uri**                                                                                      | **설명**    | **리턴값**       |
|-------------------|---------|----------------------------------------------------------------------------------------------|-----------|---------------|
| **writeNovel**    | POST    | -                                                                                            | 소설 기획서 작성 |               |
| **updateNovel**   | PUT     | `/{novelId}`                                                                                 | 소설 수정     |               |
| **readNovel**     | GET     | `/{novelId}?sort={sort}`                                                                     | 소설 상세 조회  | 소설 정보, 회차 리스트 |
| **readNovelList** | GET     | `?status={status}&title={title}&author={author}&monetized={monetized}&tag={tag}&sort={sort}` | 소설 목록 조회  |               |
| **deleteNovel**   | DELETE  | `/{novelId}`                                                                                 | 소설 삭제     |               |
| **writeEpisode**  | POST    | `/{novelId}/episodes`                                                                        | 회차 작성     |               |

### **회차 API /api/novels/episodes/{episodeId}**

| **서비스명**            | **메서드** | **uri**     | **설명**   |
|---------------------|---------|-------------|----------|
| **updateEpisode**   | PUT     | -           | 회차 수정    |
| **readEpisode**     | GET     | -           | 회차 상세 조회 |
| **deleteEpisode**   | DELETE  | -           | 회차 삭제    |
| **purchaseEpisode** | POST    | `/purchase` | 회차 구매    |
| **addItemToCart**   | POST    | `/cart`     | 장바구니 담기  |
| **likeEpisode**     | POST    | `/like`     | 회차 추천    |

### **댓글 API /api**

| **서비스명**              | **메서드** | **uri**                                                         | **설명**   |
|-----------------------|---------|-----------------------------------------------------------------|----------|
| **writeComment**      | POST    | `/novels/episodes/{episodeId}/comments`                         | 댓글 작성    |
| **getEpisodeComment** | GET     | `/novels/episodes/{episodeId}/comments?sort={sort}&page={page}` | 회차 댓글 조회 |
| **getNovelComment**   | GET     | `/novels/{novelId}/comments?sort={sort}&page={page}`            | 소설 댓글 조회 |
| **deleteComment**     | DELETE  | `/novels/episodes/comments/{commentId}`                         | 댓글 삭제    |
| **likeComment**       | POST    | `/novels/episodes/comments/{commentId}/like`                    | 댓글 추천    |

### **마이페이지 API /api/users**

| **서비스명**              | **메서드** | **uri**             | **설명**       | **리턴값**                         |
|-----------------------|---------|---------------------|--------------|---------------------------------|
| **getUser**           | GET     | -                   | 회원 정보 조회     | 닉네임, 프로필 사진, 보유 포인트, 이용 중인 서비스들 |
| **updateUser**        | PUT     | -                   | 회원 정보 수정     |                                 |
| **getFavoriteNovel**  | GET     | `/favorite-novels`  | 관심 작품 조회     | 최신업로드순                          |
| **getReadNovel**      | GET     | `/read-novels`      | 열람 작품 조회     |                                 |
| **getPointHistory**   | GET     | `/point-history`    | 포인트 이용 내역 조회 |                                 |
| **getMyNovel**        | GET     | `/my-novels`        | 연재한 작품 조회    |                                 |
| **getSettlementFee**  | GET     | `/settlement`       | 정산 받을 금액 조회  |                                 |
| **requestSettlement** | POST    | `/settlement`       | 연재한 작품 정산    |                                 |
| **getMyComment**      | GET     | `/my-comments`      | 작성한 댓글 목록    |                                 |
| **getNovelReactions** | GET     | `/novels/reactions` | 반응 분석 확인     |                                 |
| **getAccount**        | GET     | `/account`          | 계좌 번호 조회     |                                 |
| **updateAccount**     | PUT     | `/account`          | 계좌 번호 수정     |                                 |
| **getCoupon**         | GET     | `/coupons`          | 보유 쿠폰 조회     |                                 |
| **saveCoupon**        | POST    | `/coupons`          | 쿠폰 등록        |                                 |
| **getSubscription**   | GET     | `/subscription`     | 구독 기간 조회     |                                 |

### **현금 결제 API /api/cash**

| **서비스명**             | **메서드** | **uri**      | **설명**                             |
|----------------------|---------|--------------|------------------------------------|
| **establishPayment** | POST    | `/establish` | 결제 요청 (body에 단기, 정기, 자동 & 쿠폰id 포함) |
| **requestPayment**   | GET     | `/request`   | 결제 요청 성공 시 결제 승인 요청                |
| **cancelPayment**    | GET     | `/cancel`    | 결제 중 취소                            |
| **failPayment**      | GET     | `/fail`      | 결제 실패                              |
| **inactivePayment**  | POST    | `/inactive`  | 정기결제 등록 취소                         |

### **장바구니 API /api/cart**

| **서비스명**              | **메서드** | **uri**           | **설명**         |
|-----------------------|---------|-------------------|----------------|
| **getCart**           | GET     | -                 | 장바구니 아이템 조회    |
| **deleteCartItems**   | DELETE  | -                 | 장바구니 아이템 선택 삭제 |
| **purchaseCartItems** | POST    | `/purchase-items` | 장바구니 선택 결제     |

### **관리자 API /api/admin**

| **서비스명**                | **메서드** | **uri**                                                       | **설명**         |
|-------------------------|---------|---------------------------------------------------------------|----------------|
| **getUsers**            | GET     | `/users?page={page}`                                          | 전체 유저 조회       |
| **searchUsers**         | GET     | `/users/search?nickname={nickname}&email={email}&page={page}` | 닉네임/이메일로 유저 검색 |
| **activeUser**          | POST    | `/users/{userId}/active`                                      | 유저 복구          |
| **writeNotice**         | POST    | `/notices`                                                    | 공지사항 작성        |
| **updateNotice**        | PUT     | `/notices/{noticeId}`                                         | 공지사항 수정        |
| **getNoticeList**       | GET     | `/notices?page={page}`                                        | 공지사항 목록 조회     |
| **getNoticeDetail**     | GET     | `/notices/{noticeId}`                                         | 공지사항 상세 조회     |
| **deleteNotice**        | DELETE  | `/notices/{noticeId}`                                         | 공지사항 삭제        |
| **generateCoupon**      | POST    | `/coupons`                                                    | 쿠폰 발급          |
| **getCoupons**          | GET     | `/coupons?page={page}`                                        | 쿠폰 목록 조회       |
| **deleteCoupon**        | DELETE  | `/coupons/{couponId}`                                         | 쿠폰 삭제          |
| **handlePlanApproval**  | POST    | `/plan/{novelId}/approval?approve={approve}`                  | 기획서 거절/승인      |
| **restoreDeletedNovel** | POST    | `/novel/{novelId}/restore`                                    | 삭제된 작품 복구      |

### **AI API /api/ai**

| **서비스명**               | **메서드** | **uri**                       | **설명**                     |
|------------------------|---------|-------------------------------|----------------------------|
| **getNovelSuggestion** | GET     | `/{userId}/novels/suggestion` | AI를 사용한 추천                 |
| **getEpisodeReaction** | POST    | `/ai/{episodeId}/reaction`    | 회차별 댓글 분석 (댓글 5개 이상 사용 가능) |

