package be.kuleuven.cs.flexsim.example;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregatorImpl;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine.ProductionLineBuilder;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteImpl;
import be.kuleuven.cs.flexsim.domain.tso.CopperPlateTSO;
import be.kuleuven.cs.flexsim.domain.tso.RandomTSO;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.ProcessLayoutView;
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
public class ExampleScenario {

    public static void main(String[] args) {
        ExampleScenario app = new ExampleScenario();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();

        app.addGrapher(agg1, new Grapher.BufferLevelGrapher());
        app.addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        app.addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        app.addGrapher(agg4, new Grapher.TotalProfitGrapher());

        app.init();
        // Attach steersignal grapher before starting sim.
        TSOSteersignalGrapher tsot = new TSOSteersignalGrapher(app.tso);
        app.start();
        // tsot = new TSOSteersignalGrapher(app.tso);
        drawUI(agg1, agg2, agg3, agg4, tsot,
                new ProcessLayoutView(app.p.get(0)),
                new SystemLayoutView(app.s));

        agg3.print();
        agg4.print();

    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private List<ProductionLine> p;
    private List<Site> sites;
    private AggregatorImpl agg;
    private CopperPlateTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;

    public ExampleScenario() {
        s = Simulator.createSimulator(4000);
        p = Lists.newArrayList();
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
    }

    private List<ProductionLine> buildLines() {
        List<ProductionLine> toret = Lists.newArrayList();
        toret.add(new ProductionLineBuilder()
                // 8000-1600
                .setWorkingConsumption(350).setIdleConsumption(75)
                .setRfHighConsumption(600).setRfLowConsumption(300)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .addRFSteerableStation(1, 30).build());
        toret.add(new ProductionLineBuilder()
                // 4800-2520
                .setWorkingConsumption(320).setIdleConsumption(190)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .build());
        toret.add(new ProductionLineBuilder()
                // 8400-1400
                .setWorkingConsumption(400).setIdleConsumption(100)
                .addConsuming(3).addCurtailableShifted(4).addConsuming(3)
                .build());
        toret.add(new ProductionLineBuilder()
                // 8000-2400
                .setWorkingConsumption(450).setIdleConsumption(150)
                .setRfHighConsumption(500).setRfLowConsumption(250)
                .addConsuming(4).addCurtailableShifted(4).addConsuming(3)
                .addRFSteerableStation(1, 30).build());
        return toret;
    }

    public void addGrapher(GraphAggregatorView agg, Grapher g) {
        s.register(g);
        graphs.add(g);
        agg.addGrapher(g);
    }

    public void init() {
        // Add productio lines
        p.addAll(buildLines());
        // Build sites containing production lines.
        sites.add(new SiteImpl(p.get(0), p.get(1)));
        sites.add(new SiteImpl(p.get(2), p.get(3)));
        // Deliver resources to these lines.
        for (ProductionLine pl : p) {
            pl.deliverResources(ResourceFactory.createBulkMPResource(3000, 4,
                    3, 4, 40));
        }
        // Add finance trackers keeping track of profit and consumptions.
        ft.add(FinanceTrackerImpl.createDefault(sites.get(0)));
        ft.add(FinanceTrackerImpl.createDefault(sites.get(1)));
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        tso = new CopperPlateTSO(14000, new RandomTSO(-1500, 1500,
                s.getRandom()), sites.toArray(new Site[2]));
        this.agg = new AggregatorImpl(tso, 15);

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

    public void start() {
        s.start();
    }
}
