import java.util.LinkedList;

public class OS {

	private static int clockTime = 0;
	private static int quantumTime = 1, csTime = 2;
	private static LinkedList<Process2> allProcs = new LinkedList<Process2>();

	private static LinkedList<Process2> queue = new LinkedList<Process2>();
	private static Process2 curProc;
	private static int switchTimeRemaining = 0;
	private static int quantumLeft = 0;
	private static int firstJobDone = -1;
	private static boolean stopAfter800 = false;

	public static void main(String[] args) {
		createTestProccesses();
		while (true) {
			if (clockTime == 1000)
				break;
			checkNewProcs();
			if (switchTimeRemaining != 0 && csTime != 0) {
				switchTimeRemaining--;
				System.out.println(clockTime + "\t---CS---");
				clockTime++;
				continue;
			}
			if (needsProcessChange()) {
				System.out.println("need proc change!");
				if (clockTime != 0 && csTime != 0) {
					switchTimeRemaining = csTime;
					System.out.println(clockTime + "\t---CS---");
					switchTimeRemaining--;
				}
				curProc = getNextProcess();

				// if (csTime != 0)
				clockTime++;
				// switchTimeRemaining--;
				quantumLeft = quantumTime;
				continue;
			}

			if (curProc == null) {
				System.out.println(clockTime + " No processes in queue...");
				clockTime++;
				continue;

			}
			if (curProc.getSvcTimeElapsed() == 0) {
				curProc.setStartTime(clockTime);
			}
			if (curProc == null) {
				System.out.println("done?");
				System.exit(0);
			}

			curProc.work();

			if (curProc == null)
				System.out.println(clockTime + "\tcurProc=???");
			else
				System.out.println(clockTime + "\tcurProc=" + curProc.getPid() + "..." + curProc.getStatus() + "..."
						+ queue.size());

			// printQueue();

			clockTime++;
			quantumLeft--;
		}
		finishUp();
	}

	private static boolean needsProcessChange() {
		if (csTime == 0 && curProc != null) {
			if (curProc.isDone()) {
				if (firstJobDone == -1)
					firstJobDone = curProc.getPid();
				curProc.cleanup(clockTime);
				curProc = null;
				return true;
			} // else {
				// return false;
				// }
		}

		if (curProc != null && curProc.isDone()) {
			if (firstJobDone == -1)
				firstJobDone = curProc.getPid();
			curProc.cleanup(clockTime);
			curProc = null;
			return true;
		}
		if (quantumLeft == 0)
			return true;
		if (queue.isEmpty())
			return false;

		if (clockTime == 0)
			return true;
		// if ((clockTime) % (quantumTime + csTime) == 0) // end
		// of
		// quantum
		// {
		// System.out.println("clockTime:" + clockTime + "+csTime" + csTime +
		// "=" + (clockTime + csTime));
		// return true;
		// }
		return false;
	}

	private static Process2 getNextProcess() {
		if (curProc != null)
			queue.addLast(curProc);
		if (queue.size() == 0)
			return null;
		Process2 p = queue.removeFirst();
		return p;
	}

	private static void createTestProccesses() {
		/*
		 * Process2 a = new Process2(1, 20, 0); Process2 b = new Process2(2, 23,
		 * 0); Process2 c = new Process2(3, 14, 15); Process2 d = new
		 * Process2(4, 2, 35);
		 * 
		 * allProcs.add(a); allProcs.add(b); allProcs.add(c); allProcs.add(d);
		 */

		Process2[] procs = new Process2[] { new Process2(1, 75, 0), new Process2(2, 40, 10), new Process2(3, 25, 15),
				new Process2(4, 20, 80), new Process2(5, 45, 90) };
		for (Process2 p : procs) {
			allProcs.add(p);
		}
	}

	private static void printQueue() {
		System.out.print("Queue: ");
		for (Process2 p : queue) {
			System.out.print(p.getPid() + ",");
		}
		System.out.println();
	}

	private static void checkNewProcs() {
		for (Process2 p : allProcs) {
			if (p.getArrivalTime() == clockTime) {
				queue.add(p);
				System.out.println("New proccess " + p.getPid() + " arrived in queue.");
			}
		}
	}

	private static void finishUp() {
		System.out.println("Printing results...");

		System.out.println("PID of first job done: " + firstJobDone);

		System.out.println("PID\tiWait\ttWait\tTurnaround");
		// System.out.println("PID\tStart\tEnd\tiWait\ttWait\tTurnaround");
		int avgTurnaround = 0;
		int i = 0;
		for (Process2 p : allProcs) {
			i++;
			if (stopAfter800 && i > 800)
				break;
			int turnaroundTime = p.getTurnaroundTime();

			if (!stopAfter800) {
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
			System.out.println("Average turnaround time: " + ((double) avgTurnaround / allProcs.size()));
		else
			System.out.println("Average turnaround time: " + ((double) avgTurnaround / 800));

	}

}
