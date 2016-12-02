package ru.ngtu.vst.sim;

public class Robot extends Machine {
	private int target = -1;
	private int position = 0;

	public Robot(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getTarget() {
		return this.target;
	}

	public void setBusy(boolean busy, double currentTime, Detail detail, int target) {
		super.setBusy(busy, currentTime, detail);
		this.target = target;
	}
}
