package ru.ngtu.vst.sim;

public class Request {
	private double startTime = 0;
	private double executionTime = 0;

	public Request(double startTime) {
		this.startTime = startTime;
	}

	public void endExecution(double time) {
		this.executionTime = time - this.startTime;
	}

	public double getExecutionTime() {
		return this.executionTime;
	}
}
