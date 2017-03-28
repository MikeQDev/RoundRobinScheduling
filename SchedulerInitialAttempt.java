import java.util.LinkedList;

public class SchedulerInitialAttempt {
	private final int contextSwitchTime = 3;
	private final int quantumSize = 5;

	private int clockTime;
	private ProcessTEW curProc;
	private ProcessTEW[] procs = { new ProcessTEW(1, 75, 0), new ProcessTEW(2, 40, 10), new ProcessTEW(3, 25, 15),
			new ProcessTEW(4, 20, 80), new ProcessTEW(5, 45, 90) };
	private LinkedList<ProcessTEW> readyQueue = new LinkedList<ProcessTEW>();

	public SchedulerInitialAttempt() {
		clockTime = 0;
		while (true) {
			if (clockTime >= 600)
				System.exit(0);
			checkNewProcs();

			if (clockTime % quantumSize == 0 || curProc == null)
				curProc = contextSwitch();
			else{
			// is this the process' first time executing?
			if (curProc.getSvcTimeElapsed() == 0)
				curProc.setStartTime(clockTime);

			if (curProc.work() == 1) { // done executing
				curProc.setEndTime(clockTime);
				System.out.println(clockTime + "\t" + curProc);
				contextSwitch();
			} else {
				System.out.println(clockTime + "\t" + curProc + "..." + readyQueue.size());
				// thisQuantum += 1;
			}
			}
			// readyQueue;
			for (ProcessTEW proc : readyQueue) {
				System.out.print(proc.getPid() + ",");
			}
			System.out.println();

			clockTime++;
		}
	}

	private ProcessTEW contextSwitch() {
		// System.out.println(clockTime+"\t----CS----");
		if (curProc != null) {
			// deallocate
			if (!curProc.isDone()) // if not done, then put at end of ready
									// queue
				readyQueue.addLast(curProc);
		}
		for (int i = 0; i < contextSwitchTime; i++) {
			System.out.println(clockTime + "\t----------CS----------");
			// checkNewProcs();
			clockTime++;
		}
		if (readyQueue.isEmpty()) {
			System.out.println("found empty readyqueue");
			System.exit(0);
		}
		
		// dispatch next
		return readyQueue.removeFirst();

	}

	private void checkNewProcs() {
		switch (clockTime) { // add to ready queue
		case 0:
			readyQueue.addLast(procs[0]);
			break;
		case 10:
			readyQueue.addLast(procs[1]);
			break;
		case 15:
			readyQueue.addLast(procs[2]);
			break;
		case 80:
			readyQueue.addLast(procs[3]);
			break;
		case 90:
			readyQueue.addLast(procs[4]);
			break;
		}
	}

	public static void main(String[] args) {
		new SchedulerInitialAttempt();
	}

}
