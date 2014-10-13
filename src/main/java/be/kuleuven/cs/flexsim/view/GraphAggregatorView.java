package be.kuleuven.cs.flexsim.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;

import com.google.common.collect.Lists;

public class GraphAggregatorView extends ApplicationFrame implements Tabbable {

    private List<Chartable> graphs;
    private ViewConfig viewConfig;

    public GraphAggregatorView() {
        super("Aggregate Graph View");
        this.graphs = Lists.newArrayList();
        this.viewConfig = new ViewConfig(new Dimension(350, 350), 2, 1);
    }

    public void addGrapher(Chartable g) {
        this.graphs.add(g);
    }

    private ViewConfig getviewConfig() {
        return viewConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see be.kuleuven.cs.flexsim.view.Tabbable#getPanel()
     */
    @Override
    public JPanel getPanel() {
        final ViewConfig c = getviewConfig();
        ChartPanel chartPanel;
        final JPanel panel = new JPanel(
                new GridLayout(c.getLayX(), c.getLayY()));
        for (Chartable g : graphs) {
            chartPanel = new ChartPanel(g.createChart()) {

                private static final long serialVersionUID = -6487775156176874038L;

                @Override
                public Dimension getPreferredSize() {
                    return new Dimension((int) c.getDimension().getWidth()
                            / c.getLayX(), (int) c.getDimension().getHeight()
                            / c.getLayY());
                };
            };
            panel.add(chartPanel);
        }
        return panel;

    }

    public void draw() {

        final JFrame frame = new JFrame("[=] FlexSim Graphs [=]");
        final JPanel panel = getPanel();
        final JPanel totalGUI = new JPanel();
        totalGUI.add(panel);
        totalGUI.setAutoscrolls(true);

        frame.setContentPane(getPanel());
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
                        getviewConfig().setDimension(
                                ((JFrame) e.getSource()).getSize());
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

    }

    public void print() {
        for (Chartable g : graphs) {
            System.out.println(g.getChartableTitle() + ":");
            for (XYSeries l : g.getSeries()) {
                System.out.println(l.getY(l.getItemCount() - 1));

            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see be.kuleuven.cs.flexsim.view.Tabbable#getViewTitle()
     */
    @Override
    public String getViewTitle() {
        String res = "";
        if (graphs.size() > 0) {
            res += graphs.get(0).getChartablePaneName();
        }
        return res;
    }

    public List<Chartable> getChartable() {
        return this.graphs;
    }
}
