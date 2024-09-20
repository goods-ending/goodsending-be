# ❤️‍🩹 GOODSENDING
> 더 이상 필요 없는 굿즈들을 실시간 경매를 통해 구매, 판매 할 수 있는 서비스 플랫폼

🔗 [굿즈엔딩 소개 브로셔](https://www.notion.so/8528ca6a974e4795ba2ea971cbe62f53?pvs=4)
🔗 *[프로젝트 서비스 링크](https://goodsending.shop/)*


## 📌 프로젝트 소개
![굿즈엔딩 목업](https://github.com/user-attachments/assets/e19980ec-61bb-4ec6-952b-fef4573ba56f)

🧸 판매하고 싶은 상품을 경매에 등록해보세요!
💸 실시간으로 경매에 참여하여 원하는 물건을 구매해보세요!
⌨️ 경매 참여한 유저들과 채팅으로 소통해보아요!

**GoodsEnding**은 더 이상 필요 없어진 굿즈를 실시간 경매를 통해 구매하고 판매할 수 있는 플랫폼입니다. 사용자는 간편하게 상품을 등록하고, 다른 사용자와 실시간 채팅으로 소통하며 경매에 참여할 수 있습니다.

🔗 [GitHub 링크](https://github.com/goods-ending)

🔗 [GOODSENDING-FE GITHUB](https://github.com/goods-ending/goodsending-fe)

🔗 [팀 프로젝트 노션](https://nervous-mule-411.notion.site/GoodsEnding-51dccf65d0b843639f070db0a3866105?pvs=4)

🔗 [시연 영상](https://www.youtube.com/watch?v=0cbSPnoaRMQ)


📅 프로젝트 기간 : 2024.07.19 ~ 2024.08.16


### 🧬 프로젝트 아키텍처
![아키텍처](https://github.com/user-attachments/assets/6941d249-e189-4dd4-b6ee-c2ad8b725aa9)

### 🧙 기술적 의사결정

### 🔨 기술 스택

| Frontend                                                                                                                                                                                                                                                 | Backend                                                                                                                                                                                                                                                                                      | CI / CD                                                                                                                                            |
| :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------- |
| Language : JavaScript (ES6)<br>- Framework : React<br>- State Management : Redux<br>- Build Tool : AWS Amplify<br>- Package Manager : yarn<br>- UI Library : Tailwind CSS<br>- HTTP Client : Axios<br>- Formatting : Prettier<br>- Version Control : Git | Language : Java 17<br>- Framework : SpringBoot<br>- Build Toole : Gradle<br>- DB : MySQL, Redis<br>- Test : Postman<br>- JPA<br>- Auth : JWT<br>- Spring Security<br>- Docker<br>- Cloud Storage Service : AWS S3<br>- WebSocket<br>- Spring Scheduler<br>- Query DSL<br>- SMTP<br>- swagger | Deploy<br>    - AWS EC2<br>    - Docker<br>    - Github Actions<br>    - AWS ECR<br>- Communication<br>    - Slack<br>    - Github<br>    - Notion |

### 🗄 ERD
![image (2) (1)](https://github.com/user-attachments/assets/4c2464b5-1681-485c-a6c1-88f1a3a5b212)


### 🚏 API 설계
![스크린샷 2024-08-17 오전 11 22 29](https://github.com/user-attachments/assets/67c637d4-8503-4336-8d5d-97c8f44efa8c)
![스크린샷 2024-08-17 오전 11 22 58](https://github.com/user-attachments/assets/b1168bcf-22b8-4878-8e6e-a504e40b4a28)
![스크린샷 2024-08-17 오전 11 31 33](https://github.com/user-attachments/assets/c738c152-624f-4979-9a66-219b2a364266)
![스크린샷 2024-08-17 오전 11 31 49](https://github.com/user-attachments/assets/4ad86c83-9be1-41c8-9f00-bb511e9cc1df)
![스크린샷 2024-08-17 오전 11 34 52](https://github.com/user-attachments/assets/4ce8163c-06e5-4121-9821-cd02fa31ebec)
![스크린샷 2024-08-17 오전 11 36 11](https://github.com/user-attachments/assets/ac8bc942-7058-4cb2-9808-d9f196786ce2)


### 💡 주요 기능 소개
<details>
<summary>👥 [사용자] 회원 가입</summary>
<div markdown="1">
<h4>💡 실제 해당 메일 계정의 소유 여부를 검증하기 위해 인증코드를 발급하고 확인</h4>
<img src="https://github.com/user-attachments/assets/0077c91b-90f5-4e8e-955a-36be9010dded" width="70%">
<ul>
<li>1️⃣ SecureRandom.getInstanceStrong() 난수 생성 메서드</li>
<p>- 생성되는 숫자는 6자리 밖에 안되지만 강력한 보안성을 위해 사용</p>
<li>2️⃣ 네이버 SMTP (이메일을 전송할 때 사용되는 표준 통신 프로토콜)</li>
<p>- 한국 사용자에게 최적화된 SMTP 서버 이며, 국내 환경에 맞는 보안 설정과 사용성이 장점인 네이버 SMTP를 선택</p>
<li>3️⃣ Redis</li>
<p>- 인증코드는 단순하게 확인만 하면 되는 정보이므로 인메모리 데이터 저장 구조인 Redis를 사용해서 빠르게 읽을 수 있고, 자동으로 만료되는 TTL 기능도 추가하여 메모리 사용을 효율적으로 관리할 수 있도록 적용</p>
<li>4️⃣ 사용자 정의 애너테이션</li>
<p>- 사용자 정의 애너테이션을 만들어 정규 표현식 기반으로 비밀번호를 검증하고, 암호화 하여 DB에 저장되도록 설정</p>
</ul>
</div>
</details>

<details>
<summary>🔐 [사용자] 로그인 / 로그아웃</summary>
<div markdown="1">
<h4>💡 Security를 적용하여, 인증 시 Access Token과 Refresh Token 발급</h4>
<img src="https://github.com/user-attachments/assets/aa7473d5-64b3-451e-9ccc-bceb2ea9689c" width="70%">
<ul>
<li>1️⃣ 사용자 정보를 통한 JWT 토큰을 발급하는 방식으로 구현</li>
<li>2️⃣ JWT 인증 필터를 이용하여 자동적으로 토큰의 유효성 검사를 하도록 설정</li>
<li>3️⃣ JWT Access Token을 생성할 때 Refresh Token을 같이 생성해 Redis에 저장하고, API를 호출하기 전에 토큰이 만료되었는지 검사 후 만료되었으면 Redis에
저장된 Refresh Token을 확인 해 유효할 시 Access Token을 재발급하는 방법으로 사용하여 보안성을 강화하면서도 인증을 다시 하지 않아도 되도록 편의성을 갖추도록 구현</li>
<li>4️⃣ 로그아웃 경우 쿠키와 Redis에 저장되어 있는 Refresh Token은 삭제되고, Access Token은 남은 만료 시간 만큼 Redis에 저장되어 재사용이 불가능 하도록 구현
</li>
</ul>
</div>
</details>

<details>
<summary>🪪 [사용자] 마이페이지</summary>
<div markdown="1">
<h4>💡 로그인 한 회원은 자신이 보유한 캐시 와 포인트를 확인 할 수 있고, 비밀번호 변경 및 캐시 충전 가능</h4>
<img src="https://github.com/user-attachments/assets/2fbcbced-af76-4c10-87b2-e03ab4b856c0" width="70%">
<ul>
<li>@AuthenticationPrincipal 역할을 하는 사용자 정의 애너테이션 @memberId생성
인증된 사용자의 memberId를 쉽게 추출할 수 있고, 유지 보수 용이성과 코드 중복을 줄이기 위해 애너테이션 @memberId를 생성하여 사용</li>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>결제 API 사용해서 가상 결제를 추가적으로 구현해보고 싶습니다</p>
</ul>
</div>
</details>

<details>
<summary>💰 캐시 및 포인트</summary>
<div markdown="1">
<h4>💡 서비스 수익화 및 관리를 위한 캐시 및 포인트 서비스</h4>
<img src="https://github.com/user-attachments/assets/8c92cc83-6fbe-44b5-b62a-8cf599e08db6" width="70%">
<ul>
<li>1️⃣ 보증금 회수 및 환불</li>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<p>- 상품을 등록시 판매자의 캐시에서 보증금 지불</p>
<p>- 보증금 금액: 경매 최소 가격의 5%(최소 3000원)</p>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<li>2️⃣ 포인트 적립</li>
<p>- 확정금액(=낙찰자의 입찰가)의 2.5%의 포인트가 낙찰자 포인트에 적립</p>
<li>3️⃣ 수수료 징수</li>
<p>- 상품의 주문이 거래 완료 시 서비스는 확정 금액(=낙찰자의 최종 입찰가)의 5%를 수수료로 징수 후 남은 금액을 판매자에게 지급</p>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>수수료, 보증금을 관리하는 관리자 기능</p>
</ul>
</div>
</details>

<details>
<summary>📋 [주문] 낙찰된 주문 관리</summary>
<div markdown="1">
<h4>💡 낙찰된 주문 관리[배송지 정보 입력 ➡️ 배송 처리 ➡️ 거래 확정]</h4>
<img src="https://github.com/user-attachments/assets/3d2688a1-527b-44d4-b159-c32f47854aa4" width="70%">
<ul>
<p>구현한 기능</p>
<li>1️⃣ 낙찰자의 배송받을 배송지주소, 연락처, 수신자명 업데이트</li>
<li>2️⃣ 판매자의 배송지가 입력된 주문 배송처리</li>
<li>3️⃣ 낙찰자의 배송받은 주문 확정</li>
<p>- 판매자에게 보증금을 환불하고 수수료를 제외한 수익을 캐시로 입금</p>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>각각의 진행 상황에서 기한내로 다음 상태로 넘어가지 않으면 주문을 자동 처리하는 기능</p>
</ul>
</div>
</details>

<details>
<summary>✨ [주문] 낙찰자 선정 및 주문 진행</summary>
<div markdown="1">
<h4>💡 5분동안 추가 입찰이 없을 경우 낙찰자가 선정되어 주문 진행</h4>
<img src="https://github.com/user-attachments/assets/ecc76973-32c8-4e3a-be52-f68e46482205" width="70%">
<ul>
<p>구현한 기능</p>
<li>1️⃣ Redis : key expiration event</li>
<p>- 입찰 시 키의 만료시간 5분으로 정해지고 해당 키가 만료되면 key expiration event 가 발생하여 낙찰자 선정 및 주문 생성 로직이 실행</p>
<li>2️⃣ 낙찰자 선정</li>
<p>- 최대 입찰금을 지불한 마지막 입찰자가 낙찰자</p>
<li>3️⃣ 환불 처리</li>
<p>- 낙찰자를 제외한 나머지 입찰자는 지불한 캐시 및 포인트 환불 처리</p>
</ul>
</div>
</details>

<details>
<summary>📊 [인기 순위] 경매 상품 인기 순위</summary>
<div markdown="1">
<h4>💡 경매 상품의 인기 순위를 입찰자와 좋아요 수를 기준으로 조회. 경매 시간대에는 입찰자, 경매 시간대가 아닌 경우 좋아요 수를 기준으로 인기 순위를 조회</h4>
<img src="https://github.com/user-attachments/assets/85bb2cf2-2bd1-4e87-bc48-5ad1f3840cb2" width="70%">
<ul>
<p>구현한 기능</p>
<li>1️⃣ Redis를 이용한 캐싱 처리</li>
<p>- 초반에는 MySQL에서 입찰자, 좋아요 순으로 정렬하여 상위 5개의 상품을 조회하였지만, Redis의 ZSet을 사용하여 검색 성능을 개선</p>
<li>2️⃣ QueryDSL을 이용한 DTO 가져오기</li>
<p>- 처음에는 DTO를 가져오는 것이 아니라 key(ranking),value(ProductId)만 저장해서 ProductId로 상품 정보를 다시 DB에서 찾는 과정이 있었는데 과도한 MySQL
접근 대신 QueryDSL로 성능을 개선</p>
</ul>
</div>
</details>

<details>
<summary>💬 [채팅] 실시간 경매 상품 채팅</summary>
<div markdown="1">
<h4>💡 실시간 상품에 대한 채팅 참여 및 입찰,낙찰 내역을 확인</h4>
<img src="https://github.com/user-attachments/assets/5c1053b7-2bda-4113-b4d3-30e331f86c5c" width="70%">
<ul>
<li>1️⃣ Web Socket</li>
<p>- 실시간 양방향 데이터 송수신을 위한 웹소켓 활용</p>
<p>- 커스텀 핸들러를 사용하여 SEND 시 유저를 식별</p>
<li>2️⃣ STOMP</li>
<p>- WebSocket에 대한 불필요한 구현을 줄여, 명확하고 쉽게 구현</p>
<li>3️⃣ 메시지 내역 저장</li>
<p>- 유저의 채팅메시지, 입찰 및 낙찰 메시지를 DB에 저장</p>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>관리자의 차단 기능 - 무분별한 채팅을 하는 회원이 존재하면 상품의 판매자가 해당 회원을 차단하면 채팅이 불가능하게 막을 수 있는 기능</p>
</ul>
</div>
</details>

<details>
<summary>❤️ [경매 상품] 찜</summary>
<div markdown="1">
<h4>💡 서비스 수익화 및 관리를 위한 캐시 및 포인트 서비스</h4>
<img src="https://github.com/user-attachments/assets/b1696f09-ac2a-4b64-b15f-30b3069c29ca" width="70%">
<img src="https://github.com/user-attachments/assets/92f23e5a-23ab-4353-b846-e81963a4f8bf" width="70%">
<ul>
<li>1️⃣ 보증금 회수 및 환불</li>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<p>- 상품을 등록시 판매자의 캐시에서 보증금 지불</p>
<p>- 보증금 금액: 경매 최소 가격의 5%(최소 3000원)</p>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<li>2️⃣ 포인트 적립</li>
<p>- 확정금액(=낙찰자의 입찰가)의 2.5%의 포인트가 낙찰자 포인트에 적립</p>
<li>3️⃣ 수수료 징수</li>
<p>- 상품의 주문이 거래 완료 시 서비스는 확정 금액(=낙찰자의 최종 입찰가)의 5%를 수수료로 징수 후 남은 금액을 판매자에게 지급</p>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>수수료, 보증금을 관리하는 관리자 기능</p>
</ul>
</div>
</details>

<details>
<summary>🔍 [경매 상품] 검색 및 필터링 기능</summary>
<div markdown="1">
<h4>💡 사용자가 경매 상품을 검색할 때, 원하는 상품에 쉽고 빠르게 접근할 수 있는 검색 기능</h4>
<img src="https://github.com/user-attachments/assets/553b6e89-39da-4fc8-9fea-1f3b2708cf04" width="70%">
<img src="https://github.com/user-attachments/assets/d730e54a-aca5-4ce9-86dd-3de72c8646d2" width="70%">
<ul>
<li>1️⃣ QueryDSL을 사용한 검색 기능</li>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<p>- 상품을 등록시 판매자의 캐시에서 보증금 지불</p>
<p>- 보증금 금액: 경매 최소 가격의 5%(최소 3000원)</p>
<p>- 무분별한 상품 등록을 막고자 보증금 서비스 도입</p>
<li>2️⃣ 커서 기반 페이지네이션</li>
<p>- 확정금액(=낙찰자의 입찰가)의 2.5%의 포인트가 낙찰자 포인트에 적립</p>
<li>3️⃣ 경매 진행 상품, 경매 진행 될 상품, 경매 종료 된 상품 순으로 사용자의 접근성을 고려하여 정렬</li>
<li>4️⃣ 키워드, 경매 진행 상품 & 경매 진행 될 상품, 경매 종료 된 상품을 조건으로 원하는 상품에 빠르게 접근할 수 있도록 검색 결과를 제공</li>
<li><h4>추가적으로 구현하고 싶은 기능</h4></li>
<p>MySQL Ngram을 적용하여 검색 성능을 향상하거나, 대용량 데이터 처리가 필요할 경우를 대비하여 Elastic Search를 도입하여 대용량 인덱스를 관리하고 싶습니다.</p>
</ul>
</div>
</details>


### 🧨 트러블 슈팅
<details>
  <summary>🧨 프로파일 설정</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>Rapplication.yaml 을 복사하여 만든 application-cr.yaml 이 연결되지 않는 문제 발생</p>
    <ul>
      <li>원인</li>
        <p>- SpringBoot 2.4 버전부터는 각 환경에 대한 application-**.yaml 파일을 생성해야 함</p>
        <p>- 또한, application-**.yaml 파일 내부에서 spring.profiles.active 를 사용할 수 없음</p>
      <li>해결 방법</li>
        <p>- spring.profiles.active 관련 코드를 삭제</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 CORS(Cross Origin Resource Sharing)</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>백엔드 서버와 프론트 엔드 서버 연동 작업 중 API 요청 시 CORS 에러 발생</p>
    <ul>
      <li>원인</li>
        <p>- CORS 설정을 추가해 주지 않아 발생</p>
      <li>해결 방법</li>
        <p>- CORS 설정을 정의하고, SecurityFilterChain 메서드에 추가</p>
        <p>- setAllowedOrigins , setAllowedMethods , setExposedHeaders 속성을 적용할 때 와일드카드(“*”) 는 사용할 수 없으므로 명시적으로 지정</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 커서 기반 페이지네이션</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>커서 기반 페이지네이션 구현 시 cursor 판별을 위한 조회가 3회 발생</p>
    <p>설정해주어야 할 경우의 수가 많아 코드 복잡성 증가</p>
    <ul>
      <li>원인</li>
        <p>- 경매 상품의 상태(경매 진행 중, 경매 예정, 경매 종료)를 분류를 startDateTime과 maxEndDateTime로 판별</p>
        <p>- 상품 조회 마다 상태를 판별하기 위해 다수의 db 조회 발생</p>
      <li>해결 방법</li>
        <p>- 경매 상태를 나타내는 ProductStatus 를 생성하여 경매 상태의 우선순위를 설정</p>
        <p>- Cursor를 status, startDateTime, id로 설정하여 설정한 정렬 기준에 맞게 조회</p>
        <p>- 리팩토링 전후 성능 테스트(사용자 수 1000명, 1번씩 요청)를 비교하였을 때, 오류 발생 비율 약 39.5% 감소</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 쿠키 속성 SameSite 설정</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>쿠키에 저장된 Refresh Token을 서버로 가져올 때, Postman에서는 성공하지만 프론트엔드에서 API 호출 시 Refresh Token이 전달되지 않아 null이 되면서 401 오류가 발생하는 문제</p>
    <ul>
      <li>원인</li>
        <p>- SameSite를 설정해주지 않아 기본값인 Lax로 설정</p>
      <li>해결 방법</li>
        <p>- SameSite 설정을 적용할 수 있는 ResponseCookie 사용 </p>
        <p>- 속성 값은 None으로 적용</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 인기상품 Top 5의 상품 수정 문제</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>Top 5 상품의 정보를 조회할 때, 상품이 수정되면 Redis에 등록된 Top 5 상품 정보도 변경되어야 함  그래야 실시간 Top 5 상품정보가 조회될 수 있음</p>
    <ul>
      <li>원인</li>
        <p>- Redis에 상품의 정보를 입력 해두고, 해당 product 엔티티의 정보 변경이 발생했을 때,  Redis의 정보는 업데이트가 되지 않는 현상이 발생, 변경된 정보가 기존의 Redis에 등록된 정보와 다르기 때문에 오류 발생</p>
      <li>해결 방법</li>
        <p>- productId 를 기준으로 변경 할 해당 DTO를 DB (queryDSL)에서 찾아서, 먼저 Redis ZSET 에서 삭제</p>
        <p>- 그 후 해당 엔티티 product의 정보를 업데이트하고,  해당 product 의 정보를 가진 Dto 를 생성함, </p>
        <p>- 그 후 생성한 productDto(변경된 정보)를 Redis ZSET에 다시 추가해서 업데이트 </p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 토큰 검증 예외처리</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>토큰 검증시 발생하는 여러가지 예외처리 부분에 CustomException을 적용해 주었지만 제대로 출력되지 않는 문제 발생</p>
    <ul>
      <li>원인</li>
        <p>- JwtAuthenticationEntryPoint 클래스의 commence 메서드가 먼저 호출 되면서 CustomException이 아닌 commence 메서드에서 작성된 JSON 형식의 응답만 반환</p>
      <li>해결 방법</li>
        <p>- try-catch로 감싸준 다음 CustomException 예외 발생 시 jwtAuthenticationEntryPoint 클래스의 AuthenticationException 예외로 변환하여  에러 메시지가 반환 되도록 적용</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 Cannot invoke "java.lang.Long.longValue()" because "current" is null with 낙관적락</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>트랜잭션 커밋이 정상적으로 되지 않는 상황</p>
    <ul>
      <li>원인</li>
        <p>- 낙관적 락을 도입하면서 Entity 필드에 추가한 version 때문</p>
        <p>- 이전에 데이터를 프로시저를 통해 넣어주고 있었는데 version에 대한 값은 넣어주지 않음 </p>
        <p>- version이 null로 들어가기 때문에 오류가 발생</p>
      <li>해결 방법</li>
        <p>- version을 모두 0으로 직접 초기화 해주니 정상작동</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 배포한 서버 api 접근 불가</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>EC2 인스턴스에서 Spring Boot 서버를 실행하고 있는데, API에 접근할 수 없는 문제가 발생</p>
    <ul>
      <li>원인</li>
        <p>- EC2 인스턴스의 보안 그룹에서 8080 포트가 허용되지 않았기 때문에 발생한 것으로 확인</p>
        <p>- 이로 인해 외부에서 EC2 인스턴스의 Spring Boot 서버에 접근이 불가능</p>
      <li>해결 방법</li>
        <p>- 보안 그룹의 인바운드 규칙에 8080포트 번호를 허용해주어 해결</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 상속받은 클래스에 @EqualsAndHashCode 사용</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>상속받은 클래스에 @EqualsAndHashCode 사용했을 때 경고가 발생</p>
    <ul>
      <li>원인</li>
        <p>- @EqualsAndHashCode(callSuper = true) 어노테이션을 붙여주지 않으면, 부모 클래스의 필드는 제외하고 EqualsAndHashCode를 생성해서 발생하는 warning</p>
      <li>해결 방법</li>
        <p>- 자식 클래스의 필드만을 포함하고 싶기 때문에 @EqualsAndHashCode(callSuper = false)를 붙여 warning를 해결</p>
    </ul>
  </div>
</details>

<details>
  <summary>🧨 자동 낙찰자 선정 로직 구현에 대한 어려움</summary>
  <div markdown="1">
    <h4>❓문제 상황 </h4>
    <p>입찰 후 5분 후 자동으로 낙찰자를 선정해야 하는데, 단일 MySQL 데이터베이스 서버로는 실시간 입찰 데이터 처리와 만료 기반 자동 낙찰 로직을 효율적으로 처리하기 어려움이 존재</p>
    <ul>
      <li>원인</li>
        <div>
          <ol>
            <li>실시간 처리 한계: MySQL만으로는 실시간 데이터 처리에 한계</li>
            <li>지속적인 폴링(Polling)의 비효율성: 5분 후 낙찰자를 선정하기 위해 지속적인 폴링을 하면 불필요한 쿼리와 리소스 소모로 서버 성능 저하</li>
            <li>타이밍 정확성의 문제: 스케줄링 작업이 겹치면서 정확한 시점에 낙찰자를 선정하기 어려움</li>
            <li>트랜잭션 처리 부담: 실시간 다수의 입찰로 인한 트랜잭션 부하가 데이터베이스 성능 저하</li>
          </ol>
        </div>
      <li>해결 방법</li>
        <p>- Redis Key Event Notification의 만료 이벤트 기술을 도입하여 해결</p>
        <div>
          <ol>
            <li>실시간 저장: 최고 입찰 금액을 Redis에 저장하여 빠른 데이터 접근을 지원</li>
            <li>만료 설정: Redis의 만료 기능으로 5분 후 데이터가 자동으로 만료되게 설정</li>
            <li>낙찰자 선정: Redis Key Event Notification 기능을 활용하여 만료된 데이터에 대해 자동 낙찰자를 선정하는 로직을 실행</li>
          </ol>
        </div>
        <p>이로써, 입찰 데이터의 실시간 처리와 자동 낙찰을 효율적으로 구현</p>
    </ul>
  </div>
</details>

---

### 👨🏻‍💻👩🏻‍💻 팀원 구성
 
|     | 이름               | 분담                                                                              | GitHub                                                                              |
| --- | ---------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| FE  | 류진식(Vice Leader) | 로그인, 회원가입, websoket을 활용한 실시간 채팅, 상품 조회, 검색, 상세내역 보기, 경매 등록 상품 관리                | [https://github.com/ryujinsik](https://github.com/ryujinsik)                        |
| FE  | 정은주              | 상품 등록기능, 회원정보 조회, 캐시 충전, 비밀번호 변경, 마이페이지                                         | [https://github.com/25809637410](https://github.com/25809637410)                    |
| BE  | 박지은(Leader)      | CI/CD, 입찰, 낙찰, 주문 기능, Web실시간 채팅                                                 | [https://github.com/je-pa](https://github.com/je-pa)                                |
| BE  | 김채린              | 경매 상품 CRUD, 경매 상품 목록 필터링 조회, Scheduler를 사용한 경매 상품 상태 변경, 입찰자 기준 상품 인기순위 Top5 조회 | [https://github.com/puclpu](https://github.com/puclpu)                              |
| BE  | 배근우              | 찜 하기, 찜 취소, 찜 리스트, 찜 인기순위 Top5                                                  | [https://github.com/zz6331300zz](https://github.com/zz6331300zz)                    |
| BE  | 이아람              | 로그인, 회원가입, 토큰 관리, 캐시 충전, 회원정보 조회                                                | [https://github.com/ramleeramlee](https://github.com/ramleeramlee?tab=repositories) |
