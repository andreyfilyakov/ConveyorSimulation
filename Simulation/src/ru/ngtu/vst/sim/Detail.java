package ru.ngtu.vst.sim;

public class Detail {
	private double startTime = 0;
	private double generalTime = 0;

	public Detail(double startTime) {
		this.startTime = startTime;
	}

	public void endTreatment(double time) {
		this.generalTime = time - this.startTime;
	}

	public double getGeneralTime() {
		return this.generalTime;
	}
}
