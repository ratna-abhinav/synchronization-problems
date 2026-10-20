package problems;

/*

    Code:
    -> rendezvous
    -> critical section

    There are n threads. Requirement is that no thread executes critical section
    until after all threads have executed rendezvous. Multiple threads can enter the critical
    section though.

 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Counter {
    int counter = 0;
}

public class GeneralRendezvous {

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
