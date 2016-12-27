package ru.ngtu.vst.sim;

public class Passenger {
	private double entryTime = 0;
	private double queueTime = 0;
	private double startTime = 0;
	private double driveTime = 0;
	
	public void addToQueue(double time)
	{
		this.entryTime = time;
	}
	
	public void getToBus(double time)
	{
		this.queueTime = time - this.entryTime;
	}
	
	public void startDrive(double time)
	{
		this.startTime = time;
	}

	public void endDrive(double time) {
		this.driveTime = time - this.startTime;
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
