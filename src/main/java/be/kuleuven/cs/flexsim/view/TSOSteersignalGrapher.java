package be.kuleuven.cs.flexsim.view;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import be.kuleuven.cs.flexsim.domain.energy.tso.BalancingSignal;
import be.kuleuven.cs.flexsim.domain.util.listener.Listener;
import be.kuleuven.cs.flexsim.domain.util.listener.MultiplexListener;
import be.kuleuven.cs.flexsim.domain.util.listener.NoopListener;

import com.google.common.collect.Lists;

public class TSOSteersignalGrapher implements Tabbable, Chartable,
        RefreshTrigger {

    private XYSeries data;
    private int xcount;
    private final String NAME = "TSO Balance";
    private Listener<Object> ob = NoopListener.INSTANCE;

    public TSOSteersignalGrapher(BalancingSignal tso) {
        this.data = new XYSeries(NAME);
        this.xcount = 0;
        tso.addNewBalanceValueListener(new Listener<Integer>() {
            @Override
            public void eventOccurred(Integer arg) {
                data.add(xcount, arg);
                increaseX(15);
                notifyObs();
            }
        });
    }

    private void notifyObs() {
        ob.eventOccurred(this);
    }

    private void increaseX(int amount) {
        xcount += amount;
    }

    @Override
    public JFreeChart createChart() {
        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(this.data);
        return ChartFactory.createXYLineChart(NAME + "Graph", "time", NAME,
                data, PlotOrientation.VERTICAL, true, true, false);
    }

    @Override
    public List<XYSeries> getSeries() {
        return Lists.newArrayList(this.data);
    }

    @Override
    public JPanel getPanel() {
        final ChartPanel chartPanel = new ChartPanel(createChart());
        chartPanel.setPreferredSize(new java.awt.Dimension(720, 1024));
        return chartPanel;
    }

    public void draw() {
        JFrame frame = new JFrame();
        frame.getContentPane().add(getPanel());
        frame.setTitle(NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public String getViewTitle() {
        return "TSO-Balance";
    }

    @Override
    public String getChartablePaneName() {
        return NAME;
    }

    @Override
    public String getChartableTitle() {
        return NAME;
    }

    @Override
    public void subscribeForTrigger(Listener l) {
        this.ob = MultiplexListener.plus(ob, l);
    }
}
