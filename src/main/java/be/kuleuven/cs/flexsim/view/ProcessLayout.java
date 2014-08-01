package be.kuleuven.cs.flexsim.view;

import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgrapht.ext.JGraphXAdapter;

import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.Resource;
import be.kuleuven.cs.flexsim.domain.util.Buffer;
import be.kuleuven.cs.flexsim.domain.workstation.Workstation;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;

/**
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 *
 */
public class ProcessLayout extends JApplet {

    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1024, 730);

    private JGraphXAdapter<Buffer<Resource>, Workstation> jgxAdapter;
    private ProductionLine p;

    public ProcessLayout(ProductionLine p2) {
        p = p2;
        jgxAdapter = new JGraphXAdapter<>(p.getLayout());
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        // mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter);
        // mxParallelEdgeLayout layout = new mxParallelEdgeLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        mxParallelEdgeLayout pe = new mxParallelEdgeLayout(jgxAdapter, 150);
        pe.execute(jgxAdapter.getDefaultParent());
        // that's all there is to it!...
    }

    public void draw() {
        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}