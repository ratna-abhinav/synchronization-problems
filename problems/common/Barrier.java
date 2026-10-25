package problems.common;

import java.util.concurrent.Semaphore;

public class Barrier {
    private final int n;
    private int count;
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore turnstile1 = new Semaphore(0);
    private final Semaphore turnstile2 = new Semaphore(0);

    public Barrier(int n) {
        this.n = n;
        count = 0;
    }

    public void phase1() throws InterruptedException {
        mutex.acquire();
        try {
            count++;
            if (count == n) {
                turnstile1.release(n);
            }
        } finally {
            mutex.release();
        }
        turnstile1.acquire();
    }

    public void phase2() throws InterruptedException {
        mutex.acquire();
        try {
            count--;
            if (count == 0) {
                turnstile2.release(n);
            }
        } finally {
            mutex.release();
        }
        turnstile2.acquire();
    }

    public void await() throws InterruptedException {
        this.phase1();
        this.phase2();
    }
}
