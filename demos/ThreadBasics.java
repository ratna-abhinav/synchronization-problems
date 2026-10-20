package demos;

import java.util.concurrent.locks.ReentrantLock;

class Demo {
    ReentrantLock lock = new ReentrantLock(true);

    void write() {
        System.out.println("First entered " + Thread.currentThread().getName());
        if (lock.tryLock()) {
            try {
                
                System.out.println("Lock acquired by " + Thread.currentThread().getName());
            } catch (Exception e) {

            } finally {
                lock.unlock();
            }

        } else {
            System.out.println("didn't acquired by " + Thread.currentThread().getName());
        }

    }
}

public class ThreadBasics {

    public static void main(String[] args) throws InterruptedException {
        Demo demo = new Demo();
        Thread t1 = new Thread(() -> demo.write());
        Thread t2 = new Thread(() -> demo.write());

        t1.start();
        t2.start();
    }

}
