package ru.ngtu.vst.sim;

public class Machine {
	private boolean busy = false;
	private int startTime = 0;
	private int workTime = 0;
	private Detail detail = null;

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy, int currentTime, Detail detail) {
		this.busy = busy;
		this.detail = detail;
		if (this.busy) {
			this.startTime = currentTime;
		} else {
			this.workTime += currentTime - this.startTime;
		}
	}

	public int getWorkTime() {
		return this.workTime;
	}
	
	public Detail getDetail()
	{
		return this.detail;
	}
}
