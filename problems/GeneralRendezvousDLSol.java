package problems;


import problems.common.Counter;

import java.util.ArrayList;
import java.util.List;

class GeneralRendezvous {

    synchronized void rendezvous(Counter counter) {
        while (counter.allArrivedAtRendezvous) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println(Thread.currentThread().getName() + " completed rendezvous :> Current counter = " + counter.counter);
        counter.counter++;
        if (counter.counter == 5) {
            counter.allArrivedAtRendezvous = true;
            this.notifyAll();
        }
    }

    void criticalSection(Counter counter) {
        synchronized (this) {
            while (!counter.allArrivedAtRendezvous) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        System.out.println(Thread.currentThread().getName() + " entered critical section :>");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        System.out.println(Thread.currentThread().getName() + " exited critical section :>");

        synchronized (this) {
            counter.counter--;
            if (counter.counter == 0) {
                counter.allArrivedAtRendezvous = false;
                this.notifyAll();
            } else {
                while (counter.allArrivedAtRendezvous) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

}

public class GeneralRendezvousDLSol {
    public static void main(String[] args) {
        GeneralRendezvous r = new GeneralRendezvous();
        Counter counter = new Counter();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    System.out.println("---------------------------------- Start Iteration " + j + " for " + Thread.currentThread().getName() + " ---------------------------------------");
                    r.rendezvous(counter);
                    r.criticalSection(counter);
                    System.out.println("---------------------------------- End Iteration " + j + " for " + Thread.currentThread().getName() + " ---------------------------------------");
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }
    }
}
