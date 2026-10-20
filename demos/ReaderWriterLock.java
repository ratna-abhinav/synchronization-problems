package demos;
import java.util.concurrent.locks.*;

class SharedResource {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void method1() {
        rwLock.readLock().lock();
        try {
            System.out.println("Reading: " + Thread.currentThread().getName());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void method2() {
        rwLock.writeLock().lock();
        try {
            rwLock.readLock().lock();
            System.out.println("Writing: " + Thread.currentThread().getName());
        } finally {
            rwLock.readLock().unlock();
            rwLock.writeLock().unlock();
        }
    }
}

public class ReaderWriterLock {

    public static void main(String[] args) {
        SharedResource sr = new SharedResource();
        Thread r1 = new Thread(sr::method1);
        Thread r2 = new Thread(sr::method1);
        Thread w1 = new Thread(sr::method2);

//        w1.start();
        r1.start();
        r2.start();
    }

}
