package project.solution.spinlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCounter {
    // 사용자별 카운터 저장소
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();

    // 사용자별 SpinLock 저장소
    private final Map<String, SpinLock> locks = new ConcurrentHashMap<>();

    public void increment(String userId) {
        SpinLock lock = locks.computeIfAbsent(userId, key -> new SpinLock());

        lock.lock();
        try {
            int current = counters.getOrDefault(userId, 0);
            counters.put(userId, current + 1);
        } finally {
            lock.unlock();
        }
    }

    public void decrement(String userId) {
        SpinLock lock = locks.computeIfAbsent(userId, key -> new SpinLock());

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