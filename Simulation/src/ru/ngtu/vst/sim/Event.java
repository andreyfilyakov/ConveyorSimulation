package ru.ngtu.vst.sim;

public class Event {
	// 0 - getting of a detail
	// 1 - delivery of a detail to the 1st machine
	// 2 - completion of the treatment in the 1st machine
	// 3 - delivery of a detail to the 2nd machine
	// 4 - completion of the treatment in the 2nd machine
	// 5 - delivery of a detail to the stock
	// 6 - finish of simulation
	private int code;
	private int time;

	Event(int code, int time) {
		this.code = code;
		this.time = time;
	}
	
	public int getCode()
	{
		return this.code;
	}
	
	public int getTime()
	{
		return this.time;
	}
}
