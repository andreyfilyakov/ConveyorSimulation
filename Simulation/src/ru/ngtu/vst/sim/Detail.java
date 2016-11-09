package ru.ngtu.vst.sim;

public class Detail {
	private int startTime = 0;
	private int generalTime = 0;

	public Detail(int startTime) {
		this.startTime = startTime;
	}

	public void endTreatment(int time) {
		this.generalTime = time - this.startTime;
	}

	public int getGeneralTime() {
		return this.generalTime;
	}
}
