package project.springmybatis.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import project.springmybatis.domain.User;
import project.springmybatis.mapper.UserMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TransactionTestService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    // @Transactional이 있는 경우
    @Transactional
    public void testWithTransaction() {
        log.info("=== @Transactional 있는 메서드 시작 ===");
        
        // 첫 번째 조회
        User user1 = userMapper.selectUserById(1L);
        log.info("첫 번째 조회: {}", user1);
        
        // 두 번째 조회 (같은 트랜잭션 내)
        User user2 = userMapper.selectUserById(1L);
        log.info("두 번째 조회: {}", user2);
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        log.info("업데이트 완료");
        
        // 업데이트 후 조회
        User user3 = userMapper.selectUserById(1L);
        log.info("업데이트 후 조회: {}", user3);
        
        log.info("=== @Transactional 있는 메서드 종료 ===");
    }

    // @Transactional이 없는 경우
    public void testWithoutTransaction() {
        log.info("=== @Transactional 없는 메서드 시작 ===");
        
        // 첫 번째 조회
        User user1 = userMapper.selectUserById(2L);
        log.info("첫 번째 조회: {}", user1);
        
        // 두 번째 조회 (각각 다른 SqlSession)
        User user2 = userMapper.selectUserById(2L);
        log.info("두 번째 조회: {}", user2);
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        log.info("업데이트 완료");
        
        // 업데이트 후 조회
        User user3 = userMapper.selectUserById(2L);
        log.info("업데이트 후 조회: {}", user3);
        
        log.info("=== @Transactional 없는 메서드 종료 ===");
    }

    // 복합 작업 - @Transactional 있음
    @Transactional
    public void multipleOperationsWithTransaction() {
        log.info("=== 복합 작업 WITH Transaction ===");
        
        List<User> users = userMapper.selectAllUsers();
        log.info("전체 사용자 조회: {}명", users.size());
        
        for (User user : users) {
            user.setAge(user.getAge() + 1);
            userMapper.updateUser(user);
        }
        
        log.info("모든 사용자 나이 +1 완료");
        
        // 같은 트랜잭션 내에서 다시 조회
        List<User> updatedUsers = userMapper.selectAllUsers();
        log.info("업데이트 후 조회: {}명", updatedUsers.size());
        
        log.info("=== 복합 작업 WITH Transaction 종료 ===");
    }

    // 복합 작업 - @Transactional 없음
    public void multipleOperationsWithoutTransaction() {
        log.info("=== 복합 작업 WITHOUT Transaction ===");
        
        List<User> users = userMapper.selectAllUsers();
        log.info("전체 사용자 조회: {}명", users.size());
        
        for (User user : users) {
            user.setAge(user.getAge() + 1);
            userMapper.updateUser(user);
        }
        
        log.info("모든 사용자 나이 +1 완료");
        
        // 각각 새로운 SqlSession으로 조회
        List<User> updatedUsers = userMapper.selectAllUsers();
        log.info("업데이트 후 조회: {}명", updatedUsers.size());
        
        log.info("=== 복합 작업 WITHOUT Transaction 종료 ===");
    }

    // 예외 상황 테스트 - @Transactional 있음
    @Transactional
    public void testExceptionWithTransaction() {
        log.info("=== 예외 상황 WITH Transaction ===");
        
        User user = userMapper.selectUserById(3L);
        log.info("원본 사용자: " + user);
        
        user.setAge(999);
        userMapper.updateUser(user);
        log.info("나이를 999로 변경");
        
        // 의도적으로 예외 발생
        throw new RuntimeException("의도적 예외 발생!");
    }

    // 예외 상황 테스트 - @Transactional 없음
    public void testExceptionWithoutTransaction() {
        log.info("=== 예외 상황 WITHOUT Transaction ===");
        
        User user = userMapper.selectUserById(4L);
        log.info("원본 사용자: " + user);
        
        user.setAge(999);
        userMapper.updateUser(user);
        log.info("나이를 999로 변경");
        
        // 의도적으로 예외 발생
        throw new RuntimeException("의도적 예외 발생!");
    }
    
    // SqlSession 상태 확인 - @Transactional 있음
    @Transactional
    public void checkSqlSessionWithTransaction() {
        log.info("=== SqlSession 상태 확인 WITH @Transactional ===");
        
        // 트랜잭션 상태 확인
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("트랜잭션 활성화 상태: {}", isTransactionActive);
        
        // 첫 번째 쿼리 실행
        log.info("첫 번째 쿼리 실행");
        User user1 = userMapper.selectUserById(1L);
        log.info("조회 결과: " + user1);
        
        // 두 번째 쿼리 실행 (같은 SqlSession 사용 여부 확인)
        log.info("두 번째 쿼리 실행");
        User user2 = userMapper.selectUserById(1L);
        log.info("조회 결과: " + user2);
        log.info("객체 동일성 (==): {}", (user1 == user2));
        log.info("객체 동등성 (equals): {}", user1.equals(user2));
        
        log.info("=== SqlSession 상태 확인 WITH @Transactional 종료 ===");
    }
    
    // SqlSession 상태 확인 - @Transactional 없음
    public void checkSqlSessionWithoutTransaction() {
        log.info("=== SqlSession 상태 확인 WITHOUT @Transactional ===");
        
        // 트랜잭션 상태 확인
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("트랜잭션 활성화 상태: {}", isTransactionActive);
        
        // 첫 번째 쿼리 실행
        log.info("첫 번째 쿼리 실행");
        User user1 = userMapper.selectUserById(1L);
        log.info("조회 결과: " + user1);
        
        // 두 번째 쿼리 실행 (각각 다른 SqlSession 사용)
        log.info("두 번째 쿼리 실행");
        User user2 = userMapper.selectUserById(1L);
        log.info("조회 결과: " + user2);
        log.info("객체 동일성 (==): {}", (user1 == user2));
        log.info("객체 동등성 (equals): {}", user1.equals(user2));
        
        log.info("=== SqlSession 상태 확인 WITHOUT @Transactional 종료 ===");
    }
    
    // 수동 SqlSession 관리 테스트
    public void testManualSqlSession() {
        log.info("=== 수동 SqlSession 관리 테스트 ===");
        
        // autoCommit = true인 SqlSession
        log.info("--- autoCommit = true ---");
        try (SqlSession autoCommitSession = sqlSessionFactory.openSession(true)) {
            UserMapper mapper1 = autoCommitSession.getMapper(UserMapper.class);
            
            User user = mapper1.selectUserById(1L);
            log.info("조회: " + user);
            
            user.setAge(user.getAge() + 1);
            mapper1.updateUser(user);
            log.info("업데이트 완료 (자동 커밋)");
            
            User updatedUser = mapper1.selectUserById(1L);
            log.info("업데이트 후 조회: " + updatedUser);
        }
        
        // autoCommit = false인 SqlSession
        log.info("--- autoCommit = false ---");
        try (SqlSession manualCommitSession = sqlSessionFactory.openSession(false)) {
            UserMapper mapper2 = manualCommitSession.getMapper(UserMapper.class);
            
            User user = mapper2.selectUserById(2L);
            log.info("조회: " + user);
            
            user.setAge(user.getAge() + 1);
            mapper2.updateUser(user);
            log.info("업데이트 완료 (커밋 전)");
            
            // 같은 세션에서 조회
            User beforeCommit = mapper2.selectUserById(2L);
            log.info("커밋 전 같은 세션 조회: " + beforeCommit);
            
            // 다른 세션에서 조회 (커밋 전)
            try (SqlSession anotherSession = sqlSessionFactory.openSession(true)) {
                UserMapper anotherMapper = anotherSession.getMapper(UserMapper.class);
                User fromAnotherSession = anotherMapper.selectUserById(2L);
                log.info("커밋 전 다른 세션 조회: " + fromAnotherSession);
            }
            
            // 수동 커밋
            manualCommitSession.commit();
            log.info("수동 커밋 완료");
            
            // 커밋 후 조회
            User afterCommit = mapper2.selectUserById(2L);
            log.info("커밋 후 같은 세션 조회: " + afterCommit);
        }
        
        log.info("=== 수동 SqlSession 관리 테스트 종료 ===");
    }
    
    // 캐시 동작 확인
    @Transactional
    public void testCacheBehaviorWithTransaction() {
        log.info("=== 캐시 동작 확인 WITH @Transactional ===");
        
        // 첫 번째 조회 (DB에서 가져옴)
        log.info("첫 번째 조회");
        User user1 = userMapper.selectUserById(1L);
        log.info("결과: " + user1);
        
        // 두 번째 조회 (1차 캐시에서 가져옴)
        log.info("두 번째 조회 (1차 캐시 확인)");
        User user2 = userMapper.selectUserById(1L);
        log.info("결과: " + user2);
        log.info("동일한 객체 인스턴스: " + (user1 == user2));
        
        // 업데이트 후 캐시 상태 확인
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        log.info("업데이트 실행");
        
        // 업데이트 후 조회 (캐시가 무효화되었는지 확인)
        log.info("업데이트 후 조회");
        User user3 = userMapper.selectUserById(1L);
        log.info("결과: " + user3);
        log.info("user1과 user3 동일 객체: " + (user1 == user3));
        
        log.info("=== 캐시 동작 확인 WITH @Transactional 종료 ===");
    }
    
    public void testCacheBehaviorWithoutTransaction() {
        log.info("=== 캐시 동작 확인 WITHOUT @Transactional ===");
        
        // 첫 번째 조회
        log.info("첫 번째 조회");
        User user1 = userMapper.selectUserById(1L);
        log.info("결과: " + user1);
        
        // 두 번째 조회 (새로운 SqlSession)
        log.info("두 번째 조회 (새로운 SqlSession)");
        User user2 = userMapper.selectUserById(1L);
        log.info("결과: " + user2);
        log.info("동일한 객체 인스턴스: " + (user1 == user2));
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        log.info("업데이트 실행");
        
        // 업데이트 후 조회
        log.info("업데이트 후 조회");
        User user3 = userMapper.selectUserById(1L);
        log.info("결과: " + user3);
        
        log.info("=== 캐시 동작 확인 WITHOUT @Transactional 종료 ===");
    }
    
    // 성능 비교 테스트 - @Transactional 있음
    @Transactional
    public void performanceTestWithTransaction() {
        log.info("=== 성능 테스트 WITH @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            // 같은 데이터를 반복 조회 (1차 캐시 효과 확인)
            User user = userMapper.selectUserById(1L);
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("트랜잭션 있음 - {}회 조회 소요시간: {}ms", iterations, duration);
        log.info("트랜잭션 있음 - 평균 조회시간: {}ms", duration / (double) iterations);
        log.info("=== 성능 테스트 WITH @Transactional 종료 ===");
    }
    
    // 성능 비교 테스트 - @Transactional 없음
    public void performanceTestWithoutTransaction() {
        log.info("=== 성능 테스트 WITHOUT @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            // 같은 데이터를 반복 조회 (매번 새로운 SqlSession)
            User user = userMapper.selectUserById(1L);
        }
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("트랜잭션 없음 - {}회 조회 소요시간: {}ms", iterations, duration);
        log.info("트랜잭션 없음 - 평균 조회시간: {}ms", duration / (double) iterations);
        log.info("=== 성능 테스트 WITHOUT @Transactional 종료 ===");
    }
    
    // 복합 성능 테스트 - @Transactional 있음
    @Transactional
    public void complexPerformanceTestWithTransaction() {
        log.info("=== 복합 성능 테스트 WITH @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        
        // 다양한 조회 작업
        User user1 = userMapper.selectUserById(1L);  // 첫 번째 조회
        User user2 = userMapper.selectUserById(1L);  // 캐시에서 조회
        User user3 = userMapper.selectUserById(2L);  // 다른 데이터 조회
        User user4 = userMapper.selectUserById(1L);  // 다시 캐시에서 조회
        
        List<User> users = userMapper.selectAllUsers();  // 전체 조회
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("복합 작업 (트랜잭션 있음) 소요시간: {}ms", duration);
        log.info("=== 복합 성능 테스트 WITH @Transactional 종료 ===");
    }
    
    // 복합 성능 테스트 - @Transactional 없음
    public void complexPerformanceTestWithoutTransaction() {
        log.info("=== 복합 성능 테스트 WITHOUT @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        
        // 다양한 조회 작업 (매번 새로운 SqlSession)
        User user1 = userMapper.selectUserById(1L);  // 첫 번째 조회
        User user2 = userMapper.selectUserById(1L);  // 새로운 SqlSession으로 조회
        User user3 = userMapper.selectUserById(2L);  // 다른 데이터 조회
        User user4 = userMapper.selectUserById(1L);  // 또 새로운 SqlSession으로 조회
        
        List<User> users = userMapper.selectAllUsers();  // 전체 조회
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("복합 작업 (트랜잭션 없음) 소요시간: {}ms", duration);
        log.info("=== 복합 성능 테스트 WITHOUT @Transactional 종료 ===");
    }
    
    // SqlSession 생성 횟수 비교 테스트
    @Transactional
    public void sqlSessionCountTestWithTransaction() {
        log.info("=== SqlSession 생성 횟수 테스트 WITH @Transactional 시작 ===");
        log.info("트랜잭션 활성화 상태: {}", TransactionSynchronizationManager.isActualTransactionActive());
        
        // 여러 번 조회하지만 같은 SqlSession 사용
        for (int i = 1; i <= 5; i++) {
            User user = userMapper.selectUserById((long) i);
            log.info("{}번째 조회 완료: {}", i, user.getName());
        }
        
        log.info("=== SqlSession 생성 횟수 테스트 WITH @Transactional 종료 ===");
    }
    
    // SqlSession 생성 횟수 비교 테스트
    public void sqlSessionCountTestWithoutTransaction() {
        log.info("=== SqlSession 생성 횟수 테스트 WITHOUT @Transactional 시작 ===");
        log.info("트랜잭션 활성화 상태: {}", TransactionSynchronizationManager.isActualTransactionActive());
        
        // 매번 새로운 SqlSession 생성
        for (int i = 1; i <= 5; i++) {
            User user = userMapper.selectUserById((long) i);
            log.info("{}번째 조회 완료: {}", i, user.getName());
        }
        
        log.info("=== SqlSession 생성 횟수 테스트 WITHOUT @Transactional 종료 ===");
    }
    
    // 대량 데이터 처리 성능 비교 - @Transactional 있음
    @Transactional
    public void bulkDataTestWithTransaction() {
        log.info("=== 대량 데이터 처리 테스트 WITH @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        
        // 같은 사용자 정보를 50번 조회 (1차 캐시 효과 극대화)
        for (int i = 0; i < 50; i++) {
            User user = userMapper.selectUserById(1L);
        }
        
        // 전체 사용자 목록 조회
        List<User> users = userMapper.selectAllUsers();
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("대량 처리 (트랜잭션 있음) 소요시간: {}ms", duration);
        log.info("=== 대량 데이터 처리 테스트 WITH @Transactional 종료 ===");
    }
    
    // 대량 데이터 처리 성능 비교 - @Transactional 없음
    public void bulkDataTestWithoutTransaction() {
        log.info("=== 대량 데이터 처리 테스트 WITHOUT @Transactional 시작 ===");
        
        long startTime = System.nanoTime();
        
        // 같은 사용자 정보를 50번 조회 (매번 새로운 SqlSession)
        for (int i = 0; i < 50; i++) {
            User user = userMapper.selectUserById(1L);
        }
        
        // 전체 사용자 목록 조회
        List<User> users = userMapper.selectAllUsers();
        
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        
        log.info("대량 처리 (트랜잭션 없음) 소요시간: {}ms", duration);
        log.info("=== 대량 데이터 처리 테스트 WITHOUT @Transactional 종료 ===");
    }
}