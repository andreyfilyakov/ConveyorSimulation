package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Simulation {
	public static double time = 0;
	public static final int n1 = 5, n2 = 10, k = 10, v1 = 40, v2 = 5;
	public static final ExecutorList channelList = new ExecutorList(n1), carList = new ExecutorList(n2);
	public static final Queue<Request> queue = new LinkedList<Request>();
	public static final Random random = new Random();
	public static final double simulationTime = 24 * 60 * 60;
	public static final List<Request> executedRequests = new ArrayList<Request>();
	public static final EventList eventList = new EventList();
	public static double s1 = 2, s2 = 0.5, s3 = 10, profit = -(n1 + n2) * s3, t1 = 180, t2 = 30, t3 = 60, t4 = 40 * 60,
			t5 = 10 * 60;
	public static int requestCount = 0;

	public static void main(String[] args) {
		eventList.plan(new Event(0, erlang(t1, 2)));
		eventList.plan(new Event(3, simulationTime));

		boolean finish = false;
		while (true) {
			Event currentEvent = eventList.getEvent();
			time = currentEvent.getTime();
			switch (currentEvent.getCode()) {
			case 0:
				getRequest();
				break;
			case 1:
				addToQueue(currentEvent.getExecutorId());
				break;
			case 2:
				finishDelivery(currentEvent.getExecutorId());
				break;
			case 3:
				finish = true;
				break;
			}

			if (finish) {
				break;
			}
		}

		double averageTime = 0;
		for (Request request : executedRequests) {
			averageTime += request.getExecutionTime();
		}
		averageTime /= executedRequests.size();

		System.out.println("Input requests count: " + requestCount);
		System.out.println("Executed requests count: " + executedRequests.size());
		System.out.println("Average time for request (min): " + averageTime / 60.0);
		System.out.println();
		double averageCoef = 0;
		for (Executor car : carList.getExecutorList()) {
			averageCoef += car.getWorkTime() / simulationTime;
		}
		System.out.println("Average work coef of cars: " + averageCoef / carList.getExecutorList().size());
		System.out.println();
		System.out.println("Profit (rub): " + profit);
	}

	public static void getRequest() {
		eventList.plan(new Event(0, time + erlang(t1, 2)));
		double callTime = 0;
		for (int i = 0; i < k; i++) {
			callTime += t2;
			Executor channel = channelList.getExecutor();
			if (channel == null || queue.size() > n2) {
				callTime += t3;
			} else {
				Request request = new Request(time);
				requestCount++;
				channel.setBusy(true, time + callTime, request);
				eventList.plan(new Event(1, time + callTime, channel.getId()));
				break;
			}
		}
	}

	public static void addToQueue(int executorId) {
		Executor channel = channelList.getExecutor(executorId);
		Request request = channel.getRequest();
		channel.setBusy(false, time, null);
		queue.add(request);
		profit += s1;

		Executor car = carList.getExecutor();
		if (car != null) {
			car.setBusy(true, time, queue.poll());
			double deliveryTime = uniform(t4, t5);
			eventList.plan(new Event(2, time + getDistance() / uniform(v1, v2) * 60 * 60 + deliveryTime, car.getId()));
			double distance = deliveryTime * uniform(v1, v2) / 60 / 60;
			profit += distance * s2;
		}
	}

	public static void finishDelivery(int executorId) {
		Executor car = carList.getExecutor(executorId);
		Request request = car.getRequest();
		car.setBusy(false, time, null);
		request.endExecution(time);
		executedRequests.add(request);

		if (queue.size() > 0) {
			car.setBusy(true, time, queue.poll());
			double deliveryTime = uniform(t4, t5);
			eventList.plan(new Event(2, time + getDistance() / uniform(v1, v2) * 60 * 60 + deliveryTime, car.getId()));
			double distance = deliveryTime * uniform(v1, v2) / 60 / 60;
			profit += distance * s2;
		}
	}

	public static double uniform(double average, double deviation) {
		return average + random.nextDouble() * 2 * deviation - deviation;
	}

	public static double erlang(double average, int k) {
		int sign = random.nextInt(2);
		double mult = 1;
		for (int i = 0; i < k; i++) {
			mult *= Math.log(random.nextDouble());
		}

		if (sign == 0) {
			return average - mult;
		} else {
			return average + mult;
		}
	}

	private static int getDistance() {
		int result = 0;
		double ran = random.nextDouble();

		if (ran < 0.1) {
			result = 5;
		} else if (ran < 0.3) {
			result = 8;
		} else if (ran < 0.55) {
			result = 9;
		} else if (ran < 0.72) {
			result = 11;
		} else if (ran < 0.95) {
			result = 12;
		} else {
			result = 20;
		}
		return result;
	}
}