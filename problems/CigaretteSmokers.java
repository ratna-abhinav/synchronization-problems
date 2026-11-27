package problems;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class Agent {

    public Semaphore agentSem;
    public Semaphore tobacco;
    public Semaphore match;
    public Semaphore paper;

    public Agent() {
        this.agentSem = new Semaphore(1);
        this.tobacco = new Semaphore(0);
        this.match = new Semaphore(0);
        this.paper = new Semaphore(0);
    }

    public void agentA() throws InterruptedException {
        agentSem.acquire();
        tobacco.release();
        match.release();
    }

    public void agentB() throws InterruptedException {
        agentSem.acquire();
        tobacco.release();
        paper.release();
    }

    public void agentC() throws InterruptedException {
        agentSem.acquire();
        paper.release();
        match.release();
    }
}

class Pushers {

    public Semaphore tobaccoSmokerCanStart;
    public Semaphore matchSmokerCanStart;
    public Semaphore paperSmokerCanStart;

    private final Semaphore agentCreatedTobacco;
    private final Semaphore agentCreatedMatch;
    private final Semaphore agentCreatedPaper;

    private final Semaphore mutex;
    private boolean tobaccoAvailable;
    private boolean matchAvailable;
    private boolean paperAvailable;

    public Pushers(Agent agent) {
        this.tobaccoSmokerCanStart = new Semaphore(0);
        this.matchSmokerCanStart = new Semaphore(0);
        this.paperSmokerCanStart = new Semaphore(0);
        this.mutex = new Semaphore(1);
        this.agentCreatedTobacco = agent.tobacco;
        this.agentCreatedMatch = agent.match;
        this.agentCreatedPaper = agent.paper;
        this.tobaccoAvailable = false;
        this.matchAvailable = false;
        this.paperAvailable = false;
    }

    public void pusherA() throws InterruptedException {
        agentCreatedTobacco.acquire();

        try {
            mutex.acquire();
            try {
                if (matchAvailable) {
                    matchAvailable = false;
                    paperSmokerCanStart.release();
                } else if (paperAvailable) {
                    paperAvailable = false;
                    matchSmokerCanStart.release();
                } else {
                    tobaccoAvailable = true;
                }
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            agentCreatedTobacco.release();
            throw e;
        }
        System.out.println("Pusher A is signalling the corresponding smoker ...");
    }

    public void pusherB() throws InterruptedException {
        agentCreatedMatch.acquire();

        try {
            mutex.acquire();
            try {
                if (tobaccoAvailable) {
                    tobaccoAvailable = false;
                    paperSmokerCanStart.release();
                } else if (paperAvailable) {
                    paperAvailable = false;
                    tobaccoSmokerCanStart.release();
                } else {
                    matchAvailable = true;
                }
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            agentCreatedMatch.release();
            throw e;
        }
        System.out.println("Pusher B is signalling the corresponding smoker ...");
    }


    public void pusherC() throws InterruptedException {
        agentCreatedPaper.acquire();

        try {
            mutex.acquire();
            try {
                if (tobaccoAvailable) {
                    tobaccoAvailable = false;
                    matchSmokerCanStart.release();
                } else if (matchAvailable) {
                    matchAvailable = false;
                    tobaccoSmokerCanStart.release();
                } else {
                    paperAvailable = true;
                }
            } finally {
                mutex.release();
            }
        } catch (InterruptedException e) {
            agentCreatedPaper.release();
            throw e;
        }
        System.out.println("Pusher C is signalling the corresponding smoker ...");
    }
}

class Smokers {

    private final Semaphore tobaccoSmokerCanStart;
    private final Semaphore matchSmokerCanStart;
    private final Semaphore paperSmokerCanStart;
    private final Semaphore agentSem;

    public Smokers(Pushers pusher, Agent agent) {
        this.tobaccoSmokerCanStart = pusher.tobaccoSmokerCanStart;
        this.matchSmokerCanStart = pusher.matchSmokerCanStart;
        this.paperSmokerCanStart = pusher.paperSmokerCanStart;
        this.agentSem = agent.agentSem;
    }

    public void smokerWithTobacco() throws InterruptedException {
        tobaccoSmokerCanStart.acquire();
        try {
            makeCigarettes();
        } finally {
            agentSem.release();
        }
        System.out.println("Tobacco smoker is smoking it :<");
    }

    public void smokerWithMatch() throws InterruptedException {
        matchSmokerCanStart.acquire();
        try {
            makeCigarettes();
        } finally {
            agentSem.release();
        }
        System.out.println("Match smoker is smoking it :<");
    }

    public void smokerWithPaper() throws InterruptedException {
        paperSmokerCanStart.acquire();
        try {
            makeCigarettes();
        } finally {
            agentSem.release();
        }
        System.out.println("Paper smoker is smoking it :<");
    }

    public void makeCigarettes() throws InterruptedException {
        Thread.sleep(2000);
        System.out.println("Cigarettes created :<");
    }
}


public class CigaretteSmokers {

    public static void main(String[] args) throws InterruptedException {
        Agent agent = new Agent();
        Pushers pushers = new Pushers(agent);
        Smokers smokers = new Smokers(pushers, agent);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        agent.agentA();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        agent.agentB();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        agent.agentC();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });


            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        pushers.pusherA();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        pushers.pusherB();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        pushers.pusherC();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        smokers.smokerWithTobacco();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        smokers.smokerWithMatch();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            executor.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        smokers.smokerWithPaper();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            Thread.sleep(10000);
            executor.shutdownNow();
        }
    }

}
