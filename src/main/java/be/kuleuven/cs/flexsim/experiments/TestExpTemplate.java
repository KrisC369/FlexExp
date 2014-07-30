package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.events.SimEventFactory;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.ProcessLayout;

public class TestExpTemplate {

    public static void main(String[] args) {
        List<TestExpTemplate> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();

        int numberOfVariations = 8;
        for (int i = 0; i < numberOfVariations; i++) {
            apps.add(new TestExpTemplate());
            apps.get(i).addGrapher(agg1, new Grapher.BufferLevelGrapher());
            apps.get(i).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
            apps.get(i).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
            apps.get(i).addGrapher(agg4, new Grapher.TotalProfitGrapher());

            apps.get(i).init();
            apps.get(i).configureCurtailable(i);
            apps.get(i).start();
            apps.get(i).post();
        }
        agg1.draw();
        agg2.draw();
        agg3.draw();
        agg4.draw();

        agg3.print();
        agg4.print();

        visualise(apps.get(0).p);
    }

    private Simulator s;
    private ProductionLine p;
    private List<Grapher> graphs;
    private FinanceTrackerImpl ft;

    public TestExpTemplate() {
        s = Simulator.createSimulator(4000);
        // p = ProductionLine.createStaticCurtailableLayout();
        p = buildLine();
        ft = FinanceTrackerImpl.createDefault(p);
        graphs = new ArrayList<>();
    }

    private ProductionLine buildLine() {
        return new ProductionLine.ProductionLineBuilder().addShifted(7)
                .addShifted(7).addShifted(4)
                .addMultiCapExponentialConsuming(1, 50).build();
        // return new ProductionLine.ProductionLineBuilder()
        // .addMultiCapExponentialConsuming(1, 50).build();
    }

    public void configureCurtailable(final int numberOfCurtInstances) {
        SimEventFactory fac = new SimEventFactory(s, p);
        fac.controlStationFavorSpeed(200, numberOfCurtInstances);

    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    private static void visualise(ProductionLine p2) {
        ProcessLayout lp = new ProcessLayout(p2);
        lp.init();
        JFrame frame = new JFrame();
        frame.getContentPane().add(lp);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void init() {
        s.register(p);
        s.register(ft);
        // p.deliverResources(ResourceFactory.createBulkMPResource(60,0, 3, 1));
        // p.deliverResources(ResourceFactory.createBulkMPResource(60,0, 2, 3,
        // 2));
        p.deliverResources(ResourceFactory.createBulkMPResource(10000, 0, 2, 2,
                2, 1000));
        // p.deliverResources(ResourceFactory.createBulkMPResource(10000, 0,
        // 1000));
    }

    public void start() {
        s.start();
    }

    public void post() {
        // for (Grapher gs : graphs) {
        // gs.drawSingleChart();
        // }
    }
}
