package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.jfree.ui.RefineryUtilities;

public class Simulation {
	public static double time = 0;
	public static final List<Queue<Detail>> queueList = new ArrayList<Queue<Detail>>();
	public static final List<Integer> maxQueueSizes = new ArrayList<Integer>();
	public static final Random random = new Random();
	public static final double simulationTime = 8 * 60 * 60;
	public static final List<Machine> machineList = new ArrayList<Machine>();
	public static final RobotList robotList = new RobotList();
	public static final EventList eventList = new EventList();
	public static final List<Detail> readyDetails = new ArrayList<Detail>();
	public static final double t0 = 70, t1 = 10, t2 = 15, t3 = 20, t4 = 15, t5 = 3, t6 = 140, t7 = 20, t8 = 180;

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			queueList.add(new LinkedList<Detail>());
			maxQueueSizes.add(0);
		}
		for (int i = 0; i < 2; i++) {
			machineList.add(new Machine());
		}

		eventList.plan(new Event(0, exponential(t0)));
		eventList.plan(new Event(6, simulationTime));

		boolean finish = false;
		while (true) {
			Event currentEvent = eventList.getEvent();
			time = currentEvent.getTime();
			// 0 - getting of a detail
			// 1 - delivery of a detail to the 1st machine
			// 2 - completion of the treatment in the 1st machine
			// 3 - delivery of a detail to the 2nd machine
			// 4 - completion of the treatment in the 2nd machine
			// 5 - delivery of a detail to the stock
			// 6 - finish of simulation
			switch (currentEvent.getCode()) {
			case 0:
				handleDetailGetting();
				break;
			case 1:
				handleDeliveryToPosition(1);
				break;
			case 2:
				handleTreatmentOnMachine(1);
				break;
			case 3:
				handleDeliveryToPosition(2);
				break;
			case 4:
				handleTreatmentOnMachine(2);
				break;
			case 5:
				handleDeliveryToPosition(3);
				break;
			case 6:
				finish = true;
				break;
			}

			if (finish) {
				break;
			}
		}

		double averageTime = 0;
		for (Detail detail : readyDetails) {
			averageTime += detail.getGeneralTime();
		}
		averageTime /= readyDetails.size();

		System.out.println("Details count: " + readyDetails.size());
		System.out.println("Average time for detail (min): " + averageTime / 60);
		System.out.println();
		for (Robot robot : robotList.getRobotList()) {
			System.out.println("Work coef of robot #" + robotList.getRobotList().indexOf(robot) + ": "
					+ (double) robot.getWorkTime() / (double) simulationTime);
		}
		for (Machine machine : machineList) {
			System.out.println("Work coef of machine #" + machineList.indexOf(machine) + ": "
					+ (double) machine.getWorkTime() / (double) simulationTime);
		}
		System.out.println();
		for (int i = 0; i < maxQueueSizes.size(); i++) {
			int size = maxQueueSizes.get(i);
			System.out.println("Max size of queue #" + i + ": " + size);
		}

		showHistogram(readyDetails);
	}

	public static void handleDetailGetting() {
		eventList.plan(new Event(0, exponential(time + t0)));
		Detail detail = new Detail(time);
		queueList.get(0).add(detail);
		if (maxQueueSizes.get(0) < queueList.get(0).size()) {
			maxQueueSizes.set(0, queueList.get(0).size());
		}

		Robot robot = robotList.getNearestRobot(0);
		if (robot != null) {
			robot.setBusy(true, time, queueList.get(0).poll(), 1);
			double deliveryTime = timeBetweenPositions(0, robot.getPosition());
			deliveryTime += uniform(t4, t5);
			deliveryTime += t1;
			deliveryTime += uniform(t4, t5);
			eventList.plan(new Event(1, time + deliveryTime));
		}
	}

	public static void handleDeliveryToPosition(int position) {
		Robot robot = robotList.getRobotWithTarget(position);
		if (robot != null) {
			Detail detail = robot.getDetail();
			robot.setPosition(position);
			robot.setBusy(false, time, null, -1);
			switch (position) {
			case 1:
				if (machineList.get(0).isBusy()) {
					queueList.get(1).add(detail);
					if (maxQueueSizes.get(1) < queueList.get(1).size()) {
						maxQueueSizes.set(1, queueList.get(1).size());
					}
				} else {
					machineList.get(0).setBusy(true, time, detail);
					double treatmentTime = normal(t6, t7);
					eventList.plan(new Event(2, time + treatmentTime));
				}
				break;
			case 2:
				if (machineList.get(1).isBusy()) {
					queueList.get(3).add(detail);
					if (maxQueueSizes.get(3) < queueList.get(3).size()) {
						maxQueueSizes.set(3, queueList.get(3).size());
					}
				} else {
					machineList.get(1).setBusy(true, time, detail);
					double treatmentTime = exponential(t8);
					eventList.plan(new Event(4, time + treatmentTime));
				}
				break;
			case 3:
				detail.endTreatment(time);
				readyDetails.add(detail);
				break;
			}

			Queue<Detail> nearestQueue = robotList.getNearestQueue(robot.getPosition(), queueList);
			if (nearestQueue != null) {
				int queuePosition = queueList.indexOf(nearestQueue) / 2;
				robot.setBusy(true, time, nearestQueue.poll(), queuePosition + 1);
				double deliveryTime = timeBetweenPositions(queuePosition, robot.getPosition());
				deliveryTime += uniform(t4, t5);
				deliveryTime += timeBetweenPositions(queuePosition, queuePosition + 1);
				deliveryTime += uniform(t4, t5);
				eventList.plan(new Event(queuePosition * 2 + 1, time + deliveryTime));
			}

		}
	}

	public static void handleTreatmentOnMachine(int machineNumber) {
		Machine machine = machineList.get(machineNumber - 1);
		Detail detail = machine.getDetail();
		machine.setBusy(false, time, null);
		Robot robot = robotList.getNearestRobot(machineNumber);

		switch (machineNumber) {
		case 1:
			queueList.get(2).add(detail);
			if (maxQueueSizes.get(2) < queueList.get(2).size()) {
				maxQueueSizes.set(2, queueList.get(2).size());
			}
			if (robot != null) {
				robot.setBusy(true, time, queueList.get(2).poll(), 2);
				double deliveryTime = timeBetweenPositions(1, robot.getPosition());
				deliveryTime += uniform(t4, t5);
				deliveryTime += t2;
				deliveryTime += uniform(t4, t5);
				eventList.plan(new Event(3, time + deliveryTime));
			}

			if (queueList.get(1).size() > 0) {
				machine.setBusy(true, time, queueList.get(1).poll());
				double treatmentTime = normal(t6, t7);
				eventList.plan(new Event(2, time + treatmentTime));
			}
			break;
		case 2:
			queueList.get(4).add(detail);
			if (maxQueueSizes.get(4) < queueList.get(4).size()) {
				maxQueueSizes.set(4, queueList.get(4).size());
			}
			if (robot != null) {
				robot.setBusy(true, time, queueList.get(4).poll(), 3);
				double deliveryTime = timeBetweenPositions(2, robot.getPosition());
				deliveryTime += uniform(t4, t5);
				deliveryTime += t3;
				deliveryTime += uniform(t4, t5);
				eventList.plan(new Event(5, time + deliveryTime));
			}

			if (queueList.get(3).size() > 0) {
				machine.setBusy(true, time, queueList.get(3).poll());
				double treatmentTime = exponential(t8);
				eventList.plan(new Event(4, time + treatmentTime));
			}
			break;
		}
	}

	public static double uniform(double average, double deviation) {
		return average + random.nextDouble() * 2 * deviation - deviation;
	}

	public static double exponential(double average) {
		int sign = random.nextInt(2);
		if (sign == 0) {
			return average - Math.log(random.nextDouble());
		} else {
			return average + Math.log(random.nextDouble());
		}

	}

	public static double normal(double average, double deviation) {
		double x, y, s;
		while (true) {
			x = random.nextDouble() * 2 - 1;
			y = random.nextDouble() * 2 - 1;
			s = Math.pow(x, 2) + Math.pow(y, 2);
			if (s > 0 && s <= 1) {
				break;
			}
		}

		double z1 = x * Math.pow((-2 * Math.log(s) / s), 0.5), z2 = y * Math.pow((-2 * Math.log(s) / s), 0.5);
		return average + deviation * (time % 2 > 0 ? z1 : z2);
	}

	public static double timeBetweenPositions(int position1, int position2) {
		double result = 0;

		if ((position1 == 0 && position2 == 1 || position1 == 1 && position2 == 0)) {
			result = t1;
		} else if (position1 == 0 && position2 == 2 || position1 == 2 && position2 == 0) {
			result = t1 + t2;
		} else if (position1 == 0 && position2 == 3 || position1 == 3 && position2 == 0) {
			result = t1 + t2 + t3;
		} else if (position1 == 1 && position2 == 2 || position1 == 2 && position2 == 1) {
			result = t2;
		} else if (position1 == 1 && position2 == 3 || position1 == 3 && position2 == 1) {
			result = t2 + t3;
		} else if (position1 == 2 && position2 == 3 || position1 == 3 && position2 == 2) {
			result = t3;
		}

		return result;
	}

	private static void showHistogram(List<Detail> details) {
		Histogram histogram = new Histogram(details);
		histogram.pack();
		RefineryUtilities.centerFrameOnScreen(histogram);
		histogram.setVisible(true);
	}
}