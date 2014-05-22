package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import be.kuleuven.cs.flexsim.domain.factory.ProductionLine;
import be.kuleuven.cs.flexsim.domain.finances.FinanceTracker;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.simulation.SimulationComponent;
import be.kuleuven.cs.flexsim.simulation.SimulationContext;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;

public class App {

    public static void main(String[] args) {
        List<App> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();

        int numberOfVariations = 8;
        for (int i = 0; i < numberOfVariations; i++) {
            apps.add(new App());
            apps.get(i).addGrapher(agg1, new Grapher.BufferLevelGrapher());
            apps.get(i).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
            apps.get(i).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
            apps.get(i).configureCurtailable(i);
            apps.get(i).init();
            apps.get(i).start();
            apps.get(i).post();
        }
        agg1.draw();
        agg2.draw();
        agg3.draw();
        agg3.print();
    }

    private Simulator s;
    private ProductionLine p;
    private List<Grapher> graphs;
    private FinanceTracker ft;

    public App() {
        s = Simulator.createSimulator(2500);
        // p = ProductionLine.createStaticCurtailableLayout();
        p = buildLine();
        ft = FinanceTracker.createDefault(p);
        graphs = new ArrayList<>();
    }

    private ProductionLine buildLine() {
        return new ProductionLine.ProductionLineBuilder().addShifted(7)
                .addShifted(7).addShifted(4)
                .addMultiCapExponentialConsuming(1, 100).build();
    }

    public void configureCurtailable(final int numberOfCurtInstances) {
        s.register(p);
        s.register(ft);
        s.register(new SimulationComponent() {
            int ticks = 0;
            boolean curt = false;

            @Override
            public void initialize(SimulationContext context) {
            }

            @Override
            public void tick(int t) {
                ticks++;
            }

            @Override
            public void afterTick(int t) {
                if (ticks >= 200 && !curt) {
                    // if (numberOfCurtInstances > p.getCurtailableStations()
                    // .size())
                    // throw new IllegalArgumentException(
                    // "Cannot curtail more stations than the amount that allows curtailment.");
                    // for (int i = 0; i < numberOfCurtInstances; i++) {
                    // p.getCurtailableStations().get(i).doFullCurtailment();
                    // }
                    p.getCurtailableStations()
                            .get(0)
                            .setSpeedVsEConsumptionRatio(
                                    numberOfCurtInstances * 5, true);

                    curt = true;
                }
            }

            @Override
            public @Nonnull
            List<SimulationComponent> getSimulationSubComponents() {
                return Collections.emptyList();
            }
        });
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    public void init() {
        // p.deliverResources(ResourceFactory.createBulkMPResource(60,0, 3, 1));
        // p.deliverResources(ResourceFactory.createBulkMPResource(60,0, 2, 3,
        // 2));
        // p.deliverResources(ResourceFactory.createBulkMPResource(60,0, 1, 2,
        // 3));
        p.deliverResources(ResourceFactory.createBulkMPResource(10000, 0, 2, 2,
                2, 200));
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
