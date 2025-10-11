# Cucumber Practice Project

Cucumber를 활용한 BDD(Behavior Driven Development) 테스트 실습 프로젝트입니다.

## 프로젝트 구조

```
cucumber-practice/
├── src/
│   ├── main/
│   │   ├── java/project/cucumber/
│   │   │   ├── domain/          # 도메인 모델
│   │   │   ├── repository/      # 레포지토리
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   └── CucumberPracticeApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/project/cucumber/
│       │   ├── runner/          # Cucumber 테스트 러너
│       │   └── steps/           # Step Definitions
│       └── resources/
│           ├── features/        # Gherkin 시나리오
│           └── junit-platform.properties
└── build.gradle
```

## 주요 기능

### 1. 계좌 관리 시스템
- 계좌 생성
- 입금/출금
- 잔액 조회
- 예외 처리 (잔액 부족 등)

## Cucumber 테스트 실행

### Gradle을 통한 실행
```bash
./gradlew test
```

### 테스트 리포트 확인
테스트 실행 후 생성되는 리포트:
- HTML 리포트: `build/reports/cucumber.html`
- JSON 리포트: `build/reports/cucumber.json`
- JUnit 리포트: `build/reports/tests/test/index.html`

## Cucumber Feature 파일 구조

```gherkin
# language: ko
기능: 계좌 관리
  은행 고객으로서
  계좌를 관리하고 싶다
  입출금 및 잔액 조회를 할 수 있도록

  배경:
    조건 계좌번호 "123-456-789"와 소유자 "홍길동"으로 초기 잔액 10000원인 계좌가 있다

  시나리오: 계좌에 입금하기
    만약 계좌 "123-456-789"에 5000원을 입금하면
    그러면 계좌 "123-456-789"의 잔액은 15000원이어야 한다
```

## Step Definitions 작성 예제

```java
@given("계좌번호 {string}와 소유자 {string}으로 초기 잔액 {int}원인 계좌가 있다")
public void 계좌가_있다(String accountNumber, String owner, Integer initialBalance) {
    accountService.createAccount(accountNumber, owner, initialBalance);
}

@when("계좌 {string}에 {int}원을 입금하면")
public void 계좌에_입금하면(String accountNumber, Integer amount) {
    accountService.deposit(accountNumber, amount);
}

@then("계좌 {string}의 잔액은 {int}원이어야 한다")
public void 계좌의_잔액은_원이어야_한다(String accountNumber, Integer expectedBalance) {
    int actualBalance = accountService.getBalance(accountNumber);
    assertThat(actualBalance).isEqualTo(expectedBalance);
}
```

## BDD 핵심 개념

### Gherkin 키워드
- **기능 (Feature)**: 테스트할 기능에 대한 설명
- **배경 (Background)**: 모든 시나리오에 공통적으로 적용되는 전제조건
- **시나리오 (Scenario)**: 개별 테스트 케이스
- **조건/전제 (Given)**: 초기 상태 설정
- **만약/때 (When)**: 실행할 동작
- **그러면 (Then)**: 예상되는 결과

### Scenario Outline
여러 데이터 조합으로 같은 시나리오를 반복 실행:
```gherkin
시나리오 개요: 여러 금액 입금 테스트
  만약 계좌 "123-456-789"에 <입금액>원을 입금하면
  그러면 계좌 "123-456-789"의 잔액은 <최종잔액>원이어야 한다

  예:
    | 입금액 | 최종잔액 |
    | 1000  | 11000  |
    | 5000  | 15000  |
```

## 새로운 테스트 추가하기

### 📝 Step 1: Feature 파일 작성

`src/test/resources/features/` 디렉토리에 새로운 `.feature` 파일을 생성합니다.

**예제: 송금 기능 테스트 (`transfer.feature`)**

```gherkin
# language: ko
기능: 계좌 송금
  은행 고객으로서
  다른 계좌로 송금하고 싶다
  안전하게 금액을 이체할 수 있도록

  배경:
    조건 계좌번호 "111-111-111"와 소유자 "김철수"로 초기 잔액 50000원인 계좌가 있다
    그리고 계좌번호 "222-222-222"와 소유자 "이영희"로 초기 잔액 30000원인 계좌가 있다

  시나리오: 송금 성공
    만약 계좌 "111-111-111"에서 계좌 "222-222-222"로 10000원을 송금하면
    그러면 계좌 "111-111-111"의 잔액은 40000원이어야 한다
    그리고 계좌 "222-222-222"의 잔액은 40000원이어야 한다

  시나리오: 잔액 부족으로 송금 실패
    만약 계좌 "111-111-111"에서 계좌 "222-222-222"로 60000원을 송금하려고 하면
    그러면 "잔액이 부족합니다."라는 예외가 발생해야 한다
```

### 🔧 Step 2: 필요한 비즈니스 로직 구현

새로운 기능이 필요하다면 먼저 구현합니다.

**예제: `AccountService.java`에 송금 메서드 추가**

```java
public void transfer(String fromAccountNumber, String toAccountNumber, int amount) {
    Account fromAccount = getAccount(fromAccountNumber);
    Account toAccount = getAccount(toAccountNumber);

    fromAccount.withdraw(amount);  // 출금
    toAccount.deposit(amount);     // 입금
}
```

### 🎯 Step 3: Step Definitions 구현

`src/test/java/project/cucumber/steps/` 디렉토리에 Step Definition 클래스를 작성합니다.

**옵션 1: 기존 파일에 추가**
- `AccountStepDefinitions.java`에 새로운 메서드 추가

**옵션 2: 새로운 파일 생성**
- 관심사를 분리하고 싶다면 `TransferStepDefinitions.java` 같은 새 파일 생성

**예제: Step Definitions 추가**

```java
// AccountStepDefinitions.java 또는 TransferStepDefinitions.java

@when("계좌 {string}에서 계좌 {string}로 {int}원을 송금하면")
public void 계좌에서_계좌로_송금하면(String fromAccount, String toAccount, Integer amount) {
    accountService.transfer(fromAccount, toAccount, amount);
}

@when("계좌 {string}에서 계좌 {string}로 {int}원을 송금하려고 하면")
public void 계좌에서_계좌로_송금하려고_하면(String fromAccount, String toAccount, Integer amount) {
    try {
        accountService.transfer(fromAccount, toAccount, amount);
    } catch (Exception e) {
        thrownException = e;
    }
}

@then("계좌 {string}의 잔액은 {int}원이어야 한다")
public void 계좌의_잔액_확인(String accountNumber, Integer expectedBalance) {
    int actualBalance = accountService.getBalance(accountNumber);
    assertThat(actualBalance).isEqualTo(expectedBalance);
}
```

### ✅ Step 4: 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 feature만 실행 (Gradle 설정 필요)
./gradlew test -Dcucumber.filter.tags="@transfer"
```

### 🏷️ Step 5: 태그 활용 (선택사항)

Feature나 Scenario에 태그를 추가하여 선택적으로 실행할 수 있습니다.

```gherkin
@transfer @smoke
시나리오: 송금 성공
  만약 계좌 "111-111-111"에서 계좌 "222-222-222"로 10000원을 송금하면
  그러면 계좌 "111-111-111"의 잔액은 40000원이어야 한다
```

**build.gradle에 태그 필터 설정:**

```gradle
tasks.named('test') {
    useJUnitPlatform()
    systemProperty 'cucumber.filter.tags', System.getProperty('cucumber.filter.tags', '')
}
```

**특정 태그로 실행:**
```bash
./gradlew test -Dcucumber.filter.tags="@smoke"
```

## 테스트 작성 팁

### 💡 Step Definition 재사용
- 같은 문구는 자동으로 같은 Step Definition에 매핑됩니다
- 파라미터를 활용하여 범용적인 Step을 작성하세요

### 🎨 Gherkin 작성 원칙
1. **명확성**: 비즈니스 용어를 사용하여 누구나 이해할 수 있게
2. **독립성**: 각 시나리오는 독립적으로 실행 가능해야 함
3. **반복 제거**: 공통 전제조건은 `배경`으로 추출

### 🔍 디버깅
- Step Definition이 매칭되지 않으면 Cucumber가 자동으로 스켈레톤 코드를 제안합니다
- 테스트 실행 시 콘솔 출력을 확인하세요

### 📊 Data Tables 활용

복잡한 데이터 구조는 Data Tables로 표현:

```gherkin
시나리오: 여러 계좌 생성
  조건 다음 계좌들이 있다
    | 계좌번호      | 소유자  | 초기잔액 |
    | 111-111-111 | 김철수  | 10000  |
    | 222-222-222 | 이영희  | 20000  |
    | 333-333-333 | 박민수  | 30000  |
```

```java
@given("다음 계좌들이 있다")
public void 다음_계좌들이_있다(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps();
    for (Map<String, String> row : rows) {
        accountService.createAccount(
            row.get("계좌번호"),
            row.get("소유자"),
            Integer.parseInt(row.get("초기잔액"))
        );
    }
}
```

## 추가 학습 자료

### 다양한 시나리오 추가하기
1. 새로운 feature 파일 생성 (`src/test/resources/features/`)
2. Gherkin 문법으로 시나리오 작성
3. Step Definitions 구현 (`src/test/java/project/cucumber/steps/`)
4. 테스트 실행

### Cucumber 한글 지원
- Feature 파일 첫 줄에 `# language: ko` 추가
- 한글 키워드 사용 가능: 기능, 시나리오, 조건, 만약, 그러면

## 의존성

- Spring Boot 3.2.0
- Cucumber 7.15.0
- JUnit 5
- Lombok
- AssertJ

## 참고사항

- 모든 테스트는 Spring Context를 사용하여 실행됩니다
- `@CucumberContextConfiguration`으로 Spring과 Cucumber 통합
- `@Before` 훅으로 각 시나리오 전에 데이터 초기화
