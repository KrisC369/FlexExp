package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregatorImpl;
import be.kuleuven.cs.flexsim.domain.energy.generation.ConstantOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.generation.EnergyProductionTrackable;
import be.kuleuven.cs.flexsim.domain.energy.generation.RandomOutputGenerator;
import be.kuleuven.cs.flexsim.domain.energy.tso.CopperplateTSO;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine.ProductionLineBuilder;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteImpl;
import be.kuleuven.cs.flexsim.io.CSVWriter;
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
public class AggVsNoAggBalancingExp {

    public static void main(String[] args) {
        List<AggVsNoAggBalancingExp> app = Lists.newArrayList();
        app.add(new AggVsNoAggBalancingExp());
        app.add(new AggVsNoAggBalancingExp());
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();

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
        TSOSteersignalGrapher tsot = new TSOSteersignalGrapher(app.get(0).tso);
        TSOSteersignalGrapher tsot2 = new TSOSteersignalGrapher(app.get(1).tso);
        // tsot = new TSOSteersignalGrapher(app.tso);
        app.get(0).start();
        app.get(1).start();
        drawUI(agg1, agg2, agg3, agg4, tsot, tsot2,
                new ProcessLayoutView(app.get(0).p.get(0)),
                new SystemLayoutView(app.get(0).s));

        agg3.print();
        agg4.print();
        new CSVWriter("ExpBal1.csv").writeCSV(tsot);
        new CSVWriter("ExpBal2.csv").writeCSV(tsot2);

    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();
    }

    private Simulator s;
    private List<ProductionLine> p;
    private List<Site> sites;
    private AggregatorImpl agg;
    private CopperplateTSO tso;
    private List<Grapher> graphs;
    private List<FinanceTrackerImpl> ft;

    public AggVsNoAggBalancingExp() {
        s = Simulator.createSimulator(2700);
        p = Lists.newArrayList();
        sites = Lists.newArrayList();
        graphs = new ArrayList<>();
        ft = Lists.newArrayList();
    }

    private List<ProductionLine> buildLines() {
        List<ProductionLine> toret = Lists.newArrayList();
        toret.add(new ProductionLineBuilder()
                // 8000-1600
                .setWorkingConsumption(350).setIdleConsumption(200)
                .setRfHighConsumption(600).setRfLowConsumption(400)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .addRFSteerableStation(1, 30).build());
        toret.add(new ProductionLineBuilder()
                // 4800-2520
                .setWorkingConsumption(320).setIdleConsumption(190)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .build());
        toret.add(new ProductionLineBuilder()
                // 8400-1400
                .setWorkingConsumption(400).setIdleConsumption(300)
                .addConsuming(3).addCurtailableShifted(4).addConsuming(3)
                .build());
        toret.add(new ProductionLineBuilder()
                // 8000-2400
                .setWorkingConsumption(450).setIdleConsumption(250)
                .setRfHighConsumption(500).setRfLowConsumption(350)
                .addConsuming(4).addCurtailableShifted(4).addConsuming(3)
                .addRFSteerableStation(1, 30).build());

        toret.add(new ProductionLineBuilder()
                // 8000-1600
                .setWorkingConsumption(350).setIdleConsumption(275)
                .setRfHighConsumption(600).setRfLowConsumption(450)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .addRFSteerableStation(1, 30).build());
        toret.add(new ProductionLineBuilder()
                // 4800-2520
                .setWorkingConsumption(320).setIdleConsumption(290)
                .addConsuming(3).addCurtailableShifted(6).addConsuming(3)
                .build());
        toret.add(new ProductionLineBuilder()
                // 8400-1400
                .setWorkingConsumption(400).setIdleConsumption(300)
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

    public void init(boolean withagg) {
        // Add productio lines
        p.addAll(buildLines());
        // Build sites containing production lines.
        sites.add(new SiteImpl(p.get(0), p.get(1)));
        sites.add(new SiteImpl(p.get(2), p.get(3)));
        sites.add(new SiteImpl(p.get(4), p.get(5)));
        sites.add(new SiteImpl(p.get(6), p.get(7)));

        // Deliver resources to these lines.
        int count = 0;
        for (ProductionLine pl : p) {
            if (count++ % 3 == 0) {
                pl.deliverResources(ResourceFactory.createBulkMPResource(2000,
                        3, 5, 3, 42));
            } else {

                pl.deliverResources(ResourceFactory.createBulkMPResource(2000,
                        4, 3, 4, 40));
            }

        }
        // Add finance trackers keeping track of profit and consumptions.
        ft.add(FinanceTrackerImpl.createDefault(sites.get(0)));
        ft.add(FinanceTrackerImpl.createDefault(sites.get(1)));
        ft.add(FinanceTrackerImpl.createDefault(sites.get(2)));
        ft.add(FinanceTrackerImpl.createDefault(sites.get(3)));
        // Add the tso with the random signal for the aggregator and the sites
        // connected to it.
        EnergyProductionTrackable p1 = new ConstantOutputGenerator(29000);
        EnergyProductionTrackable p2 = new RandomOutputGenerator(-20, 15);
        tso = new CopperplateTSO(sites.toArray(new Site[4]));
        tso.registerProducer(p1);
        tso.registerProducer(p2);
        this.agg = new AggregatorImpl(tso, 2);

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
