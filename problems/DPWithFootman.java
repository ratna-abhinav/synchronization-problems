package problems;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class DiningPhilosopher2 {
    private final Semaphore footman;
    private final Semaphore[] forks;
    int n;

    public DiningPhilosopher2(int n) {
        this.n = n;
        footman = new Semaphore(n - 1);
        forks = new Semaphore[n];
        for (int i = 0; i < n; i++) {
            forks[i] = new Semaphore(1);
        }
    }

    int leftFork(int i) {
        return i;
    }

    int rightFork(int i) {
        return (i + 1) % n;
    }

    public void pickForks(int i) throws InterruptedException {
        footman.acquire();
        try {
            forks[leftFork(i)].acquire();
            try {
                forks[rightFork(i)].acquire();
            } catch (InterruptedException e) {
                forks[leftFork(i)].release();
                throw e;
            }
        } catch (InterruptedException e) {
            footman.release();
            throw e;
        }
    }

    public void putForks(int i) {
        forks[leftFork(i)].release();
        forks[rightFork(i)].release();
        footman.release();
    }
}

public class DPWithFootman {

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        DiningPhilosopher2 dp = new DiningPhilosopher2(n);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < n; i++) {
                int philosopher = i;
                executor.submit(() -> {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            // thinking
                            Thread.sleep((long) (Math.random() * 500));
                            // want to eat
                            dp.pickForks(philosopher);
                            try {
                                // eating
                                System.out.println("Philosopher " + philosopher + " is eating :>");
                                Thread.sleep((long) (Math.random() * 500));
                            } finally {
                                dp.putForks(philosopher);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            Thread.sleep(10000);
            executor.shutdownNow();
        }
    }
}
