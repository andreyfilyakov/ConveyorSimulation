package ru.ngtu.vst.sim;

import java.awt.Dimension;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;

public class Histogram extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	private final int N = 20; // number of sections

	public Histogram(List<Detail> details) {
		super("Details distribution");
		IntervalXYDataset dataset = createDataset(details);
		JFreeChart chart = ChartFactory.createXYBarChart("Details distribution", "time, min", false, "Details count",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(500, 270));
		setContentPane(panel);
	}

	private HistogramDataset createDataset(List<Detail> details) {
		double data[] = new double[details.size()];
		for (int i = 0; i < details.size();i++) {
			data[i] = details.get(i).getGeneralTime() / 60.0; // time in minutes
		}
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.FREQUENCY);
		dataset.addSeries("Details count", data, N);

		return dataset;
	}
}
