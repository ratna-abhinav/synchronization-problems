package problems;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

class UnboundedBuffer {
    private final Semaphore mutex;
    private final Semaphore availableItems;
    private final Queue<Integer> queue;

    public UnboundedBuffer() {
        mutex = new Semaphore(1);
        availableItems = new Semaphore(0);
        queue = new LinkedList<>();
    }

    void produce() {
        try {
            mutex.acquire();
            int val = ThreadLocalRandom.current().nextInt(1, 100);
            System.out.println(Thread.currentThread().getName() + " is producing " + val);
            queue.offer(val);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
        availableItems.release();
    }

    void consume() {
        try {
            availableItems.acquire();
            mutex.acquire();

            Integer val = queue.poll();
            System.out.println(Thread.currentThread().getName() + " is consuming " + val);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
    }
}


public class PCWithUnboundedBuffer {
    public static void main(String[] args) throws InterruptedException {
        UnboundedBuffer buffer = new UnboundedBuffer();
        ThreadFactory factory = Thread.ofVirtual().name("vt-worker-", 1).factory();

        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        buffer.produce();
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        buffer.consume();
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
            Thread.sleep(2000);
            executor.shutdownNow();
        }
    }
}
