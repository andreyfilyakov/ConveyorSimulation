package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {
	public static double time = 0;
	public static final int n1 = 1, n2 = 2, n3 = 3, n4 = 2, n5 = 1;
	public static final Random random = new Random();
	public static final double simulationTime = 8 * 60;
	public static final LibrarianList firstLibrarians = new LibrarianList(n1),
			scienceLibrarians = new LibrarianList(n2), artLibrarians = new LibrarianList(n3),
			publicLibrarians = new LibrarianList(n4), lastLibrarians = new LibrarianList(n5);
	public static final EventList eventList = new EventList();
	public static final List<Request> readyRequests = new ArrayList<Request>();
	public static final double t0 = 3, t1 = 1.1, t2 = 20, t3 = 5, t4 = 15, t5 = 5, t6 = 2, t7 = 1;
	public static final double p1 = 0.3, p2 = 0.5, p3 = 0.2;

	public static void main(String[] args) {
		eventList.plan(new Event(0, exponential(t0)));
		eventList.plan(new Event(4, simulationTime));

		boolean finish = false;
		while (true) {
			Event currentEvent = eventList.getEvent();
			time = currentEvent.getTime();
			switch (currentEvent.getCode()) {
			case 0:
				getRequest();
				break;
			case 1:
				finishReception(currentEvent.getLibrarian());
				break;
			case 2:
				finishSearch(currentEvent.getLibrarian());
				break;
			case 3:
				finish(currentEvent.getLibrarian());
				break;
			case 4:
				finish = true;
				break;
			}

			if (finish) {
				break;
			}
		}

		double averageTime = 0;
		for (Request request : readyRequests) {
			averageTime += request.getTime();
		}
		averageTime /= readyRequests.size();

		System.out.println("Ready requests count: " + readyRequests.size());
		System.out.println("Average time for request (min): " + averageTime);
		System.out.println();
		double averageWorkTime = 0;
		for (Librarian librarian : scienceLibrarians.getLibrarians()) {
			averageWorkTime += librarian.getWorkTime();
		}
		System.out.println("Work coef for science: "
				+ averageWorkTime / (double) scienceLibrarians.getLibrarians().size() / simulationTime);
		averageWorkTime = 0;
		for (Librarian librarian : artLibrarians.getLibrarians()) {
			averageWorkTime += librarian.getWorkTime();
		}
		System.out.println("Work coef for art: "
				+ averageWorkTime / (double) artLibrarians.getLibrarians().size() / simulationTime);
		averageWorkTime = 0;
		for (Librarian librarian : publicLibrarians.getLibrarians()) {
			averageWorkTime += librarian.getWorkTime();
		}
		System.out.println("Work coef for public: "
				+ averageWorkTime / (double) publicLibrarians.getLibrarians().size() / simulationTime);
	}

	public static void getRequest() {
		eventList.plan(new Event(0, time + exponential(t0)));
		Request request = new Request(time);
		firstLibrarians.addToQueue(request);
		Librarian librarian = firstLibrarians.getLibrarian();
		if (librarian != null) {
			librarian.setBusy(true, time, firstLibrarians.getQueue().poll());
			eventList.plan(new Event(1, time + exponential(t1), librarian));
		}
	}

	public static void finishReception(Librarian librarian) {
		Request request = librarian.getRequest();
		librarian.setBusy(false, time, null);
		if (firstLibrarians.getQueue().size() > 0) {
			librarian.setBusy(true, time, firstLibrarians.getQueue().poll());
			eventList.plan(new Event(1, time + exponential(t1), librarian));
		}

		LibrarianList next;
		double searchTime;
		double pos = random.nextDouble();
		if (pos <= 0.3) {
			next = scienceLibrarians;
			searchTime = uniform(t2, t3);
		} else if (pos <= 0.8) {
			next = artLibrarians;
			searchTime = uniform(t2, t3);
		} else {
			next = publicLibrarians;
			searchTime = uniform(t4, t5);
		}
		next.addToQueue(request);
		Librarian nextLibrarian = next.getLibrarian();
		if (nextLibrarian != null) {
			nextLibrarian.setBusy(true, time, next.getQueue().poll());
			eventList.plan(new Event(2, time + searchTime, nextLibrarian));
		}
	}

	public static void finishSearch(Librarian librarian) {
		Request request = librarian.getRequest();
		librarian.setBusy(false, time, null);
		LibrarianList previous;
		double searchTime;
		if (scienceLibrarians.getLibrarians().contains(librarian)) {
			previous = scienceLibrarians;
			searchTime = uniform(t2, t3);
		} else if (artLibrarians.getLibrarians().contains(librarian)) {
			previous = artLibrarians;
			searchTime = uniform(t2, t3);
		} else {
			previous = publicLibrarians;
			searchTime = uniform(t4, t5);
		}
		if (previous.getQueue().size() > 0) {
			librarian.setBusy(true, time, previous.getQueue().poll());
			eventList.plan(new Event(2, time + searchTime, librarian));
		}

		lastLibrarians.addToQueue(request);
		Librarian nextLibrarian = lastLibrarians.getLibrarian();
		if (nextLibrarian != null) {
			nextLibrarian.setBusy(true, time, lastLibrarians.getQueue().poll());
			eventList.plan(new Event(3, time + uniform(t6, t7), nextLibrarian));
		}
	}

	public static void finish(Librarian librarian) {
		Request request = librarian.getRequest();
		librarian.setBusy(false, time, null);
		if (lastLibrarians.getQueue().size() > 0) {
			librarian.setBusy(true, time, lastLibrarians.getQueue().poll());
			eventList.plan(new Event(3, time + uniform(t6, t7), librarian));
		}

		request.end(time);
		readyRequests.add(request);
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
}