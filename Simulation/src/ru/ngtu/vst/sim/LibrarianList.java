package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LibrarianList {
	private List<Librarian> librarians = new ArrayList<Librarian>();
	private Queue<Request> queue = new LinkedList<Request>();

	public LibrarianList(int n) {
		for (int i = 0; i < n; i++) {
			librarians.add(new Librarian());
		}
	}

	public Librarian getLibrarian() {
		Librarian result = null;
		for (Librarian librarian : this.librarians) {
			if (!librarian.isBusy()) {
				result = librarian;
				break;
			}
		}
		return result;
	}

	public List<Librarian> getLibrarians() {
		return this.librarians;
	}
	
	public void addToQueue(Request request)
	{
		this.queue.add(request);
	}
	
	public Queue<Request> getQueue()
	{
		return this.queue;
	}
}
