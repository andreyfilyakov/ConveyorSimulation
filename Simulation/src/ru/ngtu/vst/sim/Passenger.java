package ru.ngtu.vst.sim;

public class Passenger {
	private double entryTime = 0;
	private double queueTime = 0;
	private double driveTime = 0;
	
	public Passenger(double time)
	{
		this.entryTime = time;
	}
	
	public void getToBus(double time)
	{
		this.queueTime = time - this.entryTime;
	}

	public void endDrive(double time) {
		this.driveTime = time - this.entryTime;
	}

	public double getQueueTime()
	{
		return this.queueTime;
	}
	
	public double getDriveTime()
	{
		return this.driveTime;
	}
}
