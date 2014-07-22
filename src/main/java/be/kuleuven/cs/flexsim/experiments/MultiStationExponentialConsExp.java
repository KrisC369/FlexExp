package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.finance.FinanceTracker;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.events.SimEventFactory;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;

/**
 * Runs an experiment with multipe stations on a productionline with one curtail
 * (steer) event on a exponentialConsuming steerable station.
 * 
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 * 
 */
public class MultiStationExponentialConsExp {

    public static void main(String[] args) {
        List<MultiStationExponentialConsExp> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();

        int numberOfVariations = 8;
        for (int i = 0; i < numberOfVariations; i++) {
            apps.add(new MultiStationExponentialConsExp());
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
    }

    private Simulator s;
    private ProductionLine p;
    private List<Grapher> graphs;
    private FinanceTracker ft;

    public MultiStationExponentialConsExp() {
        s = Simulator.createSimulator(12000);
        p = buildLine();
        ft = FinanceTracker.createDefault(p);
        graphs = new ArrayList<>();
    }

    private ProductionLine buildLine() {
        return new ProductionLine.ProductionLineBuilder().addShifted(7)
                .addShifted(7).addShifted(4)
                .addMultiCapExponentialConsuming(2, 50)
                .addRFSteerableStation(2, 50).build();
    }

    public void configureCurtailable(final int numberOfCurtInstances) {
        SimEventFactory fac = new SimEventFactory(s, p);
        fac.controlStationFavorSpeed(1000, numberOfCurtInstances);
        fac.setHigh(500);
        fac.setLow(1000);
        fac.setHigh(2500);
        fac.setLow(3800);
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    public void init() {
        s.register(p);
        s.register(ft);
        p.deliverResources(ResourceFactory.createBulkMPResource(10000, 0, 2, 2,
                2, 1000, 100));
    }

    public void start() {
        s.start();
    }

    public void post() {
    }
}
