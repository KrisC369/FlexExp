package be.kuleuven.cs.flexsim.view;

import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;

public interface Chartable {

    List<XYSeries> getSeries();

    JFreeChart createChart();

    String getChartablePaneName();

    String getChartableTitle();
}
