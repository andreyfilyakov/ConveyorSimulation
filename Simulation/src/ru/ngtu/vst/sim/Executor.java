package ru.ngtu.vst.sim;

public class Executor {
	private boolean busy = false;
	private double startTime = 0;
	private double workTime = 0;
	private Request request = null;
	private int id = -1;

	public Executor(int id)
	{
		this.id = id;
	}
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
	
	public int getId()
	{
		return this.id;
	}
}
