package process;

/* 프로세스의 정보를 담을 클래스 (PCB)와 같은 역할
        PID, arrival time, burst time, priority
        time slice는 어디에 저장하지? -> 각 선점형 스케줄러의 클래스에..?
 */

public class process {
    private int pid;
    private int arrivalTime;
    private int burstTime;
    private int priority;

    //각 값을 접근하기 위한 get 함수
    public int getPid() {
        return pid;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public  int getBurstTime() {
        return burstTime;
    }
    public int getPriority() {
        return priority;
    }

    //생성자
    public process(int pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    // Process 정보 출력
    @Override
    public String toString() {
        return "Process {" +
                "pid=" + pid +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                ", priority=" + priority +
                '}';
    }

    // 사용자 입력으로 Process 객체 생성하는 메서드
    public static process createProcess(int pid, int arrivalTime, int burstTime, int priority) {
        return new process(pid, arrivalTime, burstTime, priority);
    }
}
