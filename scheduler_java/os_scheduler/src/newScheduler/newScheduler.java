package newScheduler;

import process.process;
import result.SchedulingResult;

import java.lang.reflect.Array;
import java.util.*;

public class newScheduler {
    public static ArrayList<SchedulingResult> newScheduler(ArrayList<process> PCB_list) {
        System.out.println("Hello, I'm new Scheduler!");

        //PCB_list 복사본 생성 - 원본 변경 방지를 위함
        ArrayList<process> copy_PCB = new ArrayList<process>();
        for(process P : PCB_list) {
            copy_PCB.add((process) P.clone());
        }
        //도착시간 순서대로 copy_PCB를 정렬
        Collections.sort(copy_PCB, new Comparator<process>(){ //정렬 (도착시간순으로 먼저)
            @Override
            public int compare(process p1, process p2){
                return p1.getArrivalTime()-p2.getArrivalTime();
            }
        });

        //대기시간을 구하는데 사용될 원본 실행시간 저장
        HashMap<Integer, Integer> originBurstTime = new HashMap<>();
        for(process P : PCB_list) {
            originBurstTime.put(P.getPid(), P.getBurstTime());
        }

        //반환할 new_result 생성
        ArrayList<SchedulingResult> new_result = new ArrayList<>();

        //readyQueue 생성
        Queue<process> readyQ = new LinkedList<process>();

        //필요한 객체들 선언 (cpuTime, totalWaitingTime, responseTime, startTime, runningProcess, waitingTimeList)
        int cpuTime = 0;
        int timeSlice = 4;
        int waitTime = 0;
        int totalWaitingTime = 0;   //총 대기시간 (프로세스 개수로 나누면 avg)
        int startTime = 0;  //해당 프로세스가 시작된 시간
        int lastTime = 0;   //해당 프로세스가 마지막으로 실행하고 종료된 시간
        int responseTime = 0;   //응답시간
        HashMap<Integer, Integer> waitingTimes = new HashMap<>();   //프로세스별로 대기시간 저장
        process runningProcess = null;  //현재 실행중인 프로세스

        //지금부터 모든 프로세스가 완료될 때 까지 반복
        while(!isAllCompleted(copy_PCB)) {
            //도착시간에 따라서 readyQ에 프로세스 넣기
            for(process P : copy_PCB) {
                if(P.getArrivalTime()==cpuTime && P.getBurstTime()>0 && !readyQ.contains(P))
                    readyQ.add(P);
            }

            //runningProcess==null 이면 readyQ에서 cpu에 올릴 프로세스 poll해서 cpu 할당
            if(runningProcess==null && !readyQ.isEmpty()) {
                //올리기 전에 현재 readyQ에 있는거 priority로 정렬
                ArrayList<process> temp = new ArrayList<process>(readyQ);
                Collections.sort(temp, new Comparator<process>() {
                    @Override
                    public int compare(process p1, process p2) {
                        return p1.getPriority()-p2.getPriority();
                    }
                });
                readyQ.clear(); //재정렬해서 싹 넣기
                readyQ.addAll(temp);
                
                runningProcess = readyQ.poll(); //cpu올리기
                startTime = cpuTime;
                responseTime = startTime - runningProcess.getArrivalTime();
                System.out.println("프로세스 "+runningProcess.getPid()+"번이 CPU에 올라감 | "+"시작시간: "+startTime);
                System.out.println("응답시간: "+responseTime);
            }

            //현재 실행중인 프로세스 나타내기 - 실행과정 구현 (timeslice만큼 실행 후 내리기)
            if(runningProcess!=null) {
                System.out.println("현재 실행중 프로세스: "+runningProcess.getPid()+" | 남은 burstTime: "+runningProcess.getBurstTime());
                runningProcess.setBurstTime(runningProcess.getBurstTime()-1);

                //실행하다가 해당 프로세스가 완료되면 (burstTime==0) - 완료문구 출력후 cpu에서 내리고, result에 결과 추가
                if(runningProcess.getBurstTime()==0) {
                    waitTime = cpuTime - startTime;
                    lastTime = cpuTime;
                    totalWaitingTime += waitTime;
                    System.out.println("프로세스 "+runningProcess.getPid()+"번이 완료됨");
                    runningProcess = null;  //cpu에서 내리기
                }
            }
            cpuTime++;
        }

        System.out.println("-----------------------------");
        return new_result;
    }

    //모든 프로세스가 완료되었는지 burstTime을 통해 체크하는 함수
    private static boolean isAllCompleted(ArrayList<process> P_list) {
        for(process P : P_list) {
            if(P.getBurstTime() > 0)
                return false;   //아직 모든 프로세스가 완료되지 않음
        }
        return true;    //모든 프로세스가 완료됨 (모든 프로세스의 burstTime이 0이 됨)
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
