package project.springdesignpattern.strategypattern.service;

import org.springframework.stereotype.Service;
import project.springdesignpattern.strategypattern.entity.Payment;
import project.springdesignpattern.strategypattern.repository.PaymentRepository;
import project.springdesignpattern.strategypattern.strategy.PaymentStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PaymentService 인터페이스의 구현체
 * 전략 패턴을 사용하여 다양한 결제 방법으로 결제를 처리합니다
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentStrategy> paymentStrategies;

    /**
     * 결제 레포지토리와 결제 전략들을 주입하는 생성자
     * Spring이 자동으로 PaymentStrategy 타입의 모든 빈을 Map으로 주입합니다
     */
    public PaymentServiceImpl(PaymentRepository paymentRepository, Map<String, PaymentStrategy> paymentStrategies) {
        this.paymentRepository = paymentRepository;
        this.paymentStrategies = paymentStrategies;
    }

    /**
     * 지정된 결제 방법을 사용하여 결제를 처리합니다
     * 전략 패턴을 사용하여 적절한 결제 전략을 선택합니다
     */
    @Override
    public Payment processPayment(double amount, String paymentMethod) {
        // 새로운 결제 기록 생성
        Payment payment = new Payment(amount, paymentMethod);
        
        // 적절한 결제 전략 가져오기
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        
        if (strategy == null) {
            throw new IllegalArgumentException("지원되지 않는 결제 방법: " + paymentMethod);
        }
        
        // 선택된 전략을 사용하여 결제 처리
        boolean success = strategy.pay(amount);
        
        // 결과에 따라 결제 상태 업데이트
        payment.setStatus(success ? "COMPLETED" : "FAILED");
        
        // 결제 기록을 데이터베이스에 저장
        return paymentRepository.save(payment);
    }

    /**
     * 모든 결제 내역을 조회합니다
     */
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * ID로 결제 내역을 조회합니다
     */
    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    /**
     * 사용 가능한 결제 방법을 조회합니다
     */
    @Override
    public List<String> getAvailablePaymentMethods() {
        return List.copyOf(paymentStrategies.keySet());
    }
}