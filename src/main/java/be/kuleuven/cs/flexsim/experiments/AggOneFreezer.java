package be.kuleuven.cs.flexsim.experiments;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.flexsim.domain.aggregation.AggregatorImpl;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTracker;
import be.kuleuven.cs.flexsim.domain.finance.FinanceTrackerImpl;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.domain.process.ProductionLine.ProductionLineBuilder;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.domain.site.Site;
import be.kuleuven.cs.flexsim.domain.site.SiteImpl;
import be.kuleuven.cs.flexsim.domain.tso.CopperPlateTSO;
import be.kuleuven.cs.flexsim.domain.tso.RandomTSO;
import be.kuleuven.cs.flexsim.domain.tso.SteeringSignal;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.Chartable;
import be.kuleuven.cs.flexsim.view.GraphAggregatorView;
import be.kuleuven.cs.flexsim.view.Grapher;
import be.kuleuven.cs.flexsim.view.ProcessLayout;
import be.kuleuven.cs.flexsim.view.TSOSteersignalGrapher;
import be.kuleuven.cs.flexsim.view.Tabbable;
import be.kuleuven.cs.flexsim.view.TabbedUI;

import com.google.common.collect.Lists;

public class AggOneFreezer {

    public static void main(String[] args) {
        List<AggOneFreezer> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();
        GraphAggregatorView agg5 = new GraphAggregatorView();

        apps.add(new AggOneFreezer(true));
        apps.get(0).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        apps.get(0).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        apps.get(0).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        apps.get(0).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        apps.add(new AggOneFreezer(false));
        apps.get(1).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        apps.get(1).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        apps.get(1).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        apps.get(1).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        Tabbable tsot = agg1;
        for (AggOneFreezer app : apps) {
            app.init();
            app.start();
            app.post();
        }
        agg5.addGrapher(apps.get(0).tsot);
        agg5.addGrapher(apps.get(1).tsot);
        drawUI(agg1, agg2, agg3, agg4,
                new ProcessLayout(apps.get(0).pls.get(0)), agg5);

        agg3.print();
        agg4.print();

    }

    private static void drawUI(Tabbable... agg1) {
        new TabbedUI(Lists.newArrayList(agg1)).draw();

    }

    private Simulator s;
    private AggregatorImpl agg;
    private List<Site> sites;
    private List<ProductionLine> pls;
    private List<FinanceTracker> fts;
    private List<Grapher> graphs;
    private boolean curtail;
    private CopperPlateTSO tso;

    private Chartable tsot;

    public AggOneFreezer(boolean curtail) {
        this.curtail = curtail;
        s = Simulator.createSimulator(3200);
        graphs = new ArrayList<>();
        pls = Lists.newArrayList();
        fts = Lists.newArrayList();
        sites = Lists.newArrayList();
    }

    public void addGrapher(GraphAggregatorView aggv, Grapher g) {
        s.register(g);
        graphs.add(g);
        aggv.addGrapher(g);
    }

    public void init() {

        ProductionLine line1 = new ProductionLineBuilder()
                // 8000-1600
                .setWorkingConsumption(500).setIdleConsumption(100)
                .setRfHighConsumption(800).setRfLowConsumption(400)
                .setRfWidth(1)
                // .addConsuming(3).addCurtailableShifted(6)
                // .addCurtailableShifted(4).addConsuming(3)
                .addRFSteerableStation(2, 50).build();

        line1.deliverResources(ResourceFactory.createBulkMPResource(6000, 50));

        Site site1 = new SiteImpl(line1);
        FinanceTrackerImpl t3 = FinanceTrackerImpl.createDefault(site1);

        SteeringSignal ss;
        if (curtail) {
            ss = new RandomTSO(-20, 20, s.getRandom());
            tso = new CopperPlateTSO(1200, ss, site1);
        } else {
            ss = new RandomTSO(-0, 1, s.getRandom());
            tso = new CopperPlateTSO(1200, ss, site1);
        }
        agg = new AggregatorImpl(tso, 15);
        agg.registerClient(site1);
        tsot = new TSOSteersignalGrapher(tso);

        s.register(agg);
        s.register(tso);
        s.register(t3);

        pls.add(line1);
        fts.add(t3);
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
