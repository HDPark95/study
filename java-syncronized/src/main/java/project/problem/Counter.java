package project.problem;

public class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;  // 동시성 문제 발생 가능 지점
    }

    public int getCount() {
        return count;
    }
}