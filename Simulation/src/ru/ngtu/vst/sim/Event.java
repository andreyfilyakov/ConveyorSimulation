package ru.ngtu.vst.sim;

public class Event {
	private int code;
	private double time;

	Event(int code, double time) {
		this.code = code;
		this.time = time;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public double getTime()
	{
		return this.time;
	}
}
