package ru.ngtu.vst.sim;

public class Message {
	private double startTime = 0;
	private double transferTime = 0;

	public Message(double startTime) {
		this.startTime = startTime;
	}

	public void endTransfer(double time) {
		this.transferTime = time - this.startTime;
	}

	public double getTransferTime() {
		return this.transferTime;
	}
}
