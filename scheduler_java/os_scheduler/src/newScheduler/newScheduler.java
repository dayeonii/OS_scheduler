package newScheduler;

import process.process;
import result.SchedulingResult;

import java.util.*;

public class newScheduler {
    public static ArrayList<SchedulingResult> newScheduler(ArrayList<process> PCB_list){
        System.out.println("Hello, I'm new Scheduler!");

        //PCB_list 원본 변경 방지를 위해 복사본 생성
        ArrayList<process> copy_PCB = new ArrayList<process>();
        for(process P : PCB_list) {
            copy_PCB.add((process)P.clone());
        }

        Queue<process> readyQ = new LinkedList<process>();
        ArrayList<SchedulingResult> new_result = new ArrayList<>(); //결과
        HashMap<Integer, Integer> flag = new HashMap<>();   //손 들어! 손 내려!
        for(process P : copy_PCB) { //flag 초기화
            flag.put(P.getPid(), 0);
        }

        //일단 도착 순서대로 정렬
        for(process P : PCB_list) {
            copy_PCB.add((process)P.clone());
        }
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

        //랜덤으로 우선순위를 정해준다 -> 테스트 완료
        randPriority(copy_PCB);

        //각 프로세스들을 time slice 만큼 실행한다 -> 실행된 프로세스는 flag를 1으로 바꾼다.

        //모든 프로세스의 flag가 1이 되면 새로운 턴을 시작한다 (다시 랜덤으로 우선순위 정해서 위의 과정을 반복)

        //readyQ에서 꺼내와서 순서대로 작업하기
        int timeSlice = 4;
        int cpuTime = 0;
        int totalWaitingTime = 0;   //총 대기시간 (프로세스 개수로 나누면 avg)
        int startTime = 0;  //해당 프로세스가 시작된 시간
        HashMap<Integer, Integer> waitingTimes = new HashMap<>();   //프로세스별로 대기시간 저장
        process runningProcess = null;  //현재 실행중인 프로세스

        while(!copy_PCB.isEmpty() || !readyQ.isEmpty() || runningProcess!=null) {  //Q를 다 비울때까지 (=모든 프로세스가 실행을 완료)
            System.out.print("CPU 실행 시간: "+cpuTime+" | ");

            //도착시간이 된 프로세스를 readyQ에 넣기
            while(!copy_PCB.isEmpty() && copy_PCB.get(0).getArrivalTime() <= cpuTime) {
                readyQ.offer(copy_PCB.remove(0));
            }

            //앞서 실행하던 프로세스가 끝났고, 아직 readyQ에 프로세스가 남아있으면 다음 실행할 것 올림
            if(!readyQ.isEmpty() && runningProcess == null) {
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

                runningProcess = readyQ.poll(); //cpu 올리기
                startTime = cpuTime;
                System.out.println("프로세스 "+runningProcess.getPid()+"번이 CPU에 올라감 | "+"시작시간: "+startTime);
            }


            for(int i=0; i<timeSlice; i++) {
                System.out.println("현재 실행중 프로세스:"+runningProcess.getPid()+" | 남은 burstTime: "+runningProcess.getBurstTime());
                runningProcess.setBurstTime(runningProcess.getBurstTime()-1);   //실행중인 프로세스 burstTime 감소
                if(runningProcess.getBurstTime()==0) {
                    int waitTime = cpuTime-runningProcess.getArrivalTime()-originBurstTime.get(runningProcess.getPid());
                    totalWaitingTime += waitTime;
                    waitingTimes.put(runningProcess.getPid(), waitTime);
                    System.out.println("프로세스 "+runningProcess.getPid()+"번이 완료됨");
                    //new_result에 (pid, 실행, 시작, 대기) 정보 추가
                    new_result.add(new SchedulingResult( runningProcess.getPid(), startTime, originBurstTime.get(runningProcess.getPid()), waitTime, 0));
                    runningProcess = null;  //완료된 프로세스
                }
            }

            cpuTime++;
        }

        System.out.println("----------------------------");
        return new_result;
    }

    //랜덤 순위를 우선 부여하는 함수 randPriority
    //입력받은 PCB 리스트를 접근하여, 각 프로세스의 priority를 변경해주는 개념
    public static void randPriority(ArrayList<process> P) {
        int len = P.size();  //프로세스 개수
        Random rd = new Random();
        ArrayList<Integer> randomList = new ArrayList<>();  //랜덤한 수를 나열한 리스트를 이용
        for(int i=1; i<=len;i++) {
            randomList.add(i);
        }
        Collections.shuffle(randomList,rd);

        for(int i=0; i<len; i++) {  //랜덤 우선순위 부여
            P.get(i).setPriority(randomList.get(i));
        }
    }
}