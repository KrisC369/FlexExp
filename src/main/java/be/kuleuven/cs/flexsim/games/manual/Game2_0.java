package be.kuleuven.cs.flexsim.games.manual;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregationStrategyImpl;
import be.kuleuven.cs.flexsim.domain.aggregation.ReactiveMechanismAggregator;
import be.kuleuven.cs.flexsim.domain.energy.generation.ConstantOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.generation.EnergyProductionTrackable;
import be.kuleuven.cs.flexsim.domain.energy.generation.WeighedNormalRandomOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.tso.BalancingTSO;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteSimulation;
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
public class Game2_0 implements GameInstance {
    private static final int AGGSTEPS = 1;

    public static void main(String[] args) {
        List<Game2_0> app = Lists.newArrayList();
        app.add(new Game2_0(1200, true, 2));
        app.add(new Game2_0(1200, false, 2));
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

        app.get(0).init();
        app.get(1).init();
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
    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private List<Site> sites;
    private ReactiveMechanismAggregator agg;
    private BalancingTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;
    private boolean cart;
    private int numberOfAgents;
    private final int current = 800, min = 500, max = 1000;

    public Game2_0(int seed, boolean cart, int agentAmount) {
        s = Simulator.createSimulator(1000, seed);
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
        this.cart = cart;
        this.numberOfAgents = agentAmount;
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    @Override
    public void init() {
        // Add productio lines
        // Build sites containing production lines.
        for (int i = 0; i < numberOfAgents; i++) {
            sites.add(SiteSimulation.createDefault(current, min, max, 12));
        }

        // Deliver resources to these lines.
        // Add finance trackers keeping track of profit and consumptions.
        for (int i = 0; i < numberOfAgents; i++) {
            ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                    .createBalancingFeeTracker((sites.get(i)), 30000));
        }
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        EnergyProductionTrackable p1 = new ConstantOutputGenerator(
                numberOfAgents * current);
        EnergyProductionTrackable p2 = new WeighedNormalRandomOutputGenerator(
                -1500, 1500, 0.010);
        tso = new BalancingTSO(sites.toArray(new Site[numberOfAgents]));
        // tso = new CopperplateTSO();
        tso.registerProducer(p1);
        tso.registerProducer(p2);
        // tso = new SimpleTSO(29000, new RandomTSO(-2, 2, s.getRandom()),
        // sites.toArray(new Site[4]));
        if (cart) {
            this.agg = new ReactiveMechanismAggregator(tso,
                    AggregationStrategyImpl.CARTESIANPRODUCT);
        } else {
            this.agg = new ReactiveMechanismAggregator(tso,
                    AggregationStrategyImpl.MOVINGHORIZON);
        }
        // Register the tso (with subsimcompoments recursively added. And add
        // the aggregator and finance trackers.
        s.register(tso);
        s.register(this.agg);
        for (FinanceTrackerImpl f : ft) {
            s.register(f);
        }
        // Register the sites to the aggregator as clients
        for (Site s : sites) {
            agg.registerClient(s);
        }
    }

    @Override
    public void start() {
        s.start();
    }

    @Override
    public long[] getPayOffs() {
        long[] toRet = new long[sites.size()];
        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = (long) ft.get(i).getTotalProfit();
        }
        return toRet;
    }
}
