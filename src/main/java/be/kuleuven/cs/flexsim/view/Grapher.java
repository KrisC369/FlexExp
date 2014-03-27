package be.kuleuven.cs.flexsim.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import be.kuleuven.cs.flexsim.simulation.InstrumentationComponent;
import be.kuleuven.cs.flexsim.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.events.Event;

import com.google.common.eventbus.Subscribe;

public abstract class Grapher extends ApplicationFrame implements
        InstrumentationComponent {
    private final List<XYSeries> series;
    private final String name;
    private final Map<String, Integer> titlemap;

    public Grapher(String title) {
        super(title);
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
        }
        series.get(idx).add(x, y);
    }

    public void drawChart() {
        final XYSeriesCollection data = new XYSeriesCollection();
        for (XYSeries s : series) {
            data.addSeries(s);
        }
        final JFreeChart chart = ChartFactory.createXYLineChart(name + "Graph",
                "time", name + " of Energy", data, PlotOrientation.VERTICAL,
                true, true, false);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
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

    public static class StepConsumptionGrapher extends Grapher {
        private static final String STEPCONS = "Step consumption";

        public StepConsumptionGrapher() {
            super(STEPCONS);
        }

        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            long y = e.getAttribute("totalLaststepE", Long.class);
            addRecord(STEPCONS, t, y);
        }
    }

    public static class TotalComsumptionGrapher extends Grapher {
        private static final String STEPCONS = "Total consumption";

        public TotalComsumptionGrapher() {
            super(STEPCONS);
        }

        protected void record(Event e) {
            int t = e.getAttribute("time", Integer.class);
            long y = e.getAttribute("totalTotalE", Long.class);
            addRecord(STEPCONS, t, y);
        }

    }

    public static class BufferLevelGrapher extends Grapher {
        private static final String STEPCONS = "Buffer Level";

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
}
