package problems;

/*

    Code:
    -> rendezvous
    -> critical section

    There are n threads. Requirement is that no thread can enter critical section till all the
    n threads execute and reach a rendezvous point
    Multiple threads can enter the critical section at the same time though.

 */

import problems.common.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GeneralRendezvousSolUseSemaphore {

    static Semaphore a = new Semaphore(1);
    static Semaphore b = new Semaphore(0);

    public static void main(String[] args) {
        int n = 5;
        Counter c = new Counter();
        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 1; i <= n; i++) {
            Thread t = new Thread(() -> {
                try {
                    a.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println(Thread.currentThread().getName() + "  has completed rendezvous :>");
                c.counter++;
                a.release();

                if (c.counter == n) {
                    b.release();
                }

                try {
                    b.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                b.release();

                System.out.println(Thread.currentThread().getName() + " is entering critical section :>");
            });
            threads.add(t);
        }

        for (Thread t : threads) {
            t.start();
        }
    }

}
