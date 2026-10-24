package problems;

/*

    Same as General Rendezvous Problem with extra condition that we need to wait for all the
    threads to complete the critical section before starting the next iteration of
    ( Rendezvous -> Critical Section ) execution

    Below solution is having deadlock.
    Assume that Thread-4 (0-based) executed notifyAll() in enterCriticalPoint() and lets say that all threads woke up
    so they'll try to acquire the lock and when they acquire they'll start at the moment they went to waiting state, hence
    they'll try to execute while (counter.counter > 0). But what if say Thread-0 got lock and tried checking while (counter.counter > 0),
    Thread-4 already executed next round of enterRendezvous() and made counter = 1. Then Thread-0 won't come out of while loop, it'll go
    to waiting state and Thread-4 will also go to waiting state when it executes while (counter.counter < 5) in enterCriticalPoint()
    so ultimately all threads will go to waiting state, leading to deadlock

    Below sol is functionally correct in the sense that Thread-4 was only able to go to next iteration and execute enterRendezvous()
    when counter was set to 0 and this happens only when all threads have come out of critical section
*/

import problems.common.Counter;

import java.util.ArrayList;
import java.util.List;

class RendezvousWaitTillCriticalSection extends RendezvousAndCriticalPoint{

    @Override
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        System.out.println(Thread.currentThread().getName() + " exited critical section :>");

        synchronized (this) {
            counter.counter--;
            System.out.println("For " + Thread.currentThread().getName() + " Current counter is " + counter.counter);

            while (counter.counter > 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println(Thread.currentThread().getName() + " is waking up all threads");
            this.notifyAll();
        }
    }
}

public class GeneralRendezvousDeadlock {

    public static void main(String[] args) {
        Counter counter = new Counter();
        RendezvousWaitTillCriticalSection r = new RendezvousWaitTillCriticalSection();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    System.out.println("---------------------------------- Start Iteration " + j + " for " + Thread.currentThread().getName() + " ---------------------------------------");
                    r.enterRendezvous(counter);
                    r.enterCriticalPoint(counter);
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
