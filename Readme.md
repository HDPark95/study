# 스프링 쿼츠

스프링은 기본적으로 Spring Scheduler를 제공한다. 쿼츠보다 훨씬 간단하고 쉽게 사용할 수 있다.
하지만 서비스가 이중화된다면, 스케줄러 코드가 의도한 것보다 여러번 수행될 것이다.
쿼츠는 DB를 활용한 클러스터링 방식으로, 이중화 환경에서도 하나의 서버만 작업을 수행하게 할 수 있따.
또한 복잡한 트리거 설정과 스케줄링 옵션을 제공한다.

## 쿼츠란?

쿼츠는 Java로 구현된 오픈소스 스케줄링 라이브러리이다.


### 쿼츠의 핵심 API

| 용어            | 설명                               |
|-----------------|----------------------------------|
| **Scheduler**   | 스케줄러와 상호 작용하기 위한 주요 API      |
| **Job**         | 수행할 작업에 대한 정보를 포함하는 클래스          |
| **JobDetail**   | Job의 인스턴스를 정의하는 데 사용됩니다.         |
| **Trigger**     | 특정 Job이 실행될 스케줄을 정의하는 컴포넌트   |
| **JobBuilder**  | JobDetail 인스턴스를 정의/구축하는 데 사용 |
| **TriggerBuilder** | Trigger 인스턴스를 정의/구축하는 데 사용   |

#### [Job]

```java

public class ExampleJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}

```

#### [JobDetail]



Trigger

Scheduler