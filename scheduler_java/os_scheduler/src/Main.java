/*  프로그램을 실행할 메인 클래스    */
import process.*;
import fcfs.*;
import sjf.*;
import srtf.*;
import roundrobin.*;
import newScheduler.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        //프로세스들을 담아놓을 리스트 - 각 프로세스의 정보는 p.get~~()으로 접근 가능
        ArrayList<process> PCB_list = new ArrayList<process>();

        //프로세스 생성하기 (예시) -> 나중엔 이거 텍스트파일 읽어서 해야됨
        process p1 = process.createProcess(1,1,10,1);
        PCB_list.add(p1);
        process p2 = process.createProcess(2,2,5,2);
        PCB_list.add(p2);
        process p3 = process.createProcess(3,3,2,3);
        PCB_list.add(p3);

        //생성된 프로세스 정보 출력
        System.out.println("p1의 정보\n"+p1);
        System.out.println("p2의 정보\n"+p2);
        System.out.println("p3의 정보\n"+p3);
        System.out.println("----------------------------");

        //fcfs 함수 테스트
        fcfs.fcfs(PCB_list);

        //sjf 함수 테스트
        sjf.sjf(PCB_list);

        //srtf 함수 테스트
        srtf.srtf();

        //round robin 함수 테스트
        roundrobin.roundrobin();

        //신규정책 함수 테스트
        newScheduler.newScheduler();

    }
}
