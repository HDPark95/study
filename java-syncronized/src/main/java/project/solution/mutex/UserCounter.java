package project.solution.mutex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class UserCounter {
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();

    // 사용자별 공정 락 저장소 (공정성 보장: true)
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public void increment(String userId) {
        ReentrantLock lock = locks.computeIfAbsent(userId, key -> new ReentrantLock(true)); // 공정 락

        lock.lock();
        try {
            int current = counters.getOrDefault(userId, 0);
            counters.put(userId, current + 1);
        } finally {
            lock.unlock();
        }
    }

    public void decrement(String userId) {
        ReentrantLock lock = locks.computeIfAbsent(userId, key -> new ReentrantLock(true)); // 공정 락

        lock.lock();
        try {
            int current = counters.getOrDefault(userId, 0);
            counters.put(userId, current - 1);
        } finally {
            lock.unlock();
        }
    }

    public int getCount(String userId) {
        return counters.getOrDefault(userId, 0);
    }
}
