package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventList {
	private List<Event> e_list = new ArrayList<Event>();

	public void plan(Event e) {
		e_list.add(e);
		Collections.sort(e_list, new Comparator<Event>() {
			public int compare(Event e1, Event e2) {
				return Integer.compare(e1.getTime(), e2.getTime());
			}
		});
	}

	public Event getEvent() {
		return e_list.remove(0);
	}

}
