package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.jfree.ui.RefineryUtilities;

public class Simulation {
	public static double time = 0;
	public static final Queue<Message> buffer = new LinkedList<Message>();
	public static final List<Message> transferedMessages = new ArrayList<Message>();
	public static final Random random = new Random();
	public static final double simulationTime = 8 * 60 * 60;
	public static final EventList eventList = new EventList();
	public static final Channel main = new Channel(), reserve = new Channel();
	public static final double R = 12, dR = 6, T1 = 10, dT1 = 5, T2 = 250, dT2 = 30, T3 = 4, T4 = 80, dT4 = 15, S1 = 70,
			S2 = 35, S3 = 0.02, K = 1 * 60 * 60;
	public static boolean isMainActive = true;
	public static int failCount = 0;
	public static double profit = 0;

	public static void main(String[] args) {
		eventList.plan(new Event(0, uniform(R, dR)));
		eventList.plan(new Event(3, uniform(T2, dT2) + K));
		eventList.plan(new Event(5, simulationTime));

		boolean finish = false;
		while (true) {
			Event currentEvent = eventList.getEvent();
			time = currentEvent.getTime();
			switch (currentEvent.getCode()) {
			case 0:
				getMessage();
				break;
			case 1:
				finishTransferOnMain();
				break;
			case 2:
				finishTransferOnReserve();
				break;
			case 3:
				getFail();
				break;
			case 4:
				finishRepair();
				break;
			case 5:
				finish = true;
				break;
			}

			if (finish) {
				break;
			}
		}

		double averageTime = 0;
		for (Message message : transferedMessages) {
			averageTime += message.getTransferTime();
		}
		averageTime /= transferedMessages.size();

		System.out.println("Messages count: " + transferedMessages.size());
		System.out.println("Average time for message (s): " + averageTime);
		System.out.println();
		System.out.println("Profit: " + (profit - K * S3));
		System.out.println("Fails count: " + failCount);
		System.out.println("Fails frequency (1/min): " + (double) failCount * 60.0 / (double) simulationTime);
		System.out.println("Work coef of main channel: " + (double) main.getWorkTime() / (double) simulationTime);
		System.out.println("Work coef of reserve channel: " + (double) reserve.getWorkTime() / (double) simulationTime);
		System.out.println();

		showHistogram(transferedMessages);
	}

	public static void getMessage() {
		eventList.plan(new Event(0, time + uniform(R, dR)));
		Message message = new Message(time);
		buffer.add(message);

		if (isMainActive) {
			if (!main.isBusy()) {
				main.setBusy(true, time, buffer.poll());
				eventList.plan(new Event(1, time + uniform(T1, dT1)));
			}
		} else {
			if (!reserve.isBusy()) {
				reserve.setBusy(true, time, buffer.poll());
				eventList.plan(new Event(2, time + uniform(T1, dT1)));
			}
		}
	}

	public static void finishTransferOnMain() {
		Message message = main.getMessage();
		main.setBusy(false, time, null);
		message.endTransfer(time);
		transferedMessages.add(message);
		profit += S1;

		if (buffer.size() > 0) {
			main.setBusy(true, time, buffer.poll());
			eventList.plan(new Event(1, time + uniform(T1, dT1)));
		}
	}

	public static void finishTransferOnReserve() {
		Message message = reserve.getMessage();
		reserve.setBusy(false, time, null);
		message.endTransfer(time);
		transferedMessages.add(message);
		profit += S2;

		if (!isMainActive) {
			if (buffer.size() > 0) {
				reserve.setBusy(true, time, buffer.poll());
				eventList.plan(new Event(2, time + uniform(T1, dT1)));
			}
		}
	}

	public static void getFail() {
		eventList.plan(new Event(3, time + uniform(T2, dT2) + K));
		isMainActive = false;
		if (main.isBusy()) {
			failCount++;
			Message message = main.getMessage();
			main.setBusy(false, time, null);
			eventList.removeFirstEvent(1);
			reserve.setBusy(true, time + T3, message);
			eventList.plan(new Event(2, time + T3 + uniform(T1, dT1)));
		}

		eventList.plan(new Event(4, time + uniform(T4, dT4)));
	}

	public static void finishRepair() {
		isMainActive = true;

		if (buffer.size() > 0) {
			main.setBusy(true, time, buffer.poll());
			eventList.plan(new Event(1, time + uniform(T1, dT1)));
		}
	}

	public static double uniform(double average, double deviation) {
		return average + random.nextDouble() * 2 * deviation - deviation;
	}

	private static void showHistogram(List<Message> messages) {
		Histogram histogram = new Histogram(messages);
		histogram.pack();
		RefineryUtilities.centerFrameOnScreen(histogram);
		histogram.setVisible(true);
	}
}