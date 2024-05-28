package srtf;

import process.*;
import result.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class srtf {
    // SRTF 스케줄링 시뮬레이션 메서드
    public static ArrayList<SchedulingResult> srtf(ArrayList<process> processes) {
        ArrayList<SchedulingResult> results = new ArrayList<>();

        // 도착 시간에 따라 프로세스 정렬
        processes.sort(Comparator.comparingInt(process::getArrivalTime));

        // 실행 대기열을 관리할 우선순위 큐 (남은 실행 시간이 짧은 순으로 정렬)
        PriorityQueue<ProcessState> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));

        int cpuTime = 0; // 시뮬레이션 시간
        int completedProcesses = 0; // 완료된 프로세스 수
        int index = 0; // 현재 프로세스 리스트의 인덱스

        // 각 프로세스의 남은 실행 시간을 관리할 리스트
        ArrayList<ProcessState> processStates = new ArrayList<>();
        for (int i = 0; i < processes.size(); i++) {
            processStates.add(new ProcessState(processes.get(i), i, processes.get(i).getBurstTime()));
        }

        // 프로세스가 마지막으로 실행된 시간을 저장
        int[] lastExecutedTime = new int[processes.size()];

        // 이전에 실행한 프로세스 정보 저장
        ProcessState lastProcess = null;

        while (completedProcesses < processes.size()) {
            // 현재 시간에 도착하는 프로세스를 실행 대기열에 추가
            while (index < processes.size() && processes.get(index).getArrivalTime() <= cpuTime) {
                queue.add(processStates.get(index));
                index++;
            }

            if (!queue.isEmpty()) {
                ProcessState currentProcessState = queue.poll(); // 실행할 프로세스 선택

                // 대기 시간 갱신
                if (currentProcessState.remainingTime == currentProcessState.process.getBurstTime()) {
                    // 처음 시작할 때 대기 시간
                    currentProcessState.waitingTime = cpuTime - currentProcessState.process.getArrivalTime();
                    // 응답 시간 설정
                    currentProcessState.responseTime = cpuTime - currentProcessState.process.getArrivalTime();
                } else {
                    // 선점 이후 다시 시작할 때 대기 시간
                    currentProcessState.waitingTime += (cpuTime - lastExecutedTime[currentProcessState.index]);
                }

                // 현재 프로세스 실행 (1단위 시간만큼 실행)
                currentProcessState.remainingTime--;
                System.out.println("시간 " + cpuTime + ": 프로세스 " + currentProcessState.process.getPid() + " 실행");

                // 결과 리스트에 추가 (연속된 실행 시간 병합)
                if (lastProcess != null && lastProcess.process.getPid() == currentProcessState.process.getPid()) {
                    lastProcess.duration += 1;
                } else {
                    if (lastProcess != null) {
                        results.add(new SchedulingResult(lastProcess.process.getPid(), lastProcess.startTime, lastProcess.duration, lastProcess.waitingTime, lastProcess.responseTime));
                    }
                    lastProcess = new ProcessState(currentProcessState.process, currentProcessState.index, currentProcessState.remainingTime);
                    lastProcess.startTime = cpuTime;
                    lastProcess.duration = 1;
                    lastProcess.waitingTime = currentProcessState.waitingTime;
                    lastProcess.responseTime = currentProcessState.responseTime;
                } 

                // 프로세스가 완료되었는지 확인
                if (currentProcessState.remainingTime == 0) {
                    completedProcesses++; // 완료된 프로세스 수 증가
                    System.out.println("시간 " + (cpuTime + 1) + ": 프로세스 " + currentProcessState.process.getPid() + " 완료");
                    // 프로세스 완료시 결과 리스트에 추가
                    results.add(new SchedulingResult(currentProcessState.process.getPid(), lastProcess.startTime, lastProcess.duration, currentProcessState.waitingTime, currentProcessState.responseTime));
                    lastProcess = null;
                } else {
                    // 프로세스가 완료되지 않았다면 다시 큐에 추가
                    queue.add(currentProcessState);
                }

                // 마지막 실행 시간을 갱신
                lastExecutedTime[currentProcessState.index] = cpuTime+ 1;
            }

            // 시간 1단위 증가
            cpuTime++;
        }
        System.out.println("----------------------------");

        return results;
    }

    // 프로세스 상태를 관리하기 위한 내부 클래스
    private static class ProcessState {
        process process;
        int index; // 프로세스 리스트의 인덱스
        int waitingTime; // 대기 시간
        int startTime; // 시작 시간
        int duration; // 실행 시간
        int remainingTime; // 남은 실행 시간
        int responseTime; // 응답 시간

        ProcessState(process process, int index, int remainingTime) {
            this.process = process;
            this.index = index;
            this.waitingTime = 0;
            this.startTime = -1;
            this.duration = 0;
            this.remainingTime = remainingTime;
            this.responseTime = -1; // 초기 응답 시간을 -1로 설정
        }
    }
}
