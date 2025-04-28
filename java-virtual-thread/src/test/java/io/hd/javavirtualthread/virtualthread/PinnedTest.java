package io.hd.javavirtualthread.virtualthread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PinnedTest {

    @Test
    void pinnedThreadTest() throws InterruptedException {
        Object lock = new Object();

        Thread thread = Thread.ofVirtual().start(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(1000); // pinning 유도
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.join();
    }

}
