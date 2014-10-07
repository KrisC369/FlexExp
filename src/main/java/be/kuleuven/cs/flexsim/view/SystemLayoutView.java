package be.kuleuven.cs.flexsim.view;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import be.kuleuven.cs.flexsim.domain.energy.tso.BalancingSignal;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.workstation.CurtailableWorkstation;
import be.kuleuven.cs.flexsim.domain.workstation.DualModeWorkstation;
import be.kuleuven.cs.flexsim.domain.workstation.Workstation;
import be.kuleuven.cs.flexsim.simulation.SimulationComponent;
import be.kuleuven.cs.flexsim.simulation.SimulationContext;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 *
 */
public class SystemLayoutView extends JApplet implements Tabbable {

    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1680, 1024);

    private DirectedGraph<SimulationComponent, MyEdge<SimulationComponent>> g;
    private String name;
    private Simulator context;
    private VisualizationViewer<SimulationComponent, MyEdge<SimulationComponent>> vv;

    public SystemLayoutView(Simulator sim) {
        this.context = sim;
        this.g = new DirectedSparseMultigraph<SimulationComponent, MyEdge<SimulationComponent>>();
        this.name = "JUNG visualisation of system layout";
        this.context.register(new SimulationComponent() {

            @Override
            public void initialize(SimulationContext context) {
            }

            @Override
            public void tick(int t) {
                if (t % 15 == 0) {
                    refresh();
                }
            }

            @Override
            public List<? extends SimulationComponent> getSimulationSubComponents() {
                return Collections.emptyList();
            }

            @Override
            public void afterTick(int t) {
            }
        });
        initGraph();
        vv = new VisualizationViewer<>(
                new FRLayout<SimulationComponent, MyEdge<SimulationComponent>>(
                        new DelegateForest<SimulationComponent, MyEdge<SimulationComponent>>(
                                g)));
    }

    private void refresh() {
        vv.repaint();

    }

    public void initGraph() {
        for (SimulationComponent comp : context.getSimulationComponents()) {
            g.addVertex(comp);
            addChildren(comp);
        }
    }

    public void addChildren(SimulationComponent c) {
        for (SimulationComponent sc : c.getSimulationSubComponents()) {
            g.addVertex(sc);
            if (g.findEdge(c, sc) == null) {
                g.addEdge(new MyEdge<SimulationComponent>(c, sc), c, sc,
                        EdgeType.DIRECTED);
                addChildren(sc);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        initGraph();
    }

    public JPanel getPanel() {
        // BasicVisualizationServer<SimulationComponent,
        // MyEdge<SimulationComponent>> vv = new BasicVisualizationServer<>(
        // new TreeLayout<SimulationComponent, MyEdge<SimulationComponent>>(
        // new DelegateForest<SimulationComponent, MyEdge<SimulationComponent>>(
        // g), 60, 20));
        vv.setPreferredSize(DEFAULT_SIZE);
        vv.getRenderContext().setVertexLabelTransformer(
                new Transformer<SimulationComponent, String>() {

                    @Override
                    public String transform(@Nullable SimulationComponent arg0) {
                        checkNotNull(arg0);
                        if (arg0 instanceof Workstation) {
                            return "Station";
                        }
                        if (arg0 instanceof Site) {
                            return "Site";
                        }
                        if (arg0 instanceof BalancingSignal) {
                            return "TSO";
                        }
                        if (arg0 instanceof ProductionLine) {
                            return "Process";
                        }
                        return "";
                    }
                });

        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        vv.getRenderContext().setVertexFillPaintTransformer(
                new Transformer<SimulationComponent, Paint>() {
                    @Override
                    public Paint transform(@Nullable SimulationComponent arg0) {
                        checkNotNull(arg0);
                        if (arg0 instanceof CurtailableWorkstation) {
                            if (((CurtailableWorkstation) arg0).isCurtailed()) {
                                return Color.RED;
                            }
                            return Color.GREEN;
                        }
                        if (arg0 instanceof DualModeWorkstation) {
                            if (!((DualModeWorkstation) arg0).isHigh()) {
                                return Color.RED;
                            }
                            return Color.GREEN;
                        }
                        if (arg0 instanceof Workstation) {
                            return Color.BLUE;
                        }
                        if (arg0 instanceof Site) {
                            return Color.YELLOW;
                        }
                        if (arg0 instanceof BalancingSignal) {
                            return Color.MAGENTA;
                        }
                        if (arg0 instanceof ProductionLine) {
                            return Color.ORANGE;
                        }
                        return Color.GRAY;
                    }
                });
        vv.getRenderContext().setEdgeStrokeTransformer(
                new Transformer<MyEdge<SimulationComponent>, Stroke>() {

                    @Override
                    public Stroke transform(
                            @Nullable MyEdge<SimulationComponent> arg0) {
                        checkNotNull(arg0);
                        float dash[] = { 10.0f };
                        if (arg0.getTarget() instanceof CurtailableWorkstation) {
                            if (((CurtailableWorkstation) arg0.getTarget())
                                    .isCurtailed()) {
                                return new BasicStroke(1.0f,
                                        BasicStroke.CAP_BUTT,
                                        BasicStroke.JOIN_MITER, 10.0f, dash,
                                        0.0f);
                            }
                        }
                        return new BasicStroke();
                    }
                });
        vv.setGraphMouse(new DefaultModalGraphMouse<SimulationComponent, MyEdge<SimulationComponent>>());
        return vv;
    }

    public String getViewTitle() {
        return "System layout";
    }

    public void draw() {
        JFrame frame = new JFrame();
        frame.getContentPane().add(getPanel());
        frame.setTitle(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}