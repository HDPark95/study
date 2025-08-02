package project.springdesignpattern.strategypattern.service;

import project.springdesignpattern.strategypattern.entity.Payment;

import java.util.List;
import java.util.Optional;

/**
 * 결제 처리를 위한 서비스 인터페이스
 * 결제 처리 및 결제 정보 조회를 위한 메소드를 정의합니다
 */
public interface PaymentService {
    
    /**
     * 지정된 결제 방법을 사용하여 결제를 처리합니다
     * 
     * @param amount 결제 금액
     * @param paymentMethod 사용할 결제 방법 (예: "KakaoPay", "NaverPay")
     * @return 처리된 결제 엔티티
     */
    Payment processPayment(double amount, String paymentMethod);
    
    /**
     * 모든 결제 내역을 조회합니다
     * 
     * @return 모든 결제 내역 목록
     */
    List<Payment> getAllPayments();
    
    /**
     * ID로 결제 내역을 조회합니다
     * 
     * @param id 결제 ID
     * @return 결제 내역이 존재하면 Optional에 포함하여 반환, 없으면 빈 Optional 반환
     */
    Optional<Payment> getPaymentById(Long id);
    
    /**
     * 사용 가능한 결제 방법을 조회합니다
     * 
     * @return 사용 가능한 결제 방법 이름 목록
     */
    List<String> getAvailablePaymentMethods();
}