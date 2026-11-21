package problems;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

enum State {
    THINKING,
    EATING,
    HUNGRY
}

class DiningPhilosophers {
    int n;
    private final State[] state;
    private final Condition[] condition;
    private final Queue<Integer> waitQueue;
    private final ReentrantLock lock;

    public DiningPhilosophers(int n) {
        this.n = n;
        state = new State[n];
        lock = new ReentrantLock();
        condition = new Condition[n];
        for (int i = 0; i < n; i++) {
            state[i] = State.THINKING;
            condition[i] = lock.newCondition();
        }
        waitQueue = new LinkedList<>();
    }

    public void pickForks(int i) throws InterruptedException {
        lock.lock();
        try {
            state[i] = State.HUNGRY;
            waitQueue.offer(i);

            try {
                while (!canEat(i)) {
                    // ith philosopher will wait since both forks are not free
                    condition[i].await();
                }
            } catch (Throwable e) {
                state[i] = State.THINKING;
                int leftNb = (i + n - 1) % n, rightNb = (i + 1) % n;
                condition[leftNb].signal();
                condition[rightNb].signal();
                throw e;
            }
            state[i] = State.EATING;
        } finally {
            waitQueue.remove(i);
            lock.unlock();
        }
    }

    public void putForks(int i) {
        lock.lock();
        try {
            state[i] = State.THINKING;
            System.out.println("Philosopher " + i + " is thinking :>");
            int leftNb = (i + n - 1) % n, rightNb = (i + 1) % n;
            condition[leftNb].signal();
            condition[rightNb].signal();
        } finally {
            lock.unlock();
        }
    }

    private boolean canEat(int i) {
        int leftNb = (i + n - 1) % n, rightNb = (i + 1) % n;
        if (state[leftNb] == State.EATING || state[rightNb] == State.EATING) return false;
        for (int nb : waitQueue) {
            if (nb == leftNb || nb == rightNb) return false;
            if (nb == i) return true;
        }
        return true;
    }
}


public class DPModifiedTanenbaum {

    public static void main(String[] args) throws InterruptedException {
        int n = 5;
        DiningPhilosophers dp = new DiningPhilosophers(n);
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
