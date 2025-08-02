package thread.executor.poolsize;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolSizeMainV1 {
    public static void main(String[] args) {
        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(2);
        ExecutorService es = new ThreadPoolExecutor(2, 4, 3000, TimeUnit.MILLISECONDS, arrayBlockingQueue);

        es.execute(new RunnableTask("task1"));

        es.execute(new RunnableTask("task2"));

        es.execute(new RunnableTask("task3"));

        es.execute(new RunnableTask("task4"));

        es.execute(new RunnableTask("task5"));

    }
}
