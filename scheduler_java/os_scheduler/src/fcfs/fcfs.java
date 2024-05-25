package fcfs;
import process.*;
import result.SchedulingResult;

import java.util.*;

public class fcfs {
    public static ArrayList<SchedulingResult> fcfs(ArrayList<process> PCB_list) {
        System.out.println("Hello I'm fcfs!");

        ArrayList<SchedulingResult> fcfs_result = new ArrayList<>();

        //PCB_list를 도착시간 순서대로 정렬
        ArrayList<process> sorted_PCB = new ArrayList<process>(); //원본 복사 (깊은 복사를 해야 원본 변경이 안됨)
        for(process P : PCB_list) {
            sorted_PCB.add((process)P.clone());
        }
        Collections.sort(sorted_PCB, new Comparator<process>(){ //정렬 (도착시간순으로 먼저)
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

        //ready queue에 프로세스 넣기
        Queue<process> readyQ = new LinkedList<process>();
//        for(process P : sorted_PCB) {
//            originBurstTime.put(P.getPid(), P.getBurstTime());
//        }

        //readyQ에서 꺼내와서 순서대로 작업하기
        int cpuTime = 0;    //cpu 실행시간 (진행시간)
        int totalWaitingTime = 0;   //총 대기시간 (프로세스 개수로 나누면 avg)
        int startTime = 0;  //해당 프로세스가 시작된 시간
        int responseTime = 0;   //응답시간
        HashMap<Integer, Integer> waitingTimes = new HashMap<>();   //프로세스별로 대기시간 저장
        process runningProcess = null;  //현재 실행중인 프로세스

        while(!sorted_PCB.isEmpty() || !readyQ.isEmpty() || runningProcess!=null) {  //Q를 다 비울때까지 (=모든 프로세스가 실행을 완료)
            System.out.print("CPU 실행 시간: "+cpuTime+" | ");

            //도착시간이 된 프로세스를 readyQ에 넣기
            while(!sorted_PCB.isEmpty() && sorted_PCB.get(0).getArrivalTime() == cpuTime) {
                readyQ.offer(sorted_PCB.remove(0));
            }

            //앞서 실행하던 프로세스가 끝났고, 아직 readyQ에 프로세스가 남아있으면 다음 실행할 것 올림
            if(!readyQ.isEmpty() && runningProcess == null) {
                //올리기 전에 현재 readyQ에 있는거 도착시간으로 정렬
                ArrayList<process> temp = new ArrayList<process>(readyQ);
                Collections.sort(temp, new Comparator<process>() {
                    @Override
                    public int compare(process p1, process p2) {
                        return p1.getArrivalTime()-p2.getArrivalTime();
                    }
                });
                readyQ.clear(); //재정렬해서 싹 넣기
                readyQ.addAll(temp);

                runningProcess = readyQ.poll(); //cpu 올리기
                startTime = cpuTime;
                responseTime = startTime - runningProcess.getArrivalTime();
                System.out.println("프로세스 "+runningProcess.getPid()+"번이 CPU에 올라감 | "+"시작시간: "+startTime);
                System.out.println("응답시간: "+responseTime);

            }


            if(runningProcess != null) {
                System.out.println("현재 실행중 프로세스:"+runningProcess.getPid()+" | 남은 burstTime: "+runningProcess.getBurstTime());
                runningProcess.setBurstTime(runningProcess.getBurstTime()-1);   //실행중인 프로세스 burstTime 감소
                if(runningProcess.getBurstTime()==0) {
                    int waitTime = cpuTime-runningProcess.getArrivalTime()-originBurstTime.get(runningProcess.getPid())+1;
                    totalWaitingTime += waitTime;
                    waitingTimes.put(runningProcess.getPid(), waitTime);
                    System.out.println("프로세스 "+runningProcess.getPid()+"번이 완료됨");
                    //result (pid, 시작, 실행, 대기, 응답) 정보 추가
                    fcfs_result.add(new SchedulingResult( runningProcess.getPid(), startTime, originBurstTime.get(runningProcess.getPid()), waitTime, responseTime));
                    runningProcess = null;  //완료된 프로세스
                }
            }

            cpuTime++;
        }

        System.out.println("----------------------------");
        return fcfs_result;
    }

}
