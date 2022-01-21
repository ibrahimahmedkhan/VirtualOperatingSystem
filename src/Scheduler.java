import java.util.Comparator;
import java.util.PriorityQueue;

public class Scheduler {
	
	class RunningQueue {
		PCB runningProcess;
		PCB getRunningProcess() {
			return runningProcess;
		}
		
		void setRunningProcess(PCB process) {
			runningProcess = process;
		}
	}
	
	RunningQueue runningQueue = new RunningQueue();
	byte[] memory;
	PriorityQueue<PCB> priorityQueueL1 = new PriorityQueue<PCB>(new Comparator<PCB>() {
        public int compare(PCB o1, PCB o2) {
            return o1.priority - o2.priority;
        }
    });
	PriorityQueue<PCB> roundRobinQueueL2 = new PriorityQueue<PCB>(new Comparator<PCB>() {
        public int compare(PCB o1, PCB o2) {
            return o1.priority - o2.priority;
        }
    });
	int timeSlice = 2;
	
	public Scheduler(PCB[] pcbs, byte[] memory) {
		this.memory = memory;
		for (PCB pcb : pcbs) {
			if (pcb.priority >= 0 && pcb.priority <= 15) {
				priorityQueueL1.add(pcb);
			} else if (pcb.priority >= 16 && pcb.priority <= 31) {
				roundRobinQueueL2.add(pcb);
			} else {
				System.out.println("Invalid Priority");
			}
		}
	}
	
	public void runProcesses() {
		if (timeSlice % 2 != 0) {
			System.out.println("[ERROR]: Time Slice must be a multiple of 2");
			System.exit(1);
		}
		
		//We run the processes present in the level 1 queue first.
//		while (priorityQueueL1.peek() != null) { // if the level 1 queue is not empty then
//			// run all the processes in it by their priority.
//			runningQueue.runningProcess = priorityQueueL1.poll();
//			System.out.println("[RUNNING]: "+runningQueue.runningProcess.fileName);
//			Execute.execIns(memory, runningQueue.runningProcess.gpr, runningQueue.runningProcess.spr, 0, runningQueue.runningProcess, runningQueue.runningProcess.code.length);
//		}
		
		if (priorityQueueL1.peek() == null) System.out.println("[ALERT]: LEVEL 1 QUEUE IS EMPTY.");
		
		//Now that the level 1 queue is empty, we run the processes in level 2 queue.
		while(roundRobinQueueL2.peek() != null) {
			for (PCB process : roundRobinQueueL2) {
				runningQueue.runningProcess = process; //one by one we give each process
				//a time slice to run.
				System.out.println("[RUNNING]: "+runningQueue.runningProcess.fileName);
				
				//We have provided timeSlice/2 as the last parameter 'iterations', this makes
				//sure that only number of instructions according to time slice are run.
				// if time slice is 2 then 1 instruction is run.
				Execute.execIns(memory, runningQueue.runningProcess.gpr, runningQueue.runningProcess.spr, 0, runningQueue.runningProcess, timeSlice/2);
				System.out.println("[SWITCHING] "+runningQueue.runningProcess.fileName+" SWITCHED");
				runningQueue.runningProcess = null;
			}
		}
	}
}
