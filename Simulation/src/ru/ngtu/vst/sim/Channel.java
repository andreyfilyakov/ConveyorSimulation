package ru.ngtu.vst.sim;

public class Channel {
	private boolean busy = false;
	private double startTime = 0;
	private double workTime = 0;
	private Message message = null;

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy, double currentTime, Message message) {
		this.busy = busy;
		this.message = message;
		if (this.busy) {
			this.startTime = currentTime;
		} else {
			this.workTime += currentTime - this.startTime;
		}
	}

	public double getWorkTime() {
		return this.workTime;
	}
	
	public Message getMessage()
	{
		return this.message;
	}
}
