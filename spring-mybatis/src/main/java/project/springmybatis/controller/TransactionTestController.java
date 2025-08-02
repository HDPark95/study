package project.springmybatis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.springmybatis.service.TransactionTestService;

@RestController
@RequestMapping("/api/transaction-test")
public class TransactionTestController {

    @Autowired
    private TransactionTestService transactionTestService;

    @GetMapping("/with-transaction")
    public ResponseEntity<String> testWithTransaction() {
        try {
            transactionTestService.testWithTransaction();
            return ResponseEntity.ok("@Transactional 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("@Transactional 테스트 중 예외: " + e.getMessage());
        }
    }

    @GetMapping("/without-transaction")
    public ResponseEntity<String> testWithoutTransaction() {
        try {
            transactionTestService.testWithoutTransaction();
            return ResponseEntity.ok("Non-Transactional 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("Non-Transactional 테스트 중 예외: " + e.getMessage());
        }
    }

    @GetMapping("/multiple-with-transaction")
    public ResponseEntity<String> testMultipleWithTransaction() {
        try {
            transactionTestService.multipleOperationsWithTransaction();
            return ResponseEntity.ok("복합 작업 WITH Transaction 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("복합 작업 WITH Transaction 테스트 중 예외: " + e.getMessage());
        }
    }

    @GetMapping("/multiple-without-transaction")
    public ResponseEntity<String> testMultipleWithoutTransaction() {
        try {
            transactionTestService.multipleOperationsWithoutTransaction();
            return ResponseEntity.ok("복합 작업 WITHOUT Transaction 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("복합 작업 WITHOUT Transaction 테스트 중 예외: " + e.getMessage());
        }
    }

    @GetMapping("/exception-with-transaction")
    public ResponseEntity<String> testExceptionWithTransaction() {
        try {
            transactionTestService.testExceptionWithTransaction();
            return ResponseEntity.ok("예외 상황 WITH Transaction 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("예외 상황 WITH Transaction 테스트 중 예외: " + e.getMessage());
        }
    }

    @GetMapping("/exception-without-transaction")
    public ResponseEntity<String> testExceptionWithoutTransaction() {
        try {
            transactionTestService.testExceptionWithoutTransaction();
            return ResponseEntity.ok("예외 상황 WITHOUT Transaction 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("예외 상황 WITHOUT Transaction 테스트 중 예외: " + e.getMessage());
        }
    }
    
    @GetMapping("/check-sqlsession-with-transaction")
    public ResponseEntity<String> checkSqlSessionWithTransaction() {
        try {
            transactionTestService.checkSqlSessionWithTransaction();
            return ResponseEntity.ok("SqlSession 상태 확인 WITH @Transactional 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("SqlSession 상태 확인 WITH @Transactional 중 예외: " + e.getMessage());
        }
    }
    
    @GetMapping("/check-sqlsession-without-transaction")
    public ResponseEntity<String> checkSqlSessionWithoutTransaction() {
        try {
            transactionTestService.checkSqlSessionWithoutTransaction();
            return ResponseEntity.ok("SqlSession 상태 확인 WITHOUT @Transactional 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("SqlSession 상태 확인 WITHOUT @Transactional 중 예외: " + e.getMessage());
        }
    }
    
    @GetMapping("/manual-sqlsession")
    public ResponseEntity<String> testManualSqlSession() {
        try {
            transactionTestService.testManualSqlSession();
            return ResponseEntity.ok("수동 SqlSession 관리 테스트 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("수동 SqlSession 관리 테스트 중 예외: " + e.getMessage());
        }
    }
    
    @GetMapping("/cache-with-transaction")
    public ResponseEntity<String> testCacheWithTransaction() {
        try {
            transactionTestService.testCacheBehaviorWithTransaction();
            return ResponseEntity.ok("캐시 동작 확인 WITH @Transactional 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("캐시 동작 확인 WITH @Transactional 중 예외: " + e.getMessage());
        }
    }
    
    @GetMapping("/cache-without-transaction")
    public ResponseEntity<String> testCacheWithoutTransaction() {
        try {
            transactionTestService.testCacheBehaviorWithoutTransaction();
            return ResponseEntity.ok("캐시 동작 확인 WITHOUT @Transactional 완료");
        } catch (Exception e) {
            return ResponseEntity.ok("캐시 동작 확인 WITHOUT @Transactional 중 예외: " + e.getMessage());
        }
    }
}