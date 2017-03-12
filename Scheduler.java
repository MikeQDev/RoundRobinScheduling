import java.util.LinkedList;

public class Scheduler {

	private final int contextSwitchTime = 3;
	private final int quantumSize = 10;

	private int clockTime = 0;
	private int curCtxSwitch = 0;
	private Process curProc;
	private Process[] procs = { new Process(1, 75, 0), new Process(2, 40, 10), new Process(3, 25, 15),
			new Process(4, 20, 80), new Process(5, 45, 90) };
	private LinkedList<Process> readyQueue = new LinkedList<Process>();
	private int elapsedSquares = 0;

	public Scheduler() {
		while (true) {
			if (clockTime == 0) {
				checkNewProcs();
				curProc = readyQueue.removeFirst();
			}
			while (curCtxSwitch != 0) {
				if (curProc != null) {
					readyQueue.addLast(curProc);
					curProc = null;
				}
				checkNewProcs();
				System.out.println(clockTime + "\t-----CS-----");
				curCtxSwitch--;
				clockTime++;
				if (curCtxSwitch == 0) {
					if (readyQueue.isEmpty()) {
						finishUp();
						System.exit(0);
					}
					curProc = readyQueue.removeFirst();
					elapsedSquares = 0;
				}
			}
			checkNewProcs();
			if (curProc == null) {
				System.out.println("Found null curProc - CSing");
				curCtxSwitch = contextSwitchTime;
			} else if (curProc.work() == 1) {// done
				curCtxSwitch = contextSwitchTime;
				if (curProc.getSvcTimeElapsed() == 1)
					curProc.setStartTime(clockTime);
				curProc.setEndTime(clockTime);
				System.out.println(clockTime + "\t" + curProc + " [TERMINATED]");
				curProc = null;
			} else if (elapsedSquares == quantumSize) {
				curCtxSwitch = contextSwitchTime;
				continue;
			} else {
				if (curProc.getSvcTimeElapsed() == 1)
					curProc.setStartTime(clockTime);
				System.out.println(clockTime + "\t" + curProc);
			}
			System.out.print(clockTime + "\tQUEUE:\t");
			for (Process p : readyQueue) {
				System.out.print(p.getPid() + ",");
			}
			System.out.println();
			clockTime++;
			elapsedSquares++;
		}
	}

	private void checkNewProcs() {
		switch (clockTime) { // add to ready queue
		case 0:
			if (!procs[0].isServiced()) {
				procs[0].setServiced();
				readyQueue.addLast(procs[0]);
			}
			break;
		case 10:
			if (!procs[1].isServiced()) {
				procs[1].setServiced();
				readyQueue.addLast(procs[1]);
			}
			break;
		case 15:
			if (!procs[2].isServiced()) {
				procs[2].setServiced();
				readyQueue.addLast(procs[2]);
			}
			break;
		case 80:
			if (!procs[3].isServiced()) {
				procs[3].setServiced();
				readyQueue.addLast(procs[3]);
			}
			break;
		case 90:
			if (!procs[4].isServiced()) {
				procs[4].setServiced();
				readyQueue.addLast(procs[4]);
			}
			break;
		}
	}

	private void finishUp() {
		System.out.println("Out of tasks to execute!");
		
		System.out.println("PID\tiWait\ttWait\tTurnaround");
		int avgTurnaround = 0;
		for (Process p : procs) {
			int initWait = p.getInitialWaitTime();
			int totalWait = p.getTotalWaitTime();
			int turnaroundTime = p.getTurnaroundTime();
			avgTurnaround += turnaroundTime;
			System.out.println(p.getPid() + "\t" + initWait + "\t" + totalWait + "\t" + turnaroundTime);
		}
		System.out.println("Average turnaround time: "+((double)avgTurnaround/procs.length));
	}

	public static void main(String[] args) {
		new Scheduler();
	}

}
