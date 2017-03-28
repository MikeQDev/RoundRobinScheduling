
public class ProcessTEW {
	private final int pid;
	private final int svcTimeTotal;
	private final int arrivalTime;

	private int svcTimeElapsed = 0;
	private int startTime = -1;
	private int endTime = -1;
	private boolean isServiced = false;

	public ProcessTEW(int pid, int svcTimeTotal, int arrivalTime) {
		this.pid = pid;
		this.svcTimeTotal = svcTimeTotal;
		this.arrivalTime = arrivalTime;
		// status = Status.READY;
	}

	public boolean isServiced() {
		return isServiced;
	}

	public void setServiced() {

		if (!isServiced)
			isServiced = true;
	}

	/*
	 * public int interrupt() { return 0; }
	 */

	public int work() {
		svcTimeElapsed += 1;
		if (isDone()) // done{
			return 1;
		return 0;
	}

	public boolean isDone() {
		return svcTimeElapsed == svcTimeTotal;
	}

	public String toString() {
		return pid + "\t" + svcTimeElapsed + "/" + svcTimeTotal + "\t" + startTime + "," + endTime + "\t" + arrivalTime;
	}

	public int getSvcTimeElapsed() {
		return svcTimeElapsed;
	}

	public void setSvcTimeElapsed(int svcTimeElapsed) {
		this.svcTimeElapsed = svcTimeElapsed;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getPid() {
		return pid;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getTotalWaitTime() {
		return endTime - arrivalTime - svcTimeTotal;
	}

	public int getInitialWaitTime() {
		return startTime - arrivalTime;

	}

	public int getTurnaroundTime() {
		return endTime - arrivalTime;
	}

}
