package be.kuleuven.cs.flexsim.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import be.kuleuven.cs.flexsim.simulation.InstrumentationComponent;
import be.kuleuven.cs.flexsim.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.events.Event;

import com.google.common.eventbus.Subscribe;

public abstract class Grapher extends ApplicationFrame implements
        InstrumentationComponent, Chartable {
    private static final long serialVersionUID = 3631429603857719172L;
    private final List<XYSeries> series;
    private final String name;
    private final Map<String, Integer> titlemap;
    private final XYSeriesCollection data;

    public Grapher(String title) {
        super(title);
        data = new XYSeriesCollection();
        series = new ArrayList<>();
        // series = new XYSeries(title);
        name = title;
        titlemap = new HashMap<>();
    }

    @Override
    public void initialize(SimulationContext context) {

    }

    @Subscribe
    public void recordReport(Event e) {
        if (e.getType().contains("report")) {
            record(e);
        }

    }

    protected abstract void record(Event e);

    protected void addRecord(String title, long x, long y) {
        if (!titlemap.containsKey(title)) {
            titlemap.put(title, series.size());
        }
        int idx = titlemap.get(title);
        if (idx >= series.size()) {
            series.add(idx, new XYSeries(title));
            data.addSeries(series.get(idx));
        }
        series.get(idx).add(x, y);
    }

    public void drawSingleChart() {
        final ChartPanel chartPanel = new ChartPanel(createChart());
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }

    @Override
    public JFreeChart createChart() {
        // final XYSeriesCollection data = new XYSeriesCollection();
        // for (XYSeries s : series) {
        // data.addSeries(s);
        // }
        return ChartFactory.createXYLineChart(name + "Graph", "time", name,
                data, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * @return the series
     */
    public final List<XYSeries> getSeries() {
        return new ArrayList<>(series);
    }

    /**
     * @return the titlemap
     */
    public final Map<String, Integer> getTitlemap() {
        return new HashMap<>(titlemap);
    }

    public String getChartablePaneName() {
        return getPaneName();
    }

    public String getChartableTitle() {
        return getTitle();
    }

    public static class StepConsumptionGrapher extends Grapher {
        private static final String STEPCONS = "Step consumption of energy";

        public StepConsumptionGrapher() {
            super(STEPCONS);
        }

        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            for (Entry<String, Object> es : e.getAttributes().entrySet()) {
                if (es.getKey().contains("totalLaststepE")) {
                    long y = e.getAttribute(es.getKey(), Integer.class);
                    addRecord(es.getKey(), t, y);
                }
            }
        }
    }

    public static class TotalComsumptionGrapher extends Grapher {
        private static final String STEPCONS = "Total consumption of energy";

        public TotalComsumptionGrapher() {
            super(STEPCONS);
        }

        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            for (Entry<String, Object> es : e.getAttributes().entrySet()) {
                if (es.getKey().contains("totalTotalE")) {
                    long y = e.getAttribute(es.getKey(), Integer.class);
                    addRecord(es.getKey(), t, y);
                }
            }
        }
    }

    public static class TotalProfitGrapher extends Grapher {
        private static final String STEPCONS = "Total Profit (reward-cost)";

        public TotalProfitGrapher() {
            super(STEPCONS);
        }

        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            for (Entry<String, Object> es : e.getAttributes().entrySet()) {
                if (es.getKey().contains("totalProfitM")) {
                    long y = e.getAttribute(es.getKey(), Integer.class);
                    addRecord(es.getKey(), t, y);
                }
            }
        }

    }

    public static class BufferLevelGrapher extends Grapher {
        private static final String STEPCONS = "Buffer Level of resources";

        public BufferLevelGrapher() {
            super(STEPCONS);
        }

        @Override
        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            for (Entry<String, Object> es : e.getAttributes().entrySet()) {
                if (es.getKey().contains("buffer")) {
                    Long value = e.getAttribute(es.getKey(), Long.class);
                    addRecord(es.getKey(), t, value);
                }
            }
        }
    }

    /**
     * @return the name
     */
    final String getPaneName() {
        return name;
    }
}
