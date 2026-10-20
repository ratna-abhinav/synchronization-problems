package problems;


import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class Hogwarts {
    AtomicInteger permits = new AtomicInteger(5);
    private final Semaphore semaphore = new Semaphore(5);

    Hogwarts() {
    }

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

    private class Pass {
    }

    private BlockingQueue<Pass> passQueue = new ArrayBlockingQueue<>(5);

    Hogwarts(int counter) {
        for (int i = 0; i < counter; i++) {
            passQueue.add(new Pass());
        }
    }

    void requestHogsmeadePass4() {
        Pass curPass = null;
        try {
            curPass = passQueue.take();
            System.out.printf("Congrats %s. You got permission to visit Hogsmeade%n", Thread.currentThread().getName());
            Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            if (curPass != null) {
                try {
                    passQueue.put(curPass);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

/*

    requestHogsmeadePass(), requestHogsmeadePass2(), requestHogsmeadePass3(), requestHogsmeadePass4() implement the same thing logically,
    but requestHogsmeadePass2() is having busy-wait problem when current thread after checking if count==0) continuously loops

 */

public class BusyWait {
    public static void main(String[] args) {
        Hogwarts hogwarts = new Hogwarts(5);
        for (int i = 0; i < 10; i++) {
            new Thread(hogwarts::requestHogsmeadePass4).start();
        }
    }
}
