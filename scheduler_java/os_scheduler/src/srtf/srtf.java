package srtf;

import process.process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class srtf {
    // SRTF 스케줄링 시뮬레이션 메서드
    public static void srtf(ArrayList<process> processes) {
        // 도착 시간에 따라 프로세스 정렬
        processes.sort(Comparator.comparingInt(process::getArrivalTime));

        // 실행 대기열을 관리할 우선순위 큐 (남은 실행 시간이 짧은 순으로 정렬)
        PriorityQueue<ProcessState> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));

        int currentTime = 0; // 시뮬레이션 시간
        int completedProcesses = 0; // 완료된 프로세스 수
        int index = 0; // 현재 프로세스 리스트의 인덱스

        // 각 프로세스의 남은 실행 시간을 관리할 리스트
        ArrayList<ProcessState> processStates = new ArrayList<>();
        for (process p : processes) {
            processStates.add(new ProcessState(p, p.getBurstTime()));
        }

        while (completedProcesses < processes.size()) {
            // 현재 시간에 도착하는 프로세스를 실행 대기열에 추가
            while (index < processes.size() && processes.get(index).getArrivalTime() <= currentTime) {
                queue.add(processStates.get(index));
                index++;
            }

            if (!queue.isEmpty()) {
                ProcessState currentProcessState = queue.poll(); // 실행할 프로세스 선택
                // 현재 프로세스 실행 (1단위 시간만큼 실행)
                currentProcessState.remainingTime--;
                System.out.println("시간 " + currentTime + ": 프로세스 " + currentProcessState.process.getPid() + " 실행");

                // 프로세스가 완료되었는지 확인
                if (currentProcessState.remainingTime == 0) {
                    completedProcesses++; // 완료된 프로세스 수 증가
                    System.out.println("시간 " + (currentTime + 1) + ": 프로세스 " + currentProcessState.process.getPid() + " 완료");
                } else {
                    // 프로세스가 완료되지 않았다면 다시 큐에 추가
                    queue.add(currentProcessState);
                }
            }

            // 시간 1단위 증가
            currentTime++;
        }
        System.out.println("----------------------------");
    }

    // 프로세스 상태를 관리하기 위한 내부 클래스
    private static class ProcessState {
        process process;
        int remainingTime;

        ProcessState(process process, int remainingTime) {
            this.process = process;
            this.remainingTime = remainingTime;
        }
    }
}
