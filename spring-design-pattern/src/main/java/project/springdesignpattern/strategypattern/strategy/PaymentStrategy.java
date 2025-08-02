package project.springdesignpattern.strategypattern.strategy;

/**
 * 결제 처리를 위한 전략 인터페이스
 * 모든 결제 전략이 구현해야 하는 계약을 정의합니다
 */
public interface PaymentStrategy {
    
    /**
     * 주어진 금액으로 결제를 처리합니다
     * 
     * @param amount 결제 금액
     * @return 결제가 성공하면 true, 그렇지 않으면 false
     */
    boolean pay(double amount);
    
    /**
     * 결제 방법의 이름을 가져옵니다
     * 
     * @return 결제 방법 이름
     */
    String getMethodName();
}