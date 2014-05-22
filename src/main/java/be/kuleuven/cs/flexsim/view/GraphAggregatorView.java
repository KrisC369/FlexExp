package be.kuleuven.cs.flexsim.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;

public class GraphAggregatorView extends ApplicationFrame {

    private List<Grapher> graphs;

    public GraphAggregatorView() {
        super("Aggregate Graph View");
        this.graphs = new ArrayList<>();
    }

    public void addGrapher(Grapher g) {
        this.graphs.add(g);
    }

    public void draw() {
        final ViewConfig c = new ViewConfig(new Dimension(350, 135), 3, 4);
        ChartPanel chartPanel;
        final JPanel totalGUI = new JPanel();
        final JPanel panel = new JPanel(
                new GridLayout(c.getLayX(), c.getLayY()));
        final JFrame frame = new JFrame("[=] FlexSim Graphs [=]");
        for (Grapher g : graphs) {
            chartPanel = new ChartPanel(g.createChart()) {

                @Override
                public Dimension getPreferredSize() {
                    c.getDimension();
                    return new Dimension((int) c.getDimension().getWidth()
                            / c.getLayX(), (int) c.getDimension().getHeight()
                            / c.getLayY());
                };
            };
            panel.add(chartPanel);
        }
        totalGUI.add(panel);
        totalGUI.setAutoscrolls(true);

        frame.setContentPane(totalGUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(@Nullable ComponentEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void componentResized(@Nullable ComponentEvent e) {
                if (e != null) {
                    try {
                        c.setDimension(((JFrame) e.getSource()).getSize());
                    } catch (ClassCastException ex) {
                        Logger.getLogger(this.getClass().toString()).log(
                                Level.WARNING,
                                "Event received from non-JFrame class");
                    }
                }

                for (Component p : panel.getComponents()) {
                    p.invalidate();
                }

                frame.validate();
            }

            @Override
            public void componentMoved(@Nullable ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentHidden(@Nullable ComponentEvent e) {
                // TODO Auto-generated method stub

            }
        });

        // //////////////////////// /////
        //
        // final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new
        // NumberAxis("Domain"));
        // plot.setGap(10.0);
        //
        // // add the subplots...
        // for(Grapher g : graphs){
        // plot.add(g.createXYPlot(), 1);
        // }
        // plot.setOrientation(PlotOrientation.VERTICAL);
        //
        // // return a new chart containing the overlaid plot...
        // new JFreeChart("CombinedDomainXYPlot Demo",
        // JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        // ChartPanel chartPanel = new ChartPanel(new
        // JFreeChart("CombinedDomainXYPlot Demo",
        // JFreeChart.DEFAULT_TITLE_FONT, plot, true));
        // chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // setContentPane(chartPanel);

        // /////////////////////
    }

    public void print() {
        for (Grapher g : graphs) {
            System.out.println(g.getTitle() + ":");
            for (XYSeries l : g.getSeries()) {
                System.out.println(l.getMaxY());

            }
        }

    }
}
