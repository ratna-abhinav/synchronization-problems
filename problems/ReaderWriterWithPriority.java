package problems;

/*
    We want to prioritize writer thread execution over reader thread
*/

import patterns.common.LightSwitch;

import java.util.concurrent.*;

class ReaderWriter3 {
    private final LightSwitch writerSwitch;
    private final LightSwitch readerSwitch;
    private final Semaphore readerMayEnter;
    private final Semaphore writerMayEnter;
    private int value;

    public ReaderWriter3() {
        this.writerSwitch = new LightSwitch();
        this.readerSwitch = new LightSwitch();
        this.readerMayEnter = new Semaphore(1);
        this.writerMayEnter = new Semaphore(1);
    }

    public void read() throws InterruptedException {
        readerMayEnter.acquire();
        readerSwitch.lock(writerMayEnter);
        readerMayEnter.release();

        try {
            System.out.println(Thread.currentThread().getName() + " is reading value = " + value);
            Thread.sleep(150);
        } finally {
            readerSwitch.unlock(writerMayEnter);
        }
    }

    public void write(int x) throws InterruptedException {
        writerSwitch.lock(readerMayEnter);
        try {
            writerMayEnter.acquire();
            try {
                value = x;
                System.out.println(Thread.currentThread().getName() + " is writing value = " + x);
                Thread.sleep(500);
            } finally {
                writerMayEnter.release();
            }
        } finally {
            writerSwitch.unlock(readerMayEnter);
        }
    }
}


public class ReaderWriterWithPriority {
    public static void main(String[] args) throws InterruptedException {
        ReaderWriter3 rw = new ReaderWriter3();
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
                            Thread.sleep(2500);
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
            writerExecutor.shutdownNow();
            readerExecutor.shutdownNow();
        }
    }
}
