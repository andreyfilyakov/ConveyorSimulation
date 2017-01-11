package ru.ngtu.vst.sim;

public class Librarian {
	private boolean busy = false;
	private double startTime = 0;
	private double workTime = 0;
	private Request request = null;

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy, double currentTime, Request request) {
		this.busy = busy;
		this.request = request;
		if (this.busy) {
			this.startTime = currentTime;
		} else {
			this.workTime += currentTime - this.startTime;
		}
	}

	public double getWorkTime() {
		return this.workTime;
	}
	
	public Request getRequest()
	{
		return this.request;
	}
}
