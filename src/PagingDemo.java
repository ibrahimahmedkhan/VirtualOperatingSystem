import java.util.LinkedList;
import java.util.Queue;

public class PagingDemo {
    public static byte[] memory = new byte[8192];
	public static Queue<Integer> frameQueue = new LinkedList<>();
    public static void main(String[] args){
//      byte[] memory = new byte[8192];
        int frameSize = 4;
		for(int i = 0; i< memory.length / frameSize; i++){
			frameQueue.add(i);
		}
        SpecialRegisters spr = new SpecialRegisters();
        String processes = "./src/demoFiles/"; 

        PCB p5 = new PCB(processes+"p5");
        PCB flags = new PCB(processes+"flags");
        PCB large0 = new PCB(processes+"large0");
        PCB power = new PCB(processes+"power");
        PCB sfull = new PCB(processes+"sfull");
        PCB noop = new PCB(processes+"noop");
        String[] arr = {"p5", "flags", "large0", "power", "sfull", "noop"};
        PCB[] pcbs = {p5,flags,large0,power,sfull,noop};
        for (PCB pcb : pcbs) System.out.println("" + pcb);
        for (PCB pcb : pcbs) pcb.loadProcessIntoMem(memory, frameQueue);
        
        System.out.println(arrToString(memory));
        
        Scheduler processScheduler = new Scheduler(pcbs, memory);
        processScheduler.runProcesses();
    }
    
    static private String arrToString(byte[] arr) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if ((i % 128) == 0) s.append("\n\n");
            s.append(arr[i]).append(" ");
        }
        return s.toString();
    }
}

