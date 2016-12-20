package ru.ngtu.vst.sim;

import java.util.ArrayList;
import java.util.List;

public class ExecutorList {

	private List<Executor> executorList = new ArrayList<Executor>();

	public ExecutorList(int count) {
		for (int i = 0; i < count; i++) {
			executorList.add(new Executor(i));
		}
	}

	public List<Executor> getExecutorList() {
		return this.executorList;
	}

	public Executor getExecutor() {
		Executor result = null;
		for (Executor executor : this.executorList) {
			if (!executor.isBusy()) {
				result = executor;
				break;
			}
		}
		return result;
	}

	public Executor getExecutor(int id) {
		Executor result = null;

		for (Executor executor : this.executorList) {
			if (executor.getId() == id) {
				result = executor;
				break;
			}
		}

		return result;
	}
}
