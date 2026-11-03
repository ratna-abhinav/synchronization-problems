package problems;

import patterns.common.LightSwitch;

import java.util.concurrent.*;

class ReaderWriter1 {
    private final Semaphore mayEnter;
    private final LightSwitch readLightSwitch;
    private int value;

    public ReaderWriter1() {
        mayEnter = new Semaphore(1);
        readLightSwitch = new LightSwitch();
        value = 0;
    }

    public void read() throws InterruptedException {
        readLightSwitch.lock(mayEnter);
        try {
            System.out.println(Thread.currentThread().getName() + " entering critical section. Value = " + value);
            Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + " exiting critical section :>");
        } finally {
            readLightSwitch.unlock(mayEnter);
        }
    }

    public void write(int x) throws InterruptedException {
        mayEnter.acquire();
        value = x;
        System.out.println(Thread.currentThread().getName() + " entering critical section :>");
        Thread.sleep(100);
        System.out.println(Thread.currentThread().getName() + " exiting critical section :>");
        mayEnter.release();
    }
}

public class ReaderWriterWithStarvation {
    public static void main(String[] args) throws InterruptedException {
        ReaderWriter2 rw = new ReaderWriter2();
        ThreadFactory readerFactory = Thread.ofVirtual().name("reader-", 1).factory();
        ThreadFactory writerFactory = Thread.ofVirtual().name("writer-", 1).factory();
        int writers = 4, readers = 10;

        try (ExecutorService writerExecutor = Executors.newThreadPerTaskExecutor(writerFactory);
             ExecutorService readerExecutor = Executors.newThreadPerTaskExecutor(readerFactory)) {
            for (int i = 0; i < writers; i++) {
                int x = ThreadLocalRandom.current().nextInt(1, 1000);
                writerExecutor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            rw.write(x);
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            for (int i = 0; i < readers; i++) {
                readerExecutor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            rw.read();
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            Thread.sleep(3000);
            readerExecutor.shutdownNow();
            writerExecutor.shutdownNow();
        }
    }
}
