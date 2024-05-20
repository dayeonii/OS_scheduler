package newScheduler;

import process.process;

import java.util.*;

public class newScheduler {
    public static void newScheduler(ArrayList<process> PCB_list){
        System.out.println("Hello, I'm new Scheduler!");

        int timeSlice;
        int cpuTime = 0;
        HashMap<Integer, Integer> flag = new HashMap<>();   //손 들어! 손 내려!
        Queue<process> readyQ = new LinkedList<process>();

        //PCB_list 원본 변경 방지를 위해 복사본 생성
        ArrayList<process> copy_PCB = new ArrayList<process>();
        for(process P : PCB_list) {
            copy_PCB.add((process)P.clone());
        }

        //랜덤으로 우선순위를 정해준다 -> 테스트 완료
        randPriority(copy_PCB);

        //우선순위대로 readyQ에 올린다

        //각 프로세스들을 time slice 만큼 실행한다 -> 실행된 프로세스는 flag를 1으로 바꾼다.

        //모든 프로세스의 flag가 1이 되면 새로운 턴을 시작한다 (다시 랜덤으로 우선순위 정해서 위의 과정을 반복)

        System.out.println("----------------------------");
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