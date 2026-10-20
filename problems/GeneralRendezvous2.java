package problems;


import java.util.ArrayList;
import java.util.List;

class RendezvousAndCriticalPoint {

    synchronized void enterRendezvous(Counter counter) {
        counter.counter++;
        System.out.println(Thread.currentThread().getName() + " completed rendezvous :> Current counter = " + counter.counter);

        if (counter.counter == 5) {
            this.notifyAll();
        }
    }

    void enterCriticalPoint(Counter counter) {
        synchronized (this) {
            while (counter.counter < 5) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        // Multiple threads can enter below critical section concurrently
        System.out.println(Thread.currentThread().getName() + " entered critical section :>");
    }
}

public class GeneralRendezvous2 {
    public static void main(String[] args) {
        int n = 5;
        Counter counter = new Counter();
        List<Thread> threads = new ArrayList<Thread>();
        RendezvousAndCriticalPoint r = new RendezvousAndCriticalPoint();

        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                r.enterRendezvous(counter);
                r.enterCriticalPoint(counter);
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }
    }
}
