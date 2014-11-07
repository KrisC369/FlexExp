package be.kuleuven.cs.flexsim.example;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregationStrategyImpl;
import be.kuleuven.cs.flexsim.domain.aggregation.IndependentAggregator;
import be.kuleuven.cs.flexsim.domain.energy.generation.ConstantOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.generation.EnergyProductionTrackable;
import be.kuleuven.cs.flexsim.domain.energy.generation.WeighedNormalRandomOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.tso.CopperplateTSO;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine.ProductionLineBuilder;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteImpl;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.BalanceDurationGrapher;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.TSOSteersignalGrapher;
import be.kuleuven.cs.flexsim.view.Tabbable;
import be.kuleuven.cs.flexsim.view.TabbedUI;

import com.google.common.collect.Lists;

/**
 * Example template of how to use the FlexSim project to create simulations.
 * 
 * @author Kristof Coninx (kristof.coninx AT cs.kuleuven.be)
 *
 */
public class AnotherCopyOfCopyOfScratchpadExp {
    private static final int AGGSTEPS = 15;

    public static void main(String[] args) {
        List<AnotherCopyOfCopyOfScratchpadExp> app = Lists.newArrayList();
        app.add(new AnotherCopyOfCopyOfScratchpadExp());
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();
        GraphAggregatorView agg5 = new GraphAggregatorView();
        GraphAggregatorView agg6 = new GraphAggregatorView();

        app.get(0).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        app.get(0).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        app.get(0).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        app.get(0).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        app.get(0).init(true);
        // Attach steersignal grapher before starting sim.
        agg5.addGrapher(new TSOSteersignalGrapher(app.get(0).tso, AGGSTEPS));
        agg6.addGrapher(new BalanceDurationGrapher(app.get(0).tso, AGGSTEPS));
        // tsot = new TSOSteersignalGrapher(app.tso);
        app.get(0).start();
        drawUI(agg1, agg2, agg3, agg4, agg5, agg6);

        agg3.print();
        agg4.print();

    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private List<ProductionLine> p;
    private List<Site> sites;
    private IndependentAggregator agg;
    private CopperplateTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;

    public AnotherCopyOfCopyOfScratchpadExp() {
        s = Simulator.createSimulator(1000);
        p = Lists.newArrayList();
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    public void init(boolean withagg) {
        // Add productio lines
        int simSteps = 1500;
        ProductionLine line1 = new ProductionLineBuilder()
                .setWorkingConsumption(500).setIdleConsumption(20)
                .addConsuming(3).addCurtailableShifted(6)
                .addCurtailableShifted(4).addConsuming(3).build();
        ProductionLine line2 = new ProductionLineBuilder()
                .setWorkingConsumption(400).setIdleConsumption(60)
                .addConsuming(3).addCurtailableShifted(6)
                .addCurtailableShifted(3).addConsuming(3).build();
        ProductionLine line3 = new ProductionLineBuilder()
                .setWorkingConsumption(600).setIdleConsumption(10)
                .addConsuming(3).addCurtailableShifted(4)
                .addCurtailableShifted(4).addConsuming(3).build();
        ProductionLine line4 = new ProductionLineBuilder()
                .setWorkingConsumption(500).setIdleConsumption(15)
                .addConsuming(4).addCurtailableShifted(4)
                .addCurtailableShifted(5).addConsuming(3).build();

        line1.deliverResources(ResourceFactory.createBulkMPResource(3000, 3, 3,
                3, 3));
        line2.deliverResources(ResourceFactory.createBulkMPResource(3000, 3, 3,
                3, 3));
        line3.deliverResources(ResourceFactory.createBulkMPResource(3000, 3, 3,
                3, 3));
        line4.deliverResources(ResourceFactory.createBulkMPResource(3000, 3, 3,
                3, 3));

        ft.add(FinanceTrackerImpl.createDefault(line1));
        ft.add(FinanceTrackerImpl.createDefault(line2));
        ft.add(FinanceTrackerImpl.createDefault(line3));
        ft.add(FinanceTrackerImpl.createDefault(line4));

        sites.add(new SiteImpl(line1, line2));
        sites.add(new SiteImpl(line3, line4));
        EnergyProductionTrackable p1 = new WeighedNormalRandomOutputGenerator(
                -1000, 1000);
        EnergyProductionTrackable p2 = new ConstantOutputGenerator(20000);
        tso = new CopperplateTSO(sites.get(0), sites.get(1));
        tso.registerProducer(p1);
        tso.registerProducer(p2);
        agg = new IndependentAggregator(tso, 1,
                AggregationStrategyImpl.CARTESIANPRODUCT);
        agg.registerClient(sites.get(0));
        agg.registerClient(sites.get(1));

        s.register(tso);
        s.register(agg);
        s.register(sites.get(0));
        s.register(sites.get(1));
        s.register(ft.get(0));
        s.register(ft.get(1));
        s.register(ft.get(2));
        s.register(ft.get(3));
    }

    public void start() {
        s.start();
    }
}
