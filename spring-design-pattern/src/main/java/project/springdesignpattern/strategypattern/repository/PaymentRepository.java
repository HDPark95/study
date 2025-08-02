package project.springdesignpattern.strategypattern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.springdesignpattern.strategypattern.entity.Payment;

/**
 * Payment 엔티티를 위한 레포지토리 인터페이스
 * Payment 엔티티에 대한 CRUD 작업을 제공합니다
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Spring Data JPA가 기본 CRUD 작업을 자동으로 구현합니다
    
    // 필요한 경우 여기에 사용자 정의 쿼리 메소드를 추가할 수 있습니다
    // 예시:
    // List<Payment> findByPaymentMethod(String paymentMethod);
    // List<Payment> findByStatus(String status);
}