package problems;


import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Hogwarts {
    AtomicInteger permits = new AtomicInteger(4);
    private final Semaphore semaphore = new Semaphore(5);

    void requestHogsmeadePass() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        try {
            System.out.printf("Congrats %s. You got permission to visit Hogsmeade%n", Thread.currentThread().getName());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }

    void requestHogsmeadePass2() {
        while (true) {
            int curVal = permits.get();
            if (curVal == 0) continue;
            if (permits.compareAndSet(curVal, curVal - 1)) {
                try {
                    System.out.printf("Congrats %s. You got permission to visit Hogsmeade%n", Thread.currentThread().getName());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    permits.incrementAndGet();
                }
                return;
            }
        }
    }

    int counter = 5;

    void requestHogsmeadePass3() {
        synchronized (this) {
            while (counter == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            counter--;
        }

        try {
            System.out.printf("Congrats %s. You got permission to visit Hogsmeade%n", Thread.currentThread().getName());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            synchronized (this) {
                counter++;
                this.notify();
            }
        }
    }
}

public class BusyWait {
    public static void main(String[] args) {
        Hogwarts hogwarts = new Hogwarts();
        for (int i = 0; i < 10; i++) {
            new Thread(hogwarts::requestHogsmeadePass2).start();
        }
    }
}
