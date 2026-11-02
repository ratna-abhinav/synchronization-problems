package patterns.common;

import java.util.concurrent.Semaphore;

public class LightSwitch {

    private final Semaphore mutex;
    private int counter;

    public LightSwitch() {
        mutex = new Semaphore(1);
        counter = 0;
    }

    public void lock(Semaphore semaphore) throws InterruptedException {
        mutex.acquire();
        try {
            counter++;
            if (counter == 1) semaphore.acquire();
        } catch (Throwable t) {
            counter--;
            throw t;
        } finally {
            mutex.release();
        }
    }

    public void unlock(Semaphore semaphore) {
        mutex.acquireUninterruptibly();
        counter--;
        if (counter == 0) semaphore.release();
        mutex.release();
    }
}
