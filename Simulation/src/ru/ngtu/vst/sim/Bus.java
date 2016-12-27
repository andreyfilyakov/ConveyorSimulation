package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.List;

public class Bus {
	private boolean onRoute = false;
	private double startTime = 0;
	private double workTime = 0;
	private List<Passenger> passengers = new ArrayList<Passenger>();

	public boolean onRoute() {
		return onRoute;
	}

	public void addPassenger(Passenger passenger) {
		this.passengers.add(passenger);
	}

	public void start(double time) {
		this.onRoute = true;
		this.startTime = time;
		for (Passenger passenger : this.passengers) {
			passenger.startDrive(time);
		}
	}

	public void end(double time) {
		this.onRoute = false;
		this.workTime += time - this.startTime;
	}

	public double getWorkTime() {
		return this.workTime;
	}

	public List<Passenger> getPassengers() {
		return this.passengers;
	}
}