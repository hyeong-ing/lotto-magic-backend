# 🔮 Lotto Magic 🪄

<br/>

<p align="center">

  <br/>
  프론트엔드와 백엔드를 분리한 프로젝트를 여러 번 진행했지만, <br/>
  두 영역을 독립적인 관점에서 설계하고 개발한 경험은 부족했습니다. <br/>
  그래서 이번 프로젝트에서는 역할과 책임을 명확히 나누고, <br/>
  API 규격을 기준으로 독립적으로 개발할 수 있는 구조를 구현해보고자 했습니다. <br/>
  <br/>

  <br/>
  
  <img width="800" height="450" alt="image" src="https://github.com/user-attachments/assets/f6e5751e-2a42-4f00-ae1c-4f7bae3f1a28" />
  
</p>

<br/>
<br/>
<br/>

### 🔶 프로젝트 관련 링크

+ [Blog (프로젝트 기록)](https://post-this.tistory.com/category/%F0%9F%92%BB%20%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8/%F0%9F%8D%80%ED%96%89%EC%9A%B4%EC%9D%98%20%EB%A1%9C%EB%98%90%20%EB%A7%88%EB%B2%95%EC%A7%84%F0%9F%9B%B8)
+ Youtube (동작화면)
+ [Figma (다이어그램)](https://www.figma.com/board/l2IJSK7tnbOUJtfsGLfCHB/Lotto-Magic-Circle?node-id=0-1&t=GXmAo1ozuWh2cIsq-1)


<br/>
<br/>

### 🔶 프로젝트 설명

<br/>

<p align="center">
  <img width="800" height="500" alt="image" src="https://github.com/user-attachments/assets/26fa638a-5937-4133-87e0-91c0ca8db71c" />
</p>

<br/>

+ 사용자는 16개의 행운의 요소 중 원하는 요소 3개를 선택할 수 있습니다.
+ 선택한 요소의 점수를 반영해서 중복되지 않는 로또 번호 6개를 생성합니다.
+ 요소 점수와 날짜, 무작위 값을 조합해 행운 점수를 계산합니다.
+ 점수 구간에 따른 행운의 메시지와 무작위 마법진 이미지를 제공합니다.
+ 별도의 데이터베이스 없이 요청마다 새로운 결과를 생성합니다.

<br/>
<br/>

### 🔶 기술 스택 & 라이브러리
+ 백엔드 : Java 17, Spring Boot
+ API 문서화 : Springdoc OpenAPI, Swagger UI
+ 서버 상태 확인 : Spring Boot Actuator
+ 테스트 : JUnit 5, Mockito, MockMvc
+ 배포 : Docker, Render

<br/>
<br/>

### 🔶 프로젝트 목표
+ Swagger와 OpenAPI를 활용해 요청과 응답 규격을 문서화하기.
+ JUnit 5, Mockito, MockMvc를 활용해 프론트엔드에 의존하지 않고 백엔드 기능 검증하기.
+ 입력값 검증과 전역 예외 처리를 적용해 일관된 오류 응답 형식 구현하기.
+ Docker, 환경 변수와 Actuator를 적용해 배포 및 운영 환경을 고려한 서버 구성하기.

<br/>
<br/>

### 🔶 핵심 로직
1) 선택 요소 검증 <br/>
사용자가 전달한 선택 요소가 로또 번호 생성 조건을 만족하는지 검증했습니다.

+ 선택 요소는 정확히 3개여야 합니다.
+ 빈 값이나 서버에 등록되지 않은 요소는 사용할 수 없습니다.
+ 동일한 요소를 중복해서 선택할 수 없습니다.

```java
    if (selectedOptions == null || selectedOptions.size() != 3) {
        throw new IllegalArgumentException(
                "요소는 정확히 3개 선택해야 합니다."
        );
    }
    ...
```

<br/>
<br/>

----

2) 로또 번호 생성 <br/>
선택한 요소마다 설정된 점수를 합산하고 해당 점수를 번호 생성 과정에 반영했습니다.

+ 0부터 44 사이의 중복되지 않는 인덱스 6개를 생성합니다.
+ 각 인덱스를 선택 요소 점수만큼 이동시킵니다.
```java
private List<Integer> generateLottoNumbers(int optionScore) {
    Set<Integer> randomIndexes = new LinkedHashSet<>();

    while (randomIndexes.size() < 6) {
        randomIndexes.add(random.nextInt(45));
    }

    List<Integer> numbers = randomIndexes.stream()
            .map(index -> (index + optionScore) % 45 + 1)
            .sorted()
            .toList();

    return numbers;
}
```

<br/>
<br/>

----

3) 행운 점수와 메시지, 마법진 생성 <br/>
선택한 요소뿐만 아니라 날짜와 무작위 값을 함께 사용해 매번 다른 행운 결과가 나타나도록 생성했습니다.

+ 기본 점수는 10부터 60 사이에서 무작위로 정합니다.
+ 선택 요소 점수와 오늘 날짜의 마지막 숫자를 더합니다.
+ -5부터 5사이의 무작위 보정값을 추가합니다.
+ 점수 구간에 따라 행운 메시지를 선택하고, 9개의 마법진 중 하나를 반환합니다.

```java
private int generateLuckScore(int optionScore) {
    int baseScore = random.nextInt(51) + 10;
    int todayLastDigit =
            LocalDate.now(clock).getDayOfMonth() % 10;
    int randomAdjustment = random.nextInt(11) - 5;

    int score = baseScore
            + optionScore
            + todayLastDigit
            + randomAdjustment;

    return Math.max(0, Math.min(score, 100));
}
```
```java
private String pickLuckMessage(int score) {
    if (score <= 20) return "우주 와이파이 끊김";
    if (score <= 40) return "조상님이 애쓰는 중";
    if (score <= 60) return "요정의 행운이 다가오는 중";
    if (score <= 80) return "내인생약간상승황동티켓";
    if (score <= 92) return "우주 통신 연결 완료";

    return "외계인도 박수치는 날";
}
```
<br/>
<br/>

----

4) 공통 에러 응답 처리 <br/>
컨트롤러와 서비스에서 발생하는 오류를 전역 예외 처리 클래스에서 관리했습니다.

+ 요청값 검증 실패와 서비스 검증 실패는 `400 Bad Request`로 반환합니다.
+ 잘못된 JSON 요청과 예상하지 못한 서버 오류를 구분해 처리합니다.
+ 모든 오류를 동일한 응답 구조로 전달해 프론트엔드가 일관되게 처리할 수 있도록 했습니다.

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handle(
        IllegalArgumentException exception
) {
    return ResponseEntity.badRequest()
            .body(createErrorResponse(exception));
}
```

<br/>
<br/>
<br/>

### 🔶 문제 해결

### [ 로또 번호 계산을 인덱스 기반으로 변경 ] <br/>

1) 문제 발생 <br/>

+ 처음에는 `1~45` 범위의 로또 번호를 직접 생성한 뒤, 선택 요소 점수만큼 이동시키도록 구현했습니다.
+ 하지만 `45`를 초과한 번호를 순환시키기 위해 `-1`, `%45`, `+1` 처리를 반복해야했습니다.

<br/>

2) 원인 파악 <br/>

+ 작성된 계산식은 결국 번호를 잠시 `0~44` 범위로 바꿨다가 다시 로또 번호로 변환하는 구조였습니다.
+ 사실상 인덱스 방식과 동일한 계산을 하고 있었습니다.

<br/>

3) 문제 해결 <br/>

+ 처음부터 `0~44` 범위의 인덱스를 생성하고 점수만큼 이동한 뒤, 마지막에 `1`을 더해 로또 번호로 변환했습니다.
+ 계산용 인덱스와 실제 로또 번호를 분리하면서 코드가 간결해졌고 순환 이동 로직의 의도가 더 명확해졌습니다.

```java
int randomIndex = random.nextInt(45);
int movedIndex = (randomIndex + selectedOptionScore) % 45;
int lottoNumber = movedIndex + 1;
```

<br/>
<br/>


### [ @Valid 검증 예외 미처리 ] <br/>

1) 문제 발생 <br/>

+ 선택 요소를 2개만 전달했을 때 `400 Bad Request`가 반환되는지 확인하는 테스트를 작성했습니다.
+ 그러나 예상과 달리 `500 Internal Service Error`가 반환되었습니다.

<br/>

2) 원인 파악 <br/>

+ `LottoRequst`에 요소 개수를 3개로 제한하는 `@Size`가 적용되어 있었습니다.
+ 컨트롤러의 `@Valid`가 서비스 호출 전에 요청값을 검증하면서 `MethodArgumentNotValidException`이 먼저 발생했습니다.
+ 해당 예외를 처리하는 메서드가 없어, 모든 예외를 처리하는 `Exception` 핸들러가 이를 잡고 500 응답을 반환했습니다.
+ 따라서 Mock으로 설정한 `lottoService.draw()`는 실제로 호출되지 않았습니다.

```java
@Size(min = 3, max = 3, message = "3개의 요소를 선택해주세요.")
List<String> selectedOptions
```
<br/>

3) 문제 해결 <br/>

+ `MethodArgumentNotValidException` 전용 핸들러를 추가했습니다.
+ DTO에 작성한 검증 메시지를 추출해 일관된 `400 Bad Request` 응답으로 반환하도록 수정했습니다.

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
) {
    String message = exception.getBindingResult()
            .getFieldErrors()
            .getFirst()
            .getDefaultMessage();

    return ResponseEntity.badRequest()
            .body(createErrorResponse(message, request));
}
```

<br/>
<br/>

### [ CORS 문제 ] <br/>

1) 문제 발생 <br/>

+ 프론트엔드와 백엔드가 서로 다른 주소에서 실행되어 요청이 CORS 정책에 의해 차단되었습니다.

<br/>

2) 원인 파악 <br/>

+ 백엔드에 프론트엔드 출처를 허용하는 CORS 설정이 필요했습니다.
+ 여러 API에 같은 정책을 적용해야 해 컨트롤러별 `@CrossOrigin`보다 전역 설정이 적합해보였습니다.
  
<br/>

3) 문제 해결 <br/>

+ `WebMvcConfigurer`로 `/api/**` 요청에 CORS 정책을 공통 적용했습니다.
+ 로컬과 배포 환경에서 허용할 주소가 다르므로, 주소는 설정값으로 주입받도록 구성했습니다.

```java
@Value("${app.cors.allowed-origin:http://localhost:3000}")
private String allowedOrigin;

registry.addMapping("/api/**")
        .allowedOrigins(allowedOrigin)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false)
        .maxAge(3600);
```

<br/>
<br/>










