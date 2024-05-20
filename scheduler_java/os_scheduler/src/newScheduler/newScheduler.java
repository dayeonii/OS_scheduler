package newScheduler;

import process.process;
import result.SchedulingResult;

import java.util.*;

public class newScheduler {
    public static ArrayList<SchedulingResult> newScheduler(ArrayList<process> PCB_list) {
        System.out.println("Hello, I'm new Scheduler!");

        // PCB_list 원본 변경 방지를 위해 복사본 생성
        ArrayList<process> copy_PCB = new ArrayList<>();
        for (process P : PCB_list) {
            copy_PCB.add((process) P.clone());
        }

        Queue<process> readyQ = new LinkedList<>();
        ArrayList<SchedulingResult> new_result = new ArrayList<>(); // 결과
        HashMap<Integer, Integer> flag = new HashMap<>(); // 손 들어! 손 내려!
        for (process P : copy_PCB) { // flag 초기화
            flag.put(P.getPid(), 0);
        }

        // 일단 도착 순서대로 정렬
        Collections.sort(copy_PCB, Comparator.comparingInt(process::getArrivalTime));

        // 대기시간을 구하는데 사용될 원본 실행시간 저장
        HashMap<Integer, Integer> originBurstTime = new HashMap<>();
        for (process P : PCB_list) {
            originBurstTime.put(P.getPid(), P.getBurstTime());
        }

        // 랜덤으로 우선순위를 정해준다
        randPriority(copy_PCB);

        int timeSlice = 4;
        int cpuTime = 0;
        process runningProcess = null; // 현재 실행중인 프로세스
        process lastProcess = null; // 마지막으로 실행된 프로세스

        while (!copy_PCB.isEmpty() || !readyQ.isEmpty() || runningProcess != null) { // 모든 프로세스가 완료될 때까지
            System.out.print("CPU 실행 시간: " + cpuTime + " | ");

            // 도착시간이 된 프로세스를 readyQ에 넣기
            while (!copy_PCB.isEmpty() && copy_PCB.get(0).getArrivalTime() <= cpuTime) {
                readyQ.offer(copy_PCB.remove(0));
            }

            // 프로세스를 타임슬라이스만큼 실행
            if (runningProcess != null) {
                for (int i = 0; i < timeSlice; i++) {
                    System.out.println("현재 실행중 프로세스: " + runningProcess.getPid() + " | 남은 burstTime: " + runningProcess.getBurstTime());
                    runningProcess.setBurstTime(runningProcess.getBurstTime() - 1); // 실행중인 프로세스 burstTime 감소
                    cpuTime++; // 시간 증가

                    if (runningProcess.getBurstTime() == 0) {
                        int waitTime = cpuTime - runningProcess.getArrivalTime() - originBurstTime.get(runningProcess.getPid());
                        flag.put(runningProcess.getPid(), 1); // 실행되면 flag를 1로 설정
                        System.out.println("프로세스 " + runningProcess.getPid() + "번이 완료됨");
                        // new_result에 (pid, 실행, 시작, 대기) 정보 추가
                        new_result.add(new SchedulingResult(runningProcess.getPid(), cpuTime - originBurstTime.get(runningProcess.getPid()), originBurstTime.get(runningProcess.getPid()), waitTime, 0));
                        runningProcess = null; // 완료된 프로세스
                        break; // 타임슬라이스 루프 종료
                    }
                }
            }

            // 프로세스를 타임슬라이스만큼 실행한 후 큐에 다시 추가
            if (runningProcess != null) {
                flag.put(runningProcess.getPid(), 1); // 실행된 프로세스 flag를 1로 설정
                readyQ.offer(runningProcess); // 큐에 다시 추가
                lastProcess = runningProcess; // 마지막으로 실행된 프로세스 갱신
                runningProcess = null; // 실행 프로세스 초기화
            }

            // 모든 프로세스의 flag가 1이 되면 한 턴 종료 -> 모두 0으로 바꿔주고 다시 우선순위 랜덤 지정
            if (flag.values().stream().allMatch(value -> value == 1)) {
                System.out.println("flag all 1");
                flag.replaceAll((pid, v) -> 0); // 플래그 초기화
                randPriority(new ArrayList<>(readyQ)); // 다시 랜덤 우선순위 지정
            }

            // 앞서 실행하던 프로세스가 끝났고, 아직 readyQ에 프로세스가 남아있으면 다음 실행할 것 올림
            if (runningProcess == null && !readyQ.isEmpty()) {
                // 올리기 전에 현재 readyQ에 있는거 priority로 정렬
                ArrayList<process> temp = new ArrayList<>(readyQ);
                temp.sort(Comparator.comparingInt(process::getPriority));
                readyQ.clear(); // 재정렬해서 싹 넣기
                readyQ.addAll(temp);

                // 이전에 실행된 프로세스와 다른 프로세스 선택
                do {
                    runningProcess = readyQ.poll();
                } while (runningProcess != null && lastProcess != null && runningProcess.getPid() == lastProcess.getPid() && !readyQ.isEmpty());

                if (runningProcess == null) {
                    runningProcess = lastProcess; // 같은 프로세스가 연속으로 실행되지 않도록
                }

                System.out.println("프로세스 " + runningProcess.getPid() + "번이 CPU에 올라감 | " + "시작시간: " + cpuTime);
            }
        }

        System.out.println("----------------------------");
        return new_result;
    }

    // 랜덤 순위를 우선 부여하는 함수 randPriority
    // 입력받은 PCB 리스트를 접근하여, 각 프로세스의 priority를 변경해주는 개념
    public static void randPriority(ArrayList<process> P) {
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
