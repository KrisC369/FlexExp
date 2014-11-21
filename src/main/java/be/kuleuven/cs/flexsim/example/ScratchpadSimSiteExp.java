package be.kuleuven.cs.flexsim.example;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregationStrategyImpl;
import be.kuleuven.cs.flexsim.domain.aggregation.ReactiveMechanismAggregator;
import be.kuleuven.cs.flexsim.domain.energy.generation.ConstantOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.generation.EnergyProductionTrackable;
import be.kuleuven.cs.flexsim.domain.energy.generation.WeighedNormalRandomOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.tso.BalancingTSO;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteSimulation;
import be.kuleuven.cs.flexsim.io.CSVWriter;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.BalanceDurationGrapher;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.SystemLayoutView;
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
public class ScratchpadSimSiteExp {
    private static final int AGGSTEPS = 1;

    public static void main(String[] args) {
        List<ScratchpadSimSiteExp> app = Lists.newArrayList();
        app.add(new ScratchpadSimSiteExp());
        app.add(new ScratchpadSimSiteExp());
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

        app.get(1).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        app.get(1).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        app.get(1).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        app.get(1).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        app.get(0).init(true);
        app.get(1).init(false);
        // Attach steersignal grapher before starting sim.
        agg5.addGrapher(new TSOSteersignalGrapher(app.get(0).tso, AGGSTEPS));
        agg5.addGrapher(new TSOSteersignalGrapher(app.get(1).tso, AGGSTEPS));
        agg6.addGrapher(new BalanceDurationGrapher(app.get(0).tso, AGGSTEPS));
        agg6.addGrapher(new BalanceDurationGrapher(app.get(1).tso, AGGSTEPS));
        // tsot = new TSOSteersignalGrapher(app.tso);
        app.get(0).start();
        app.get(1).start();
        drawUI(agg1, agg2, agg3, agg4, agg5, agg6,
                new SystemLayoutView(app.get(0).s));

        agg3.print();
        agg4.print();
        new CSVWriter("ExpBal1.csv").writeCSV(agg5.getChartable().get(0));
        new CSVWriter("ExpBal2.csv").writeCSV(agg5.getChartable().get(1));
        new CSVWriter("ExpBalDuration.csv")
                .writeCSV(agg6.getChartable().get(0));
        new CSVWriter("ExpBalDuration2.csv").writeCSV(agg6.getChartable()
                .get(1));
    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private List<ProductionLine> p;
    private List<Site> sites;
    private ReactiveMechanismAggregator agg;
    private BalancingTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;

    public ScratchpadSimSiteExp() {
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
        // Build sites containing production lines.

        sites.add(SiteSimulation.createDefault(7000, 3000, 10000, 6));
        sites.add(SiteSimulation.createDefault(3000, 3000, 5000, 6));
        sites.add(SiteSimulation.createDefault(800, 700, 1000, 5));
        sites.add(SiteSimulation.createDefault(800, 500, 1000, 6));
        sites.add(SiteSimulation.createDefault(1500, 200, 2000, 3));
        sites.add(SiteSimulation.createDefault(800, 500, 1000, 6));

        // Deliver resources to these lines.
        // Add finance trackers keeping track of profit and consumptions.
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(0), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(1), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(2), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(3), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(4), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(5), 30000));
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        EnergyProductionTrackable p1 = new ConstantOutputGenerator(12100);
        EnergyProductionTrackable p2 = new WeighedNormalRandomOutputGenerator(
                -50000, 50000, 0.010);
        tso = new BalancingTSO(sites.toArray(new Site[6]));
        // tso = new CopperplateTSO();
        tso.registerProducer(p1);
        tso.registerProducer(p2);
        // tso = new SimpleTSO(29000, new RandomTSO(-2, 2, s.getRandom()),
        // sites.toArray(new Site[4]));
        this.agg = new ReactiveMechanismAggregator(tso,
                AggregationStrategyImpl.MOVINGHORIZON);

        // Register the tso (with subsimcompoments recursively added. And add
        // the aggregator and finance trackers.
        s.register(tso);
        s.register(this.agg);
        for (FinanceTrackerImpl f : ft) {
            s.register(f);
        }
        // Register the sites to the aggregator as clients
        if (withagg) {
            for (Site s : sites) {
                agg.registerClient(s);
            }
        }
    }

    public void start() {
        s.start();
    }
}
