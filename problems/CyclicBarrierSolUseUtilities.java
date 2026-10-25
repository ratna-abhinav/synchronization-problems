package problems;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class GeneralRendezvous3 {
    private int n;
    private final CyclicBarrier entryBarrier;
    private final CyclicBarrier exitBarrier;

    public GeneralRendezvous3(int n) {
        this.n = n;
        entryBarrier = new CyclicBarrier(n, () -> System.out.println("All n threads completed rendezvous"));
        exitBarrier = new CyclicBarrier(n, () -> System.out.println("All n threads completed critical section"));
    }

    void enterRendezvous() throws InterruptedException, BrokenBarrierException {
        System.out.println(Thread.currentThread().getName() + " completed rendezvous :>");
        entryBarrier.await();
    }

    void enterCriticalSection() throws InterruptedException, BrokenBarrierException {
        System.out.println(Thread.currentThread().getName() + " entered critical section :>");
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " completed critical section :>");
        exitBarrier.await();
    }
}

public class CyclicBarrierSolUseUtilities {
    public static void main(String[] args) {
        int n = 5;
        GeneralRendezvous3 r = new GeneralRendezvous3(n);
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    System.out.println(Thread.currentThread().getName() + " is starting iteration " + j);
                    try {
                        r.enterRendezvous();
                        r.enterCriticalSection();
                    } catch (Exception e) {
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
