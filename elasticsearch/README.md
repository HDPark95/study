# Elasticsearch 클린 아키텍처 데모

이 프로젝트는 클린 아키텍처 원칙을 따라 Spring Boot와 함께 Elasticsearch를 사용하는 방법을 보여줍니다.

## 아키텍처

이 프로젝트는 다음과 같은 계층으로 구성된 클린 아키텍처를 따릅니다:

- **인터페이스 계층**: HTTP 요청과 응답을 처리하는 컨트롤러와 DTO
- **애플리케이션 계층**: 유스케이스를 구현하는 서비스
- **도메인 계층**: 핵심 비즈니스 모델과 레포지토리 인터페이스
- **인프라스트럭처 계층**: 레포지토리와 외부 서비스의 구현

## 사전 요구사항

- Java 24
- Docker 및 Docker Compose

## 애플리케이션 실행하기

### 1. Elasticsearch 시작하기

```bash
docker-compose up -d
```

이 명령어는 9200 포트에서 Elasticsearch 컨테이너를 시작합니다.

### 2. Spring Boot 애플리케이션 실행하기

```bash
./gradlew bootRun
```

애플리케이션은 8080 포트에서 시작됩니다.

## API 엔드포인트

### 문서 생성하기

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "Content-Type: application/json" \
  -d '{
    "title": "샘플 문서",
    "content": "이것은 Elasticsearch 인덱싱을 위한 샘플 문서입니다.",
    "author": "홍길동"
  }'
```

### ID로 문서 가져오기

```bash
curl -X GET http://localhost:8080/api/documents/{id}
```

### 모든 문서 가져오기

```bash
curl -X GET http://localhost:8080/api/documents
```

### 문서 검색하기

```bash
curl -X GET "http://localhost:8080/api/documents/search?query=샘플"
```

### 인덱스 정보 가져오기 (Cat API)

```bash
curl -X GET http://localhost:8080/api/documents/index-info
```

### 문서 삭제하기

```bash
curl -X DELETE http://localhost:8080/api/documents/{id}
```

## 프로젝트 구조

```
src/main/java/project/elasticsearch/
├── interfaces/
│   ├── controller/
│   │   └── DocumentController.java
│   └── dto/
│       ├── DocumentRequest.java
│       └── DocumentResponse.java
├── application/
│   └── service/
│       └── DocumentService.java
├── domain/
│   ├── model/
│   │   └── Document.java
│   └── repository/
│       └── DocumentRepository.java
└── infrastructure/
    ├── config/
    │   └── ElasticsearchConfig.java
    └── repository/
        ├── ElasticsearchDocument.java
        ├── ElasticsearchDocumentRepository.java
        └── SpringDataElasticsearchRepository.java
```
