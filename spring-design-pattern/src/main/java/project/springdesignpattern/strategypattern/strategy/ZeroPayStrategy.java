package project.springdesignpattern.strategypattern.strategy;

import org.springframework.stereotype.Component;

/**
 * 제로페이 결제 처리를 위한 구체적인 전략
 */
@Component
public class ZeroPayStrategy implements PaymentStrategy {

    private static final String METHOD_NAME = "ZeroPay";

    @Override
    public boolean pay(double amount) {
        // 실제 애플리케이션에서는 제로페이 API와 연동됩니다
        System.out.println(amount + "원을 제로페이로 결제 처리 중");
        
        // 결제 처리 시뮬레이션
        // 실제 애플리케이션에서는 API 호출, 유효성 검사 등을 처리합니다
        return processZeroPayment(amount);
    }

    @Override
    public String getMethodName() {
        return METHOD_NAME;
    }
    
    private boolean processZeroPayment(double amount) {
        // 제로페이 API 연동 시뮬레이션
        // 실제 애플리케이션에서는 실제 연동 코드가 포함됩니다
        System.out.println("제로페이 결제가 성공적으로 처리되었습니다");
        return true;
    }
}