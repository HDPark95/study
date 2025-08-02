package project.springmybatis.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import project.springmybatis.domain.User;
import project.springmybatis.mapper.UserMapper;

import java.util.List;

@Service
public class TransactionTestService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    // @Transactional이 있는 경우
    @Transactional
    public void testWithTransaction() {
        System.out.println("=== @Transactional 있는 메서드 시작 ===");
        
        // 첫 번째 조회
        User user1 = userMapper.selectUserById(1L);
        System.out.println("첫 번째 조회: " + user1);
        
        // 두 번째 조회 (같은 트랜잭션 내)
        User user2 = userMapper.selectUserById(1L);
        System.out.println("두 번째 조회: " + user2);
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        System.out.println("업데이트 완료");
        
        // 업데이트 후 조회
        User user3 = userMapper.selectUserById(1L);
        System.out.println("업데이트 후 조회: " + user3);
        
        System.out.println("=== @Transactional 있는 메서드 종료 ===");
    }

    // @Transactional이 없는 경우
    public void testWithoutTransaction() {
        System.out.println("=== @Transactional 없는 메서드 시작 ===");
        
        // 첫 번째 조회
        User user1 = userMapper.selectUserById(2L);
        System.out.println("첫 번째 조회: " + user1);
        
        // 두 번째 조회 (각각 다른 SqlSession)
        User user2 = userMapper.selectUserById(2L);
        System.out.println("두 번째 조회: " + user2);
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        System.out.println("업데이트 완료");
        
        // 업데이트 후 조회
        User user3 = userMapper.selectUserById(2L);
        System.out.println("업데이트 후 조회: " + user3);
        
        System.out.println("=== @Transactional 없는 메서드 종료 ===");
    }

    // 복합 작업 - @Transactional 있음
    @Transactional
    public void multipleOperationsWithTransaction() {
        System.out.println("=== 복합 작업 WITH Transaction ===");
        
        List<User> users = userMapper.selectAllUsers();
        System.out.println("전체 사용자 조회: " + users.size() + "명");
        
        for (User user : users) {
            user.setAge(user.getAge() + 1);
            userMapper.updateUser(user);
        }
        
        System.out.println("모든 사용자 나이 +1 완료");
        
        // 같은 트랜잭션 내에서 다시 조회
        List<User> updatedUsers = userMapper.selectAllUsers();
        System.out.println("업데이트 후 조회: " + updatedUsers.size() + "명");
        
        System.out.println("=== 복합 작업 WITH Transaction 종료 ===");
    }

    // 복합 작업 - @Transactional 없음
    public void multipleOperationsWithoutTransaction() {
        System.out.println("=== 복합 작업 WITHOUT Transaction ===");
        
        List<User> users = userMapper.selectAllUsers();
        System.out.println("전체 사용자 조회: " + users.size() + "명");
        
        for (User user : users) {
            user.setAge(user.getAge() + 1);
            userMapper.updateUser(user);
        }
        
        System.out.println("모든 사용자 나이 +1 완료");
        
        // 각각 새로운 SqlSession으로 조회
        List<User> updatedUsers = userMapper.selectAllUsers();
        System.out.println("업데이트 후 조회: " + updatedUsers.size() + "명");
        
        System.out.println("=== 복합 작업 WITHOUT Transaction 종료 ===");
    }

    // 예외 상황 테스트 - @Transactional 있음
    @Transactional
    public void testExceptionWithTransaction() {
        System.out.println("=== 예외 상황 WITH Transaction ===");
        
        User user = userMapper.selectUserById(3L);
        System.out.println("원본 사용자: " + user);
        
        user.setAge(999);
        userMapper.updateUser(user);
        System.out.println("나이를 999로 변경");
        
        // 의도적으로 예외 발생
        throw new RuntimeException("의도적 예외 발생!");
    }

    // 예외 상황 테스트 - @Transactional 없음
    public void testExceptionWithoutTransaction() {
        System.out.println("=== 예외 상황 WITHOUT Transaction ===");
        
        User user = userMapper.selectUserById(4L);
        System.out.println("원본 사용자: " + user);
        
        user.setAge(999);
        userMapper.updateUser(user);
        System.out.println("나이를 999로 변경");
        
        // 의도적으로 예외 발생
        throw new RuntimeException("의도적 예외 발생!");
    }
    
    // SqlSession 상태 확인 - @Transactional 있음
    @Transactional
    public void checkSqlSessionWithTransaction() {
        System.out.println("=== SqlSession 상태 확인 WITH @Transactional ===");
        
        // 트랜잭션 상태 확인
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("트랜잭션 활성화 상태: " + isTransactionActive);
        
        // 첫 번째 쿼리 실행
        System.out.println("첫 번째 쿼리 실행");
        User user1 = userMapper.selectUserById(1L);
        System.out.println("조회 결과: " + user1);
        
        // 두 번째 쿼리 실행 (같은 SqlSession 사용 여부 확인)
        System.out.println("두 번째 쿼리 실행");
        User user2 = userMapper.selectUserById(1L);
        System.out.println("조회 결과: " + user2);
        System.out.println("객체 동일성 (==): " + (user1 == user2));
        System.out.println("객체 동등성 (equals): " + user1.equals(user2));
        
        System.out.println("=== SqlSession 상태 확인 WITH @Transactional 종료 ===");
    }
    
    // SqlSession 상태 확인 - @Transactional 없음
    public void checkSqlSessionWithoutTransaction() {
        System.out.println("=== SqlSession 상태 확인 WITHOUT @Transactional ===");
        
        // 트랜잭션 상태 확인
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        System.out.println("트랜잭션 활성화 상태: " + isTransactionActive);
        
        // 첫 번째 쿼리 실행
        System.out.println("첫 번째 쿼리 실행");
        User user1 = userMapper.selectUserById(1L);
        System.out.println("조회 결과: " + user1);
        
        // 두 번째 쿼리 실행 (각각 다른 SqlSession 사용)
        System.out.println("두 번째 쿼리 실행");
        User user2 = userMapper.selectUserById(1L);
        System.out.println("조회 결과: " + user2);
        System.out.println("객체 동일성 (==): " + (user1 == user2));
        System.out.println("객체 동등성 (equals): " + user1.equals(user2));
        
        System.out.println("=== SqlSession 상태 확인 WITHOUT @Transactional 종료 ===");
    }
    
    // 수동 SqlSession 관리 테스트
    public void testManualSqlSession() {
        System.out.println("=== 수동 SqlSession 관리 테스트 ===");
        
        // autoCommit = true인 SqlSession
        System.out.println("--- autoCommit = true ---");
        try (SqlSession autoCommitSession = sqlSessionFactory.openSession(true)) {
            UserMapper mapper1 = autoCommitSession.getMapper(UserMapper.class);
            
            User user = mapper1.selectUserById(1L);
            System.out.println("조회: " + user);
            
            user.setAge(user.getAge() + 1);
            mapper1.updateUser(user);
            System.out.println("업데이트 완료 (자동 커밋)");
            
            User updatedUser = mapper1.selectUserById(1L);
            System.out.println("업데이트 후 조회: " + updatedUser);
        }
        
        // autoCommit = false인 SqlSession
        System.out.println("--- autoCommit = false ---");
        try (SqlSession manualCommitSession = sqlSessionFactory.openSession(false)) {
            UserMapper mapper2 = manualCommitSession.getMapper(UserMapper.class);
            
            User user = mapper2.selectUserById(2L);
            System.out.println("조회: " + user);
            
            user.setAge(user.getAge() + 1);
            mapper2.updateUser(user);
            System.out.println("업데이트 완료 (커밋 전)");
            
            // 같은 세션에서 조회
            User beforeCommit = mapper2.selectUserById(2L);
            System.out.println("커밋 전 같은 세션 조회: " + beforeCommit);
            
            // 다른 세션에서 조회 (커밋 전)
            try (SqlSession anotherSession = sqlSessionFactory.openSession(true)) {
                UserMapper anotherMapper = anotherSession.getMapper(UserMapper.class);
                User fromAnotherSession = anotherMapper.selectUserById(2L);
                System.out.println("커밋 전 다른 세션 조회: " + fromAnotherSession);
            }
            
            // 수동 커밋
            manualCommitSession.commit();
            System.out.println("수동 커밋 완료");
            
            // 커밋 후 조회
            User afterCommit = mapper2.selectUserById(2L);
            System.out.println("커밋 후 같은 세션 조회: " + afterCommit);
        }
        
        System.out.println("=== 수동 SqlSession 관리 테스트 종료 ===");
    }
    
    // 캐시 동작 확인
    @Transactional
    public void testCacheBehaviorWithTransaction() {
        System.out.println("=== 캐시 동작 확인 WITH @Transactional ===");
        
        // 첫 번째 조회 (DB에서 가져옴)
        System.out.println("첫 번째 조회");
        User user1 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user1);
        
        // 두 번째 조회 (1차 캐시에서 가져옴)
        System.out.println("두 번째 조회 (1차 캐시 확인)");
        User user2 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user2);
        System.out.println("동일한 객체 인스턴스: " + (user1 == user2));
        
        // 업데이트 후 캐시 상태 확인
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        System.out.println("업데이트 실행");
        
        // 업데이트 후 조회 (캐시가 무효화되었는지 확인)
        System.out.println("업데이트 후 조회");
        User user3 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user3);
        System.out.println("user1과 user3 동일 객체: " + (user1 == user3));
        
        System.out.println("=== 캐시 동작 확인 WITH @Transactional 종료 ===");
    }
    
    public void testCacheBehaviorWithoutTransaction() {
        System.out.println("=== 캐시 동작 확인 WITHOUT @Transactional ===");
        
        // 첫 번째 조회
        System.out.println("첫 번째 조회");
        User user1 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user1);
        
        // 두 번째 조회 (새로운 SqlSession)
        System.out.println("두 번째 조회 (새로운 SqlSession)");
        User user2 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user2);
        System.out.println("동일한 객체 인스턴스: " + (user1 == user2));
        
        // 업데이트
        user1.setAge(user1.getAge() + 1);
        userMapper.updateUser(user1);
        System.out.println("업데이트 실행");
        
        // 업데이트 후 조회
        System.out.println("업데이트 후 조회");
        User user3 = userMapper.selectUserById(1L);
        System.out.println("결과: " + user3);
        
        System.out.println("=== 캐시 동작 확인 WITHOUT @Transactional 종료 ===");
    }
}