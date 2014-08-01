package be.kuleuven.cs.flexsim.view;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;

import javax.annotation.Nullable;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.Resource;
import be.kuleuven.cs.flexsim.domain.util.Buffer;
import be.kuleuven.cs.flexsim.domain.workstation.CurtailableWorkstation;
import be.kuleuven.cs.flexsim.domain.workstation.Workstation;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 *
 */
public class ProcessLayout extends JApplet implements Tabbable {

    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1024, 730);

    private Layout<Buffer<Resource>, Workstation> layout;
    private String name;

    public ProcessLayout(ProductionLine p2) {
        layout = new CircleLayout<>(p2.getLayout());
        this.name = "JUNG visualisation of process layout";
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
    }

    public JPanel getPanel() {
        BasicVisualizationServer<Buffer<Resource>, Workstation> vv = new BasicVisualizationServer<>(
                layout);
        vv.setPreferredSize(DEFAULT_SIZE);
        vv.getRenderContext().setEdgeLabelTransformer(
                new ToStringLabeller<Workstation>());
        vv.getRenderContext().setVertexLabelTransformer(
                new ToStringLabeller<Buffer<Resource>>());

        vv.getRenderContext().setEdgeLabelTransformer(
                new Transformer<Workstation, String>() {

                    @Override
                    public String transform(@Nullable Workstation arg0) {
                        checkNotNull(arg0);
                        return shortenString(arg0);
                    }
                });
        vv.getRenderContext().setVertexLabelTransformer(
                new Transformer<Buffer<Resource>, String>() {

                    @Override
                    public String transform(@Nullable Buffer<Resource> arg0) {
                        checkNotNull(arg0);
                        return shortenString(arg0);
                    }
                });

        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
        vv.getRenderContext().setVertexFillPaintTransformer(
                new Transformer<Buffer<Resource>, Paint>() {

                    @Override
                    public Paint transform(@Nullable Buffer<Resource> arg0) {
                        checkNotNull(arg0);
                        if (arg0.isEmpty()) {
                            return Color.GREEN;
                        }
                        return Color.RED;
                    }
                });
        vv.getRenderContext().setEdgeStrokeTransformer(
                new Transformer<Workstation, Stroke>() {

                    @Override
                    public Stroke transform(@Nullable Workstation arg0) {
                        checkNotNull(arg0);
                        float dash[] = { 10.0f };
                        if (arg0 instanceof CurtailableWorkstation) {
                            if (((CurtailableWorkstation) arg0).isCurtailed()) {
                                return new BasicStroke(1.0f,
                                        BasicStroke.CAP_BUTT,
                                        BasicStroke.JOIN_MITER, 10.0f, dash,
                                        0.0f);
                            }
                        }
                        return new BasicStroke();
                    }
                });

        return vv;
    }

    private final String shortenString(Object arg0) {
        return arg0.toString().subSequence(0, arg0.toString().indexOf(" "))
                .toString();
    }

    public String getViewTitle() {
        return "Process layout";
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