package ex2;

public class ThreadpoolDTO {
    private final int waitingSimulations;
    private final int runningSimulations;
    private final long endedSimulations;

    public ThreadpoolDTO(int waitingSimulations, int runningSimulations, long endedSimulations) {
        this.waitingSimulations = waitingSimulations;
        this.runningSimulations = runningSimulations;
        this.endedSimulations = endedSimulations;
    }

    public int getWaitingSimulations() {
        return waitingSimulations;
    }

    public int getRunningSimulations() {
        return runningSimulations;
    }

    public long getEndedSimulations() {
        return endedSimulations;
    }
}
