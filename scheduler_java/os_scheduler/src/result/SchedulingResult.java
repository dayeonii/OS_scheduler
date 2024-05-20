package result;

public class SchedulingResult {
    private int pid;
    private int startTime;
    private int duration;
    private int waitingTime;
    private int responseTime;

    public SchedulingResult(int pid, int startTime, int duration, int waitingTime, int responseTime) {
        this.pid = pid;
        this.startTime = startTime;
        this.duration = duration;
        this.waitingTime = waitingTime;
        this.responseTime = responseTime;
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

    public int getResponseTime() {
        return responseTime;
    }
}
