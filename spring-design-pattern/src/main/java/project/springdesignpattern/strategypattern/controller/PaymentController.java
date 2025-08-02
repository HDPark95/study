package project.springdesignpattern.strategypattern.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.springdesignpattern.strategypattern.entity.Payment;
import project.springdesignpattern.strategypattern.service.PaymentService;

import java.util.List;
import java.util.Map;

/**
 * 결제 처리를 위한 REST 컨트롤러
 * 결제 처리 및 결제 정보 조회를 위한 엔드포인트를 제공합니다
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 결제를 처리합니다
     * 
     * @param paymentRequest 금액과 결제 방법을 포함한 결제 요청
     * @return 처리된 결제 정보
     */
    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequest paymentRequest) {
        Payment payment = paymentService.processPayment(
                paymentRequest.getAmount(),
                paymentRequest.getPaymentMethod()
        );
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    /**
     * 모든 결제 내역을 조회합니다
     * 
     * @return 모든 결제 내역 목록
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    /**
     * ID로 결제 내역을 조회합니다
     * 
     * @param id 결제 ID
     * @return 결제 내역이 존재하는 경우 해당 결제 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * 사용 가능한 결제 방법을 조회합니다
     * 
     * @return 사용 가능한 결제 방법 목록
     */
    @GetMapping("/methods")
    public ResponseEntity<List<String>> getAvailablePaymentMethods() {
        List<String> methods = paymentService.getAvailablePaymentMethods();
        return new ResponseEntity<>(methods, HttpStatus.OK);
    }

    /**
     * 지원되지 않는 결제 방법 예외를 처리합니다
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * 결제 요청 DTO
     */
    public static class PaymentRequest {
        private double amount;
        private String paymentMethod;

        // 게터와 세터 메소드
        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}