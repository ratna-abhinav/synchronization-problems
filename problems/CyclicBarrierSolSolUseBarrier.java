package problems;


import problems.common.Barrier;

import java.util.ArrayList;
import java.util.List;

class GeneralRendezvous2 {
    void enterRendezvous(Barrier barrier) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " completed rendezvous :>");
        barrier.await();
    }

    void enterCriticalSection(Barrier barrier) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " entered critical section :>");
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " completed critical section :>");
        barrier.await();
    }
}

public class CyclicBarrierSolSolUseBarrier {
    public static void main(String[] args) {
        int n = 4;
        Barrier entryBarrier = new Barrier(n);
        Barrier exitBarrier = new Barrier(n);
        GeneralRendezvous2 r = new GeneralRendezvous2();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    System.out.println(Thread.currentThread().getName() + " is starting iteration " + j);
                    try {
                        r.enterRendezvous(entryBarrier);
                        r.enterCriticalSection(exitBarrier);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }
    }
}
