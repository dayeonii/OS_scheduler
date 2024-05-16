/*  non-preemptive sjf -> 다연  */
package sjf;

import java.util.ArrayList;
import process.*;

public class sjf {
    public static void sjf(ArrayList<process> PCB_list){
        System.out.println("Hello, I'm sjf");

        int p1_pid = PCB_list.get(0).getPid();
        System.out.println(p1_pid);
        System.out.println("----------------------------");
    }
}
