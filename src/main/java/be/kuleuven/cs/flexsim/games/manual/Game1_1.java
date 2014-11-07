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
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;

import com.google.common.collect.Lists;

/**
 * Example template of how to use the FlexSim project to create simulations.
 * 
 * @author Kristof Coninx (kristof.coninx AT cs.kuleuven.be)
 *
 */
public class Game1_1 implements GameInstance {
    private static final int AGGSTEPS = 1;

    private Simulator s;
    private List<Site> sites;
    private ReactiveMechanismAggregator agg1;
    private ReactiveMechanismAggregator agg2;
    private BalancingTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;

    public Game1_1(int seed) {
        s = Simulator.createSimulator(1000, seed);
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
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

        sites.add(new SiteSimulation(800, 500, 1000, 6));
        sites.add(new SiteSimulation(800, 500, 1000, 6));

        // Deliver resources to these lines.
        // Add finance trackers keeping track of profit and consumptions.
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker((sites.get(0)), 30000));
        ft.add((FinanceTrackerImpl) FinanceTrackerImpl
                .createBalancingFeeTracker(sites.get(1), 30000));
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        EnergyProductionTrackable p1 = new ConstantOutputGenerator(1600);
        EnergyProductionTrackable p2 = new WeighedNormalRandomOutputGenerator(
                -1500, 1500, 0.010);
        tso = new BalancingTSO(sites.toArray(new Site[2]));
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
        // for (Site s : sites) {
        agg1.registerClient(sites.get(0));
        // }
        agg2.registerClient(sites.get(1));
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
