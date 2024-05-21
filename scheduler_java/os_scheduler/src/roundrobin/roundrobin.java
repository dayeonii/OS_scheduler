/*  round-robin     */
package roundrobin;

import process.*;
import result.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class roundrobin {
    public static List<SchedulingResult> roundrobin(ArrayList<process> PCB_list, int timeSlice) {
        System.out.println("Hello, I'm Round Robin!");

        // 프로세스 리스트를 복사, 깊은 복사
        ArrayList<process> processes = new ArrayList<>();
        for (process P : PCB_list) {
            processes.add((process) P.clone());
        }

        // 도착 시간에 따라 프로세스 정렬
        processes.sort((p1, p2) -> p1.getArrivalTime() - p2.getArrivalTime());

        // Ready Queue 생성
        Queue<process> readyQueue = new LinkedList<>();
        for (process P : processes) {
            readyQueue.offer(P);
        }

        // 결과를 저장할 리스트
        List<SchedulingResult> results = new ArrayList<>();

        int currentTime = 0; // 현재까지 실행된 시간
        boolean[] firstExecuted = new boolean[processes.size()]; // 각 프로세스의 첫 실행 여부를 저장하는 배열
        int[] lastExecutionTime = new int[processes.size()]; // 각 프로세스의 이전 실행이 끝난 시간을 저장하는 배열
        int[] responseTimes = new int[processes.size()]; // 각 프로세스의 응답 시간을 저장하는 배열

        while (!readyQueue.isEmpty()) {
            process currentProcess = readyQueue.poll();
            int pid = currentProcess.getPid();
            int burstTime = currentProcess.getBurstTime();
            int executeTime = Math.min(timeSlice, burstTime);

            // 대기 시간 계산
            int waitingTime;
            if (!firstExecuted[pid - 1]) {
                waitingTime = Math.max(0, currentTime - currentProcess.getArrivalTime()); // 처음 실행될 때의 대기 시간
            } else {
                waitingTime = Math.max(0, currentTime - lastExecutionTime[pid - 1]); // 두 번째 실행부터는 이전 실행이 끝난 후의 대기 시간
            }

            // 응답 시간 계산
            if (!firstExecuted[pid - 1]) {
                responseTimes[pid - 1] = currentTime - currentProcess.getArrivalTime(); // 처음 실행될 때 응답 시간 계산
                firstExecuted[pid - 1] = true; // 해당 프로세스가 처음 실행됨을 표시
            }

            // 프로세스 실행
            System.out.println("현재 실행시간 " + currentTime + ": Process " + pid + "은 " + executeTime + " 동안 실행됨");
            currentTime += executeTime;
            burstTime -= executeTime;

            // 결과 저장
            results.add(new SchedulingResult(pid, currentTime - executeTime, executeTime, waitingTime, responseTimes[pid - 1]));

            // 프로세스 상태 출력
            System.out.println("Process " + pid + " - 남은 burst time: " + burstTime + ", Time slice: " + (timeSlice - executeTime) + "\n");

            // 프로세스가 완료되었는지 확인
            if (burstTime > 0) {
                currentProcess.setBurstTime(burstTime);
                readyQueue.offer(currentProcess);
            } else {
                System.out.println("\nProcess " + pid + " 번이 " + currentTime + " 에 완료됨\n");
            }

            // 이전 실행이 끝난 시간 업데이트
            lastExecutionTime[pid - 1] = currentTime;
        }

        System.out.println("----------------------------");

        return results;
    }
}