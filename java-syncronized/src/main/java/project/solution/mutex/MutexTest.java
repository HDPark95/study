package project.solution.mutex;

public class MutexTest {
    public static void main(String[] args) throws InterruptedException {
        UserCounter counter = new UserCounter();
        String userId = "user123";

        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment(userId);
            }
        });

        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.decrement(userId);
            }
        });

        incrementThread.start();
        decrementThread.start();

        incrementThread.join();
        decrementThread.join();

        int finalCount = counter.getCount(userId);
        System.out.println(userId + "의 최종 카운트 값: " + finalCount); // 예상: 0
    }
}
