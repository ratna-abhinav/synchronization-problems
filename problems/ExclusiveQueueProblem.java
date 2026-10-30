package problems;

import java.util.concurrent.Semaphore;

class ExclusiveQueue {
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore leaderQueue = new Semaphore(0);
    private final Semaphore followerQueue = new Semaphore(0);
    private final Semaphore rendezvous = new Semaphore(0);
    private int leadersWaiting = 0, followersWaiting = 0;

    void leaderArrived() throws InterruptedException {
        mutex.acquire();
        if (followersWaiting > 0) {
            followersWaiting--;
            followerQueue.release();
        } else {
            leadersWaiting++;
            mutex.release();
            leaderQueue.acquire();
        }
        dance();
        rendezvous.acquire();
        mutex.release();  // only release mutex once both the follower and leader have danced
    }

    void followerArrived() throws InterruptedException {
        mutex.acquire();
        if (leadersWaiting > 0) {
            leadersWaiting--;
            leaderQueue.release();
        } else {
            followersWaiting++;
            mutex.release();
            followerQueue.acquire();
        }
        dance();
        rendezvous.release();
    }

    private void dance() {
        System.out.printf("[%s] is dancing!%n", Thread.currentThread().getName());
    }
}


public class ExclusiveQueueProblem {
    public static void main(String[] args) throws InterruptedException {
        ExclusiveQueue ballRoom = new ExclusiveQueue();
        int couples = 4;
        for (int i = 0; i < couples; i++) {
            Thread leader = new Thread(() -> {
                try {
                    ballRoom.leaderArrived();
                } catch (Exception e) {
                }
            }, "Leader-" + i);

            Thread follower = new Thread(() -> {
                try {
                    ballRoom.followerArrived();
                } catch (Exception e) {
                }
            }, "Follower-" + i);

            leader.start();
            follower.start();
        }
    }
}
