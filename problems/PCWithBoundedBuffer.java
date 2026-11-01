package problems;


import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.*;

class BoundedBuffer {
    private final Semaphore mutex;
    private final Semaphore availableItems;
    private final Semaphore bufferHasSpace;
    private final Queue<Integer> queue;

    public BoundedBuffer(int size) {
        mutex = new Semaphore(1);
        availableItems = new Semaphore(0);
        bufferHasSpace = new Semaphore(size);
        queue = new ArrayDeque<>(size);
    }

    public void produce() throws InterruptedException {
        bufferHasSpace.acquire();
        try {
            mutex.acquire();
            try {
                Integer val = ThreadLocalRandom.current().nextInt(1, 1000);
                queue.offer(val);
                System.out.println(Thread.currentThread().getName() + " is producing " + val);
            } finally {
                mutex.release();
            }
        } catch (Throwable t) {
            bufferHasSpace.release();
            throw t;
        }
        availableItems.release();
    }

    void consume() throws InterruptedException {
        availableItems.acquire();
        try {
            mutex.acquire();
            try {
                Integer val = queue.poll();
                System.out.println(Thread.currentThread().getName() + " is consuming " + val);
            } finally {
                mutex.release();
            }
        } catch (Throwable t) {
            availableItems.release();
            throw t;
        }
        bufferHasSpace.release();
    }
}


public class PCWithBoundedBuffer {
    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer buffer = new BoundedBuffer(4);
        ThreadFactory factory = Thread.ofVirtual().name("vt-worker-", 1).factory();
        int producersCnt = 8, consumersCnt = 10;

        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {
            for (int i = 0; i < producersCnt; i++) {
                executor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            buffer.produce();
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            for (int i = 0; i < consumersCnt; i++) {
                executor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            buffer.consume();
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            Thread.sleep(2000);
            executor.shutdownNow();
        }
    }
}
