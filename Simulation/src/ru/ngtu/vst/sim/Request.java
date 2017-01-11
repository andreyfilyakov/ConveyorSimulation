package ru.ngtu.vst.sim;

public class Request {
	private double startTime = 0;
	private double generalTime = 0;

	public Request(double startTime) {
		this.startTime = startTime;
	}

	public void end(double time) {
		this.generalTime = time - this.startTime;
	}

	public double getTime() {
		return this.generalTime;
	}
}
