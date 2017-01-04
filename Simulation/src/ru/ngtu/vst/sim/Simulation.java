package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Simulation {
	public static double time = 0;
	public static final Queue<Passenger> queue = new LinkedList<Passenger>();
	public static final Random random = new Random(5);
	public static final double simulationTime = 15*(2.0/3.0) * 60;
	public static final EventList eventList = new EventList();
	public static final List<Passenger> readyPassengers = new ArrayList<Passenger>();
	public static final Bus A = new Bus(), B = new Bus();
	public static final double t1 = 0.5, t2 = 0.2, t3 = 20, t4 = 5, t5 = 30, t6 = 5, t7 = 2, t8 = 1;
	public static final int n = 25, l = 30, s = 2;
	public static double profit = 0;

	public static void main(String[] args) {
		eventList.plan(new Event(0, uniform(t1, t2)));
		eventList.plan(new Event(5, simulationTime));

		boolean finish = false;
		while (true) {
			Event currentEvent = eventList.getEvent();
			time = currentEvent.getTime();
			switch (currentEvent.getCode()) {
			case 0:
				getPassenger();
				break;
			case 1:
				endDriveOnA();
				break;
			case 2:
				endDriveOnB();
				break;
			case 3:
				returnA();
				break;
			case 4:
				returnB();
				break;
			case 5:
				finish = true;
				break;
			}

			if (finish) {
				break;
			}
		}

		double queueTime = 0;
		double driveTime = 0;
		for (Passenger passenger : readyPassengers) {
			queueTime += passenger.getQueueTime();
			driveTime += passenger.getDriveTime();
		}
		queueTime /= readyPassengers.size();
		driveTime /= readyPassengers.size();

		System.out.println("Queue time: " + queueTime);
		System.out.println("Common time: " + driveTime);
		System.out.println("Profit: " + profit);
		System.out.println("Work coef A: " + A.getWorkTime() / simulationTime);
		System.out.println("Work coef B: " + B.getWorkTime() / simulationTime);
	}

	public static void getPassenger() {
		eventList.plan(new Event(0, time + uniform(t1, t2)));

		if (queue.size() <= l) {
			Passenger passenger = new Passenger(time);
			queue.add(passenger);

			if (!A.onRoute()) {
				A.addPassenger(queue.poll(), time);
				if (A.getPassengers().size() == n) {
					A.start(time);
					eventList.plan(new Event(1, time + uniform(t3, t4) + uniform(t7, t8)));
				}
			} else if (!B.onRoute()) {
				B.addPassenger(queue.poll(), time);
				if (B.getPassengers().size() == n) {
					B.start(time);
					eventList.plan(new Event(2, time + uniform(t5, t6) + uniform(t7, t8)));
				}
			}
		} else {
			profit -= s;
		}
	}

	public static void endDriveOnA() {
		List<Passenger> passengers = A.getPassengers();
		for (Passenger passenger : passengers) {
			passenger.endDrive(time);
			readyPassengers.add(passenger);
			profit += s;
		}
		A.getPassengers().removeAll(passengers);

		eventList.plan(new Event(3, time + uniform(t3, t4)));
	}

	public static void endDriveOnB() {
		List<Passenger> passengers = B.getPassengers();
		for (Passenger passenger : passengers) {
			passenger.endDrive(time);
			readyPassengers.add(passenger);
			profit += s;
		}
		B.getPassengers().removeAll(passengers);

		eventList.plan(new Event(4, time + uniform(t5, t6)));
	}

	public static void returnA() {
		A.end(time);

		while (A.getPassengers().size() < n && queue.size() > 0) {
			A.addPassenger(queue.poll(), time);
		}

		if (A.getPassengers().size() == n) {
			A.start(time);
			eventList.plan(new Event(1, time + uniform(t3, t4) + uniform(t7, t8)));

			while (!B.onRoute() && B.getPassengers().size() < n && queue.size() > 0) {
				B.addPassenger(queue.poll(), time);
			}

			if (B.getPassengers().size() == n) {
				B.start(time);
				eventList.plan(new Event(2, time + uniform(t5, t6) + uniform(t7, t8)));
			}
		}
	}

	public static void returnB() {
		B.end(time);

		while (B.getPassengers().size() < n && queue.size() > 0 && A.onRoute()) {
			B.addPassenger(queue.poll(), time);
		}

		if (B.getPassengers().size() == n) {
			B.start(time);
			eventList.plan(new Event(2, time + uniform(t5, t6) + uniform(t7, t8)));
		}
	}

	public static double uniform(double average, double deviation) {
		return average + random.nextDouble() * 2 * deviation - deviation;
	}
}