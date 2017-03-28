import java.util.LinkedList;

public class OS {

	private static int clockTime = 0;
	private static int quantumTime = 5, csTime = 1;
	private static LinkedList<Process2> allProcs = new LinkedList<Process2>();

	private static LinkedList<Process2> queue = new LinkedList<Process2>();
	private static Process2 curProc;
	private static int switchTimeRemaining = 0;
	private static int quantumLeft = 0;

	public static void main(String[] args) {
		createTestProccesses();
		while (true) {
			if (clockTime == 200)
				break;
			checkNewProcs();
			if (switchTimeRemaining != 0 && csTime != 0) {
				switchTimeRemaining--;
				System.out.println(clockTime + "\t---CSo---" + switchTimeRemaining);
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
				//if (csTime != 0)
					clockTime++;
				// switchTimeRemaining--;
				quantumLeft = quantumTime;
				continue;
			}
			if (curProc.getSvcTimeElapsed() == 0) {
				curProc.setStartTime(clockTime);
			}
			if (curProc == null) {
				System.out.println("done?");
				System.exit(0);
			}
			if (clockTime == 200) {
				System.out.println("200 clocktime");
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
	}

	private static boolean needsProcessChange() {
		if (csTime == 0 && curProc != null) {
			if (curProc.isDone()) {
				curProc.cleanup();
				curProc = null;
				return true;
			} else {
				return false;
			}
		}

		if (curProc != null && curProc.isDone()) {
			curProc.cleanup();
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
		Process2 p = queue.removeFirst();
		return p;
	}

	private static void createTestProccesses() {
		Process2 a = new Process2(1, 20, 0);
		Process2 b = new Process2(2, 23, 0);
		Process2 c = new Process2(3, 14, 15);
		Process2 d = new Process2(4, 2, 35);

		allProcs.add(a);
		allProcs.add(b);
		allProcs.add(c);
		allProcs.add(d);
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

}
