/*  non-preemptive sjf -> 다연  */
/*  sjf : burst time이 제일 짧은것부터 실행   */
package sjf;

import java.util.*;
import process.*;
import java.lang.Object;

public class sjf {
    public static void sjf(ArrayList<process> PCB_list){
        System.out.println("Hello, I'm sjf");

        //PCB_list를 burstTime 기준으로 오름차순 정렬하기 (실행시간 짧은 것 부터 실행)
        ArrayList<process> sorted_PCB = new ArrayList<process>(); //원본 복사 (깊은 복사를 해야 원본 변경이 안됨)
        for(process P : PCB_list) {
            sorted_PCB.add((process)P.clone());
        }
        Collections.sort(sorted_PCB, new Comparator<process>(){ //정렬
            @Override
            public int compare(process p1, process p2){
                return p1.getBurstTime()-p2.getBurstTime();
            }
        });

        //ready queue에 프로세스 넣기
        Queue<process> readyQ = new LinkedList<process>();
        for(process P : sorted_PCB) {
            readyQ.offer(P);
        }

        //오름차순 정렬됐는지 출력 -> 완료!
        System.out.println("burst time으로 정렬된 큐: ");
        System.out.println(readyQ);

        //readyQ에서 꺼내와서 순서대로 작업하기
        int cpuTime = 0;    //cpu 실행시간 (진행시간)
        process runningProcess = null;  //현재 실행중인 프로세스

        while(!readyQ.isEmpty() || runningProcess!=null) {  //Q를 다 비울때까지 (=모든 프로세스가 실행을 완료)
            System.out.print("CPU 실행 시간: "+cpuTime+" | ");
            if(runningProcess != null) {
                System.out.println("현재 실행중 프로세스:"+runningProcess.getPid()+" | 남은 burstTime: "+runningProcess.getBurstTime());
                runningProcess.setBurstTime(runningProcess.getBurstTime()-1);   //실행중인 프로세스 burstTime 감소
                if(runningProcess.getBurstTime()==0) {
                    System.out.println("프로세스 "+runningProcess.getPid()+"번이 완료됨\n");
                    runningProcess = null;
                }
            }

            if(!readyQ.isEmpty() && runningProcess == null) {   //앞서 실행하던 프로세스가 끝났고, 아직 readyQ에 프로세스가 남아있으면 다음 실행할 것 올림
                runningProcess = readyQ.poll();
                System.out.println("프로세스 "+runningProcess.getPid()+"번이 CPU에 올라감");
            }

            cpuTime++;
        }

        System.out.println("----------------------------");
    }
}
