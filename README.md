# 로또 번호 생성 마법진 Backend

로또 번호 생성 마법진 프로젝트의 Spring Boot 백엔드 서버입니다.

사용자가 선택한 마법 요소 3개를 기반으로 로또 번호 6개, 행운 점수, 행운 메시지, 마법진 이미지 정보를 생성해 프론트엔드에 전달합니다.

## 주요 기능

- 선택 가능한 마법 요소 목록 조회
- 선택 요소 3개 기반 로또 번호 생성
- 행운 점수 및 행운 메시지 생성
- 마법진 이미지 번호와 이미지 경로 반환
- 요청값 검증 및 공통 에러 응답 처리
- 프론트엔드 연동을 위한 CORS 설정
- Swagger UI 기반 API 문서 제공
- Actuator 기반 서버 상태 확인

## 기술 스택

- Java 17
- Spring Boot 3.5.x
- Spring Web MVC
- Spring Validation
- Spring Boot Actuator
- Springdoc OpenAPI Swagger
- Lombok
- Gradle
- JUnit 5
- Mockito
- MockMvc

## 실행 환경

- Java 17 이상
- Gradle Wrapper 사용

## 로컬 실행

```bash
./gradlew bootRun
```

기본 실행 주소는 다음과 같습니다.

```text
http://localhost:8080
```

기본 프로필은 `local`입니다.

## 테스트 실행

```bash
./gradlew test
```

테스트는 서비스 로직, 컨트롤러 응답, 요청 검증, 공통 예외 처리를 확인합니다.

## API 문서

로컬 실행 후 Swagger UI에서 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON 문서는 다음 경로에서 확인할 수 있습니다.

```text
http://localhost:8080/v3/api-docs
```

## 주요 API

### 서버 상태 확인

```http
GET /
```

서버가 실행 중인지 확인하는 간단한 JSON 응답을 반환합니다.

### 선택 요소 목록 조회

```http
GET /api/lotto/options
```

사용자가 선택할 수 있는 마법 요소 목록을 반환합니다.

응답 예시:

```json
{
  "options": [
    "행운",
    "조상님의도움",
    "제왕의자리"
  ]
}
```

### 로또 번호 생성

```http
POST /api/lotto/draw
Content-Type: application/json
```

요청 예시:

```json
{
  "selectedOptions": [
    "행운",
    "조상님의도움",
    "외계인의텔레파시"
  ]
}
```

응답 예시:

```json
{
  "numbers": [3, 11, 19, 27, 34, 42],
  "luckScore": 84,
  "luckMessage": "우주 통신 연결 완료",
  "selectedOptions": [
    "행운",
    "조상님의도움",
    "외계인의텔레파시"
  ],
  "spellNumber": 3,
  "spellImageUrl": "/images/spells/3.png"
}
```

## 요청 검증 규칙

`POST /api/lotto/draw` 요청의 `selectedOptions`는 다음 조건을 만족해야 합니다.

- 반드시 존재해야 합니다.
- 정확히 3개의 요소를 포함해야 합니다.
- 각 요소는 빈 문자열일 수 없습니다.
- 서버에 등록된 선택 요소만 사용할 수 있습니다.
- 같은 요소를 중복 선택할 수 없습니다.

검증에 실패하면 HTTP 400 응답을 반환합니다.

## 에러 응답

공통 에러 응답 형식은 다음과 같습니다.

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "3개의 요소를 선택해주세요.",
  "path": "/api/lotto/draw",
  "timestamp": "2026-05-24T15:30:12.123"
}
```

주요 에러 상황:

- 잘못된 요청값: `400 Bad Request`
- 잘못된 JSON 형식: `400 Bad Request`
- 서버 내부 오류: `500 Internal Server Error`

## 상태 확인

Actuator health endpoint는 다음 경로에서 확인할 수 있습니다.

```text
http://localhost:8080/actuator/health
```

## 환경 설정

공통 설정은 `application.yml`에서 관리합니다.

로컬 환경 설정:

```text
application-local.yml
```

운영 환경 설정:

```text
application-prod.yml
```

주요 설정값:

| 설정 | 설명 | 기본값 |
|---|---|---|
| `server.port` | 서버 실행 포트 | `8080` |
| `spring.profiles.default` | 기본 실행 프로필 | `local` |
| `app.cors.allowed-origin` | 허용할 프론트엔드 Origin | `http://localhost:3000` |
| `management.endpoints.web.exposure.include` | 노출할 Actuator endpoint | `health,info` |

운영 환경에서는 `FRONTEND_URL` 환경 변수로 CORS 허용 Origin을 지정할 수 있습니다.

```bash
FRONTEND_URL=https://example.com
```

## 배포

이 프로젝트는 Render와 같은 Java 애플리케이션 호스팅 환경에 배포할 수 있습니다.

권장 설정:

- Java 버전: `17`
- Build Command: `./gradlew build`
- Start Command: `java -jar build/libs/lotto-backend-0.0.1-SNAPSHOT.jar`
- Environment Variable: `FRONTEND_URL`

서버 포트는 `PORT` 환경 변수가 있으면 해당 값을 사용하고, 없으면 `8080`을 사용합니다.
