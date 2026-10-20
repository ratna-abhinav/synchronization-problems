package demos;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicStampedReference;

class Gryffindor {
    public int count = 0;

    void incrementByOne() {
        if (count < 10) count++;
    }

    AtomicInteger counter = new AtomicInteger(0);

    void threadSafeIncrementByOne() {
        while (true) {
            int curVal = counter.get();
            if (curVal == 10) return;
            if (counter.compareAndSet(curVal, curVal + 1)) {
                return;
            }
        }
    }

    void threadSafeIncrementByOne2() {
        counter.updateAndGet(x -> (x < 10 ? x + 1 : x));
    }

    AtomicStampedReference<Integer> counter3 = new AtomicStampedReference<>(0, 0);
    void threadSafeAndABASafeIncrement() {
        while (true) {
            int[] stamp = new int[1];
            int curVal = counter3.get(stamp);
            int curStamp = stamp[0];
            if (curVal == 10) return;
            counter3.compareAndSet(curVal, curVal + 1, curStamp, curStamp + 1);
        }
    }
}

public class CASRetry {

    public static void main(String[] args) throws InterruptedException {
        Gryffindor house = new Gryffindor();
        for (int i = 0; i < 1000; i++) {
            Thread t = new Thread(() -> house.threadSafeAndABASafeIncrement());
            t.start();
        }
        Thread.sleep(2000);

        System.out.println("Final Count = " + house.counter3.getReference());

    }

}
