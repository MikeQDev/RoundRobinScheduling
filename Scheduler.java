import java.util.LinkedList;

public class Scheduler {

	private final int contextSwitchTime = 0;
	private final int quantumSize = 1;

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
			if (contextSwitchTime == 0 && elapsedSquares == quantumSize) {
				System.out.println(clockTime + "\t-----CS-----");

				if (curProc != null) {
					readyQueue.addLast(curProc);
					curProc = null;
				}

				if (readyQueue.isEmpty()) {
					finishUp();
					System.exit(0);
				}
				curProc = readyQueue.removeFirst();
				elapsedSquares = 0;

			} else {
				while (curCtxSwitch != 0) {
					if (curProc != null) {
						readyQueue.addLast(curProc);
						curProc = null;
					}
					// if (contextSwitchTime != 0) {
					checkNewProcs();
					System.out.println(clockTime + "\t-----CS-----");
					curCtxSwitch--;
					clockTime++;
					// }
					if (curCtxSwitch == 0) {
						if (readyQueue.isEmpty()) {
							finishUp();
							System.exit(0);
						}
						curProc = readyQueue.removeFirst();
						elapsedSquares = 0;
					}
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
		for (Process p : procs) {
			if (p.getArrivalTime() == clockTime && !p.isServiced()) {
				p.setServiced();
				readyQueue.addLast(p);
			}
		}
	}

	private void finishUp() {
		System.out.println("Out of tasks to execute!");

		boolean testCase = true;
		if (!testCase)
			System.out.println("PID\tiWait\ttWait\tTurnaround");
		else
			System.out.println("PID\tStart\tEnd\tiWait\ttWait\tTurnaround");
		int avgTurnaround = 0;
		for (Process p : procs) {
			int turnaroundTime = p.getTurnaroundTime();

			if (!testCase) {
				int initWait = p.getInitialWaitTime();
				int totalWait = p.getTotalWaitTime();
				System.out.println(p.getPid() + "\t" + initWait + "\t" + totalWait + "\t" + turnaroundTime);
			} else {
				System.out.println(p.getPid() + "\t" + p.getStartTime() + "\t" + p.getEndTime() + "\t"
						+ p.getInitialWaitTime() + "\t" + p.getTotalWaitTime() + "\t" + p.getTurnaroundTime());
			}
			avgTurnaround += turnaroundTime;
			/*
			 * -Start time -End time -IntitialWait time -TotalWait time
			 * -Turnaround time
			 */
		}
		System.out.println("Average turnaround time: " + ((double) avgTurnaround / procs.length));
	}

	public static void main(String[] args) {
		new Scheduler();
	}

}
