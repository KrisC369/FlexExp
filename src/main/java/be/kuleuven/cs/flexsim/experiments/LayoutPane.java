package be.kuleuven.cs.flexsim.experiments;

import java.awt.Dimension;

import javax.swing.JApplet;

import org.jgrapht.ext.JGraphXAdapter;

import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.Resource;
import be.kuleuven.cs.flexsim.domain.util.Buffer;
import be.kuleuven.cs.flexsim.domain.workstation.Workstation;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;

public class LayoutPane extends JApplet {

    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1024, 730);

    private JGraphXAdapter<Buffer<Resource>, Workstation> jgxAdapter;
    private ProductionLine p;

    public LayoutPane(ProductionLine p2) {
        p = p2;
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        jgxAdapter = new JGraphXAdapter<>(p.getLayout());

        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        mxParallelEdgeLayout pe = new mxParallelEdgeLayout(jgxAdapter, 150);
        pe.execute(jgxAdapter.getDefaultParent());
        // that's all there is to it!...
    }
}