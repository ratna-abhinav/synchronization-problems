package problems;

import java.util.concurrent.*;

class Buffer {
    private BlockingQueue<Integer> queue;

    public Buffer(int size) {
        queue = new ArrayBlockingQueue<>(size);
    }

    void produce(Integer val) throws InterruptedException {
        queue.put(val);
        System.out.println(Thread.currentThread().getName() + " produced " + val);
    }

    void consume() throws InterruptedException {
        Integer val = queue.take();
        System.out.println(Thread.currentThread().getName() + " consumes " + val);
    }

}

public class ProducerConsumer {
    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer(5);
        int producerCnt = 10, consumerCnt = 18;

        ThreadFactory producerFactory = Thread.ofVirtual().name("producer-", 1).factory();
        ThreadFactory consumerFactory = Thread.ofVirtual().name("consumer-", 1).factory();

        try (ExecutorService producerExecutor = Executors.newThreadPerTaskExecutor(producerFactory);
             ExecutorService consumerExecutor = Executors.newThreadPerTaskExecutor(consumerFactory)) {
            for (int i = 0; i < producerCnt; i++) {
                producerExecutor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            Integer val = ThreadLocalRandom.current().nextInt(1, 1000);
                            buffer.produce(val);
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            for (int i = 0; i < consumerCnt; i++) {
                consumerExecutor.submit(() -> {
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
            producerExecutor.shutdownNow();
            consumerExecutor.shutdownNow();
        }

    }
}
