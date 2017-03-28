import java.util.LinkedList;
import java.util.Random;

public class Scheduler {
	private static final int PROC_COUNT = 1000;
	private final int CONTEXTSWITCHTIME = 0;
	private final int QUANTUMSIZE = 3;

	private int clockTime = 0;
	private int curCtxSwitch = 0;
	private int firstJobDone = -1;
	private ProcessTEW curProc;
	private ProcessTEW[] procs;
	private LinkedList<ProcessTEW> readyQueue = new LinkedList<ProcessTEW>();
	private int elapsedSquares = 0;
	private boolean debug = true;
	private boolean stopAfter800 = false;

	public Scheduler() {
		if (debug) {
			procs = new ProcessTEW[] { new ProcessTEW(1, 75, 0), new ProcessTEW(2, 40, 10), new ProcessTEW(3, 25, 15),
					new ProcessTEW(4, 20, 80), new ProcessTEW(5, 45, 90) };
		} else {
			procs = new ProcessTEW[PROC_COUNT];
			findProcesses();
		}
		while (true) {
			if (clockTime == 0) {
				checkNewProcs();
				curProc = readyQueue.removeFirst();
			}
			if (CONTEXTSWITCHTIME == 0 && elapsedSquares % QUANTUMSIZE == 0) {

				System.out.println(clockTime + "\t-----CS-----");

				if (curProc != null) {
					readyQueue.addLast(curProc);
					curProc = null;
				}
				boolean allDone = true;
				if (readyQueue.isEmpty()) { // nothing in ready queue
					for (ProcessTEW p : procs) {
						if (!p.isDone()) {
							allDone = false;
							break;
						}
					} /// everything is done
					if (allDone) {
						finishUp();
						System.exit(0);
					}
				} else {
					curProc = readyQueue.removeFirst();
					elapsedSquares = 0;
				}

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
						boolean allDone = true;
						if (readyQueue.isEmpty()) { // nothing in ready queue
							for (ProcessTEW p : procs) {
								if (!p.isDone()) {
									allDone = false;
									break;
								}
							} /// everything is done
							if (allDone) {
								finishUp();
								System.exit(0);
							}
						} else {

							curProc = readyQueue.removeFirst();
							elapsedSquares = 0;
						}
					}
				}
			}
			checkNewProcs();
			if (curProc == null) {
				System.out.println("Found null curProc - CSing");
				curCtxSwitch = CONTEXTSWITCHTIME;
			} else if (curProc.work() == 1) {// done

				curCtxSwitch = CONTEXTSWITCHTIME;
				if (curProc.getSvcTimeElapsed() == 1)
					curProc.setStartTime(clockTime);
				curProc.setEndTime(clockTime);
				if (firstJobDone == -1) {
					firstJobDone = curProc.getPid();
				}
				System.out.println(clockTime + "\t" + curProc + " [TERMINATED]");
				if (stopAfter800 && curProc.getPid() == 799) {
					System.out.println("Job 800 finished!");
					finishUp();
					System.exit(0);
				}
				curProc = null;
			} else if (elapsedSquares == QUANTUMSIZE) {
				curCtxSwitch = CONTEXTSWITCHTIME;
				continue;
			} else {
				if (curProc.getSvcTimeElapsed() == 1)
					curProc.setStartTime(clockTime);
				System.out.println(clockTime + "\t" + curProc);
			}
			System.out.print(clockTime + "\tQUEUE:\t");
			for (ProcessTEW p : readyQueue) {
				System.out.print(p.getPid() + ",");
			}
			System.out.println();
			clockTime++;
			elapsedSquares++;
		}
	}

	private void checkNewProcs() {
		for (ProcessTEW p : procs) {
			if (p.getArrivalTime() == clockTime && !p.isServiced()) {
				p.setServiced();
				readyQueue.addLast(p);
			}
		}
	}

	private void finishUp() {
		System.out.println("Out of tasks to execute!");

		System.out.println("PID of first job done: " + firstJobDone);

		boolean testCase = !debug;
		if (!testCase)
			System.out.println("PID\tiWait\ttWait\tTurnaround");
		else
			System.out.println("PID\tStart\tEnd\tiWait\ttWait\tTurnaround");
		int avgTurnaround = 0;
		int i = 0;
		for (ProcessTEW p : procs) {
			i++;
			if (stopAfter800 && i > 800)
				break;
			int turnaroundTime = p.getTurnaroundTime();

			if (!testCase) {
				int initWait = p.getInitialWaitTime();
				int totalWait = p.getTotalWaitTime();
				System.out.println(p.getPid() + "\t" + initWait + "\t" + totalWait + "\t" + turnaroundTime);
			} else {
				if (i <= 10 || i >= 790)
					System.out.println(p.getPid() + "\t" + p.getStartTime() + "\t" + p.getEndTime() + "\t"
							+ p.getInitialWaitTime() + "\t" + p.getTotalWaitTime() + "\t" + p.getTurnaroundTime());
			}
			avgTurnaround += turnaroundTime;
		}
		if (!stopAfter800)
			System.out.println("Average turnaround time: " + ((double) avgTurnaround / procs.length));
		else
			System.out.println("Average turnaround time: " + ((double) avgTurnaround / 800));

	}

	/*
	 * This module will generate inter-arrival times. Inter-arrival times will
	 * be within a range. The values will be in between the minimum (=4) and
	 * maximum (=8). I will show you in class how to generate arrival times from
	 * the inter-arrival times.
	 */
	private int generateTime(int min, int max) {
		return (int) (min + (max - min + 1) * new Random().nextFloat());
	}

	private void findProcesses() {
		for (int i = 0; i < procs.length; i++) {
			int interarrival = generateTime(4, 8);
			int svc = generateTime(2, 5);
			if (i == 0) {
				procs[i] = new ProcessTEW(i, svc, 0);
				continue;
			}
			procs[i] = new ProcessTEW(i, svc, interarrival + procs[i - 1].getArrivalTime());
		}
	}

	public static void main(String[] args) {
		new Scheduler();
	}

}
