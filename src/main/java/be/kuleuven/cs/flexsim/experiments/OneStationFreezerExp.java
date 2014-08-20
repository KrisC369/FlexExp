package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.events.SimEventFactory;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.Tabbable;
import be.kuleuven.cs.flexsim.view.TabbedUI;

import com.google.common.collect.Lists;

public class OneStationFreezerExp {

    public static void main(String[] args) {
        List<OneStationFreezerExp> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();

        int numberOfVariations = 2;
        for (int i = 0; i < numberOfVariations; i++) {
            apps.add(new OneStationFreezerExp());
            apps.get(i).addGrapher(agg1, new Grapher.BufferLevelGrapher());
            apps.get(i).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
            apps.get(i).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
            apps.get(i).addGrapher(agg4, new Grapher.TotalProfitGrapher());

            apps.get(i).init();
            apps.get(i).configureCurtailable(i);
            apps.get(i).start();
            apps.get(i).post();
        }
        drawUI(agg1, agg2, agg3, agg4);

        agg3.print();
        agg4.print();
    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private ProductionLine p;
    private List<Grapher> graphs;
    private FinanceTrackerImpl ft;

    public OneStationFreezerExp() {
        s = Simulator.createSimulator(4000);
        // p = ProductionLine.createStaticCurtailableLayout();
        p = buildLine();
        ft = FinanceTrackerImpl.createDefault(p);
        graphs = new ArrayList<>();
    }

    private ProductionLine buildLine() {
        return new ProductionLine.ProductionLineBuilder()
                .setRfHighConsumption(4500).setRfLowConsumption(300)
                .setRfWidth(100).addRFSteerableStation(1, 50).build();
    }

    public void configureCurtailable(final int numberOfCurtInstances) {
        SimEventFactory fac = new SimEventFactory(s, p);
        // fac.controlStationFavorSpeed(1, numberOfCurtInstances);
        fac.setHigh(200);
        if (numberOfCurtInstances > 0) {
            fac.setLow(400);
            fac.setHigh(600);
            fac.setLow(800);
            fac.setHigh(1000);
            fac.setLow(1200);
            fac.setHigh(1400);
        }
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    public void init() {
        s.register(p);
        s.register(ft);
        p.deliverResources(ResourceFactory.createBulkMPResource(100000, 80));
    }

    public void start() {
        s.start();
    }

    public void post() {
    }
}
