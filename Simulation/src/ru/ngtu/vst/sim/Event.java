package ru.ngtu.vst.sim;

public class Event {
	private int code;
	private double time;
	private int executorId;

	Event(int code, double time) {
		this.code = code;
		this.time = time;
	}
	
	Event(int code, double time, int executorId) {
		this.code = code;
		this.time = time;
		this.executorId = executorId;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public double getTime()
	{
		return this.time;
	}
	
	public int getExecutorId()
	{
		return this.executorId;
	}
}
