package problems;

/*
    Thread A
    -> stmt a1
    -> stmt a2

    Thread B
    -> stmt b1
    -> stmt b2

    We want to guarantee that a1 happens before b2 and b1 happens before a2.
 */

import java.util.concurrent.Semaphore;

public class Rendezvous {
    static Semaphore a1Done = new Semaphore(0);
    static Semaphore b1Done = new Semaphore(0);

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println("a1 executed :>");
            a1Done.release();

            try {
                b1Done.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("a2 is executed :>");
        });

        Thread t2 = new Thread(() -> {
            System.out.println("b1 executed :>");
            b1Done.release();
            
            try {
                a1Done.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("b2 is executed :>");
        });

        t1.start();
        t2.start();
    }

}
