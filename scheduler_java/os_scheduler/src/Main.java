/*  프로그램을 실행할 메인 클래스    */
import process.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("process 생성");

        process p1 = process.createProcess(1,1,10,4);
        System.out.println("created");
        //생성된 프로세스 정보 출력
        System.out.println(p1);
        System.out.println("p1의 pid: "+p1.getPid());

    }
}
