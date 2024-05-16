/*  non-preemptive sjf -> 다연  */
/*  sjf : burst time이 제일 짧은것부터 실행   */
package sjf;

import java.util.*;

import process.*;

public class sjf {
    public static void sjf(ArrayList<process> PCB_list){
        System.out.println("Hello, I'm sjf");

        int cpuTime = 0;    //cpu 실행시간 (진행시간)

        //PCB_list를 burstTime 기준으로 오름차순 정렬하기 (실행시간 짧은 것 부터 실행)
        ArrayList<process> sorted_PCB = new ArrayList<process>(PCB_list); //원본 복사
        Collections.sort(sorted_PCB, new Comparator<process>(){
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

        System.out.println("----------------------------");
    }
}
