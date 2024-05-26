package newScheduler;

import process.process;
import result.SchedulingResult;

import java.util.*;

import static java.util.Collections.min;

public class newScheduler {
    public static ArrayList<SchedulingResult> newScheduler(ArrayList<process> PCB_list, int timeSlice) {
        System.out.println("Hello, I'm new Scheduler!");

        // PCB_list 복사본 생성 - 원본 변경 방지를 위함
        ArrayList<process> copy_PCB = new ArrayList<>();
        for (process P : PCB_list) {
            copy_PCB.add((process) P.clone());
        }

        // 도착시간 순서대로 copy_PCB를 정렬
        Collections.sort(copy_PCB, Comparator.comparingInt(process::getArrivalTime));

        // 1번째 턴 하기 전에 랜덤 우선순위 할당
        randPriority(copy_PCB);
        System.out.println("첫번째 턴: " + copy_PCB);

        // 대기시간을 구하는데 사용될 원본 실행시간 저장
        HashMap<Integer, Integer> originBurstTime = new HashMap<>();
        for (process P : PCB_list) {
            originBurstTime.put(P.getPid(), P.getBurstTime());
        }

        // 반환할 new_result 생성
        ArrayList<SchedulingResult> new_result = new ArrayList<>();

        // readyQueue 생성
        Queue<process> readyQ = new LinkedList<>();

        // flag 맵 생성
        HashMap<Integer, Integer> flag = new HashMap<>();
        for (process P : copy_PCB) {
            flag.put(P.getPid(), 0);   // 0으로 초기화
        }

        // 필요한 객체들 선언 (cpuTime, totalWaitingTime, responseTime, startTime, runningProcess, waitingTimeList)
        int cpuTime = 0;
        int totalWaitingTime = 0;   // 총 대기시간 (프로세스 개수로 나누면 avg)
        int totalExecutedTime = 0;  // 총 실행시간
        int startTime = 0;  // 해당 프로세스가 시작된 시간
        int responseTime = 0;   // 응답시간 (첫 시작된)
        int waitTime = 0; //대기시간
        process runningProcess = null;  // 현재 실행중인 프로세스

        boolean[] firstExecuted = new boolean[copy_PCB.size()]; // 각 프로세스의 첫 실행 여부를 저장하는 배열
        int[] responseTimes = new int[copy_PCB.size()]; // 각 프로세스의 첫 실행시간을 저장 - 간트차트 표기를 위함

        // 지금부터 모든 프로세스가 완료될 때 까지 반복
        while (!isAllCompleted(copy_PCB) || !readyQ.isEmpty() || runningProcess != null) {
            // 도착시간에 따라서 readyQ에 프로세스 넣기
            for (process P : copy_PCB) {
                if (P.getArrivalTime() == cpuTime && P.getBurstTime() > 0 && !readyQ.contains(P)) {
                    readyQ.add(P);
                }
            }

            // runningProcess가 null이면 readyQ에서 cpu에 올릴 프로세스 poll해서 cpu 할당
            if (runningProcess == null && !readyQ.isEmpty()) {
                // 올리기 전에 현재 readyQ에 있는거 priority로 정렬
                ArrayList<process> temp = new ArrayList<>(readyQ);
                temp.sort(Comparator.comparingInt(process::getPriority));
                readyQ.clear(); // 재정렬해서 싹 넣기
                readyQ.addAll(temp);

                runningProcess = readyQ.poll(); // cpu올리기
                startTime = cpuTime;
                //responseTime = startTime - runningProcess.getArrivalTime();
                System.out.println("프로세스 " + runningProcess.getPid() + "번이 CPU에 올라감 | " + "시작시간: " + startTime + " 응답시간: " + responseTime);

                // 응답 시간 계산 첫 응답시간
                if (!firstExecuted[runningProcess.getPid() - 1]) {
                    responseTime = startTime - runningProcess.getArrivalTime();
                    responseTimes[runningProcess.getPid()-1] = responseTime;
                    firstExecuted[runningProcess.getPid() - 1] = true; // 해당 프로세스가 처음 실행됨을 표시
                }
            }

            // 현재 실행중인 프로세스 나타내기 - 실행과정 구현 (timeslice만큼 실행 후 내리기)
            if (runningProcess != null) {
                System.out.println("현재 실행중 프로세스: " + runningProcess.getPid() + " | 남은 burstTime: " + runningProcess.getBurstTime());
                runningProcess.setBurstTime(runningProcess.getBurstTime() - 1);
                cpuTime++;
                
                // 실행하다가 해당 프로세스가 완료되면 (burstTime==0) - 완료문구 출력후 cpu에서 내리고, result에 결과 추가
                if (runningProcess.getBurstTime() == 0) {
                	//실행 프로세스 기록
                    waitTime = cpuTime - startTime - 1;
                    totalWaitingTime += waitTime;
                    System.out.println("프로세스 " + runningProcess.getPid() + "번이 완료됨");

                    // 여기에 result 추가
                    new_result.add(new SchedulingResult(runningProcess.getPid(), startTime, cpuTime-startTime, totalWaitingTime, responseTimes[runningProcess.getPid()-1]));

                    flag.put(runningProcess.getPid(), 1);   // 실행한건 flag 1 바꿔주기
                    runningProcess = null;  // cpu에서 내리기
                } else if (cpuTime - startTime == timeSlice) {    // timeSlice만큼 실행했으면 cpu에서 내리고 다음 프로세스 실행
                	waitTime = cpuTime - startTime - 1;
                    totalWaitingTime += waitTime;
                    new_result.add(new SchedulingResult(runningProcess.getPid(), startTime, cpuTime-startTime, waitTime, responseTimes[runningProcess.getPid()-1]));
                    System.out.println("timeslice만큼 실행된 " + runningProcess.getPid() + "번 | 남은 burst: " + runningProcess.getBurstTime());
                    flag.put(runningProcess.getPid(), 1);   // 실행한건 flag 1 바꿔주기 
                  
                    runningProcess = null;
                }
            } else {
                cpuTime++;
            }

            // 모든 프로세스의 flag가 1이면, 다시 0으로 초기화 (다음 턴), 우선순위 랜덤재할당, readyQ에 다시 넣기
            boolean allFlagsSet = true;
            for (process P : copy_PCB) {
                if (P.getBurstTime() > 0 && flag.get(P.getPid()) == 0) {
                    allFlagsSet = false;
                    break;
                }
            }

            if (allFlagsSet) {
                for (process P : copy_PCB) {
                    if (P.getBurstTime() > 0) {
                        flag.put(P.getPid(), 0); // 남아있는 프로세스의 flag만 0으로 초기화
                    }
                }
                randPriority(copy_PCB); // 우선순위 재할당
                System.out.println("모든 flag가 1이 됨 - 다음 턴: " + copy_PCB);
                for (process P : copy_PCB) {
                    if (P.getBurstTime() > 0 && !readyQ.contains(P)) {
                        readyQ.add(P);
                    }
                }
            }
        }

        System.out.println("-----------------------------");
        return new_result;
    }

    // 모든 프로세스가 완료되었는지 burstTime을 통해 체크하는 함수
    private static boolean isAllCompleted(ArrayList<process> P_list) {
        for (process P : P_list) {
            if (P.getBurstTime() > 0)
                return false;   // 아직 모든 프로세스가 완료되지 않음
        }
        return true;    // 모든 프로세스가 완료됨 (모든 프로세스의 burstTime이 0이 됨)
    }

    // 랜덤 순위를 우선 부여하는 함수 randPriority
    // 입력받은 PCB 리스트를 접근하여, 각 프로세스의 priority를 변경해주는 개념
    private static void randPriority(ArrayList<process> P) {
        int len = P.size(); // 프로세스 개수
        Random rd = new Random();
        ArrayList<Integer> randomList = new ArrayList<>(); // 랜덤한 수를 나열한 리스트를 이용
        for (int i = 1; i <= len; i++) {
            randomList.add(i);
        }
        Collections.shuffle(randomList, rd);

        for (int i = 0; i < len; i++) { // 랜덤 우선순위 부여
            P.get(i).setPriority(randomList.get(i));
        }
    }
}
