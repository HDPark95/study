package project.solution.spinlock;

public class SpinLockTest {
    public static void main(String[] args) throws InterruptedException {
        UserCounter counter = new UserCounter();
        String userId = "user123";

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment(userId);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment(userId);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(userId + "의 카운트 값: " + counter.getCount(userId)); // 예상: 2000
    }
}
