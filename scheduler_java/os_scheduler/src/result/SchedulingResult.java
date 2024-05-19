package result;

public class SchedulingResult {
    private int pid;
    private int startTime;
    private int duration;
    private int waitingTime;

    public SchedulingResult(int pid, int startTime, int duration, int waitingTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.duration = duration;
        this.waitingTime = waitingTime;
    }

    public int getPid() {
        return pid;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
}
