package be.kuleuven.cs.flexsim.view;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import be.kuleuven.cs.flexsim.domain.tso.CopperPlateTSO;
import be.kuleuven.cs.flexsim.domain.util.listener.Listener;

public class TSOSteersignalGrapher implements Tabbable {

    private XYSeries data;
    private int xcount;
    private final String NAME = "TSO Balance";

    public TSOSteersignalGrapher(CopperPlateTSO tso) {
        this.data = new XYSeries(NAME);
        this.xcount = 0;
        tso.addNewSteerValueListener(new Listener<Integer>() {

            @Override
            public void eventOccurred(Integer arg) {
                data.add(xcount, arg);
                increaseX(15);
            }
        });
    }

    private void increaseX(int amount) {
        xcount += amount;
    }

    public JFreeChart createChart() {
        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(this.data);
        return ChartFactory.createXYLineChart(NAME + "Graph", "time", NAME,
                data, PlotOrientation.VERTICAL, true, true, false);
    }

    @Override
    public JPanel getPanel() {
        final ChartPanel chartPanel = new ChartPanel(createChart());
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
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

}
