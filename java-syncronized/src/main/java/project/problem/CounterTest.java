package project.problem;

public class CounterTest {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        // 1000번씩 증가시키는 작업을 하는 두 개의 스레드
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();

        // 두 스레드가 끝날 때까지 대기
        t1.join();
        t2.join();

        System.out.println("최종 카운트: " + counter.getCount());
    }
}
