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
public class GameX_X implements GameInstance {
    private static final int AGGSTEPS = 1;

    public static void main(String[] args) {
        List<GameX_X> app = Lists.newArrayList();
        app.add(new GameX_X(1200, 3, 1));
        app.add(new GameX_X(1200, 1, 3));
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
    private ReactiveMechanismAggregator agg1;
    private ReactiveMechanismAggregator agg2;
    private BalancingTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;
    private final int n1, n2;
    private final int current = 800, min = 500, max = 1000;

    public GameX_X(int seed, int n1, int n2) {
        s = Simulator.createSimulator(1000, seed);
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
        this.n1 = n1;
        this.n2 = n2;
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    private int getNumberOfAgents() {
        return n1 + n2;
    }

    @Override
    public void init() {
        // Add productio lines
        // Build sites containing production lines.
        for (int i = 0; i < getNumberOfAgents(); i++) {
            sites.add(SiteSimulation.createDefault(current, min, max, 12));
        }
        // Deliver resources to these lines.
        // Add finance trackers keeping track of profit and consumptions.
        for (int i = 0; i < getNumberOfAgents(); i++) {
            ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                    .createBalancingFeeTracker((sites.get(i)), 30000));
        }
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        EnergyProductionTrackable p1 = new ConstantOutputGenerator(current
                * getNumberOfAgents());
        EnergyProductionTrackable p2 = new WeighedNormalRandomOutputGenerator(
                -1500, 1500, 0.010);
        tso = new BalancingTSO(sites.toArray(new Site[getNumberOfAgents()]));
        // tso = new CopperplateTSO();
        tso.registerProducer(p1);
        tso.registerProducer(p2);
        // tso = new SimpleTSO(29000, new RandomTSO(-2, 2, s.getRandom()),
        // sites.toArray(new Site[4]));
        this.agg1 = new ReactiveMechanismAggregator(tso,
                AggregationStrategyImpl.CARTESIANPRODUCT);
        this.agg2 = new ReactiveMechanismAggregator(tso,
                AggregationStrategyImpl.MOVINGHORIZON);
        // Register the tso (with subsimcompoments recursively added. And add
        // the aggregator and finance trackers.
        s.register(tso);
        s.register(this.agg1);
        s.register(this.agg2);
        for (FinanceTrackerImpl f : ft) {
            s.register(f);
        }
        // Register the sites to the aggregator as clients
        for (int i = 0; i < n1; i++) {
            agg1.registerClient(sites.get(i));
        }
        for (int i = 0; i < n2; i++) {
            agg2.registerClient(sites.get(n1 + i));
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
