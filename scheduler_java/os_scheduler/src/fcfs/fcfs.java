package fcfs;
import process.*;
import java.util.ArrayList;

/*  fcfs 스케줄러를 구현할 클래스 */
//여기서 변경해도 반영되나요? - 다연
//hihi

public class fcfs {
    public static void fcfs(ArrayList<process> PCB_list) {
        System.out.println("Hello I'm fcfs!");
        System.out.println("리스트에서 프로세스 정보 뽑아오기");
        int p1_pid = PCB_list.get(0).getPid();
        System.out.println("p1의 pid: "+p1_pid);
        System.out.println("----------------------------");
    }

}
