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

	public Histogram(List<Message> messages) {
		super("Messages distribution");
		IntervalXYDataset dataset = createDataset(messages);
		JFreeChart chart = ChartFactory.createXYBarChart("Messages distribution", "time, s", false, "Messages count",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(500, 270));
		setContentPane(panel);
	}

	private HistogramDataset createDataset(List<Message> messages) {
		double data[] = new double[messages.size()];
		for (int i = 0; i < messages.size();i++) {
			data[i] = messages.get(i).getTransferTime();
		}
		HistogramDataset dataset = new HistogramDataset();
		dataset.setType(HistogramType.FREQUENCY);
		dataset.addSeries("Messages count", data, N);

		return dataset;
	}
}
