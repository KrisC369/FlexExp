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
import be.kuleuven.cs.flexsim.view.ProcessLayoutView;
import be.kuleuven.cs.flexsim.view.SystemLayoutView;
import be.kuleuven.cs.flexsim.view.TSOSteersignalGrapher;
import be.kuleuven.cs.flexsim.view.Tabbable;
import be.kuleuven.cs.flexsim.view.TabbedUI;

import com.google.common.collect.Lists;

public class CopyOfCPTSO_DiffGenWAgg3Sites {

    public static void main(String[] args) {
        List<CopyOfCPTSO_DiffGenWAgg3Sites> apps = new ArrayList<>();
        GraphAggregatorView agg1 = new GraphAggregatorView();
        GraphAggregatorView agg2 = new GraphAggregatorView();
        GraphAggregatorView agg3 = new GraphAggregatorView();
        GraphAggregatorView agg4 = new GraphAggregatorView();
        GraphAggregatorView agg5 = new GraphAggregatorView();

        // int numberOfVariations = 8;
        // for (int i = 0; i < numberOfVariations; i++) {
        apps.add(new CopyOfCPTSO_DiffGenWAgg3Sites(true));
        apps.get(0).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        apps.get(0).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        apps.get(0).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        apps.get(0).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        apps.add(new CopyOfCPTSO_DiffGenWAgg3Sites(false));
        apps.get(1).addGrapher(agg1, new Grapher.BufferLevelGrapher());
        apps.get(1).addGrapher(agg2, new Grapher.StepConsumptionGrapher());
        apps.get(1).addGrapher(agg3, new Grapher.TotalComsumptionGrapher());
        apps.get(1).addGrapher(agg4, new Grapher.TotalProfitGrapher());

        Tabbable tsot = agg1;
        for (CopyOfCPTSO_DiffGenWAgg3Sites app : apps) {
            app.init();
            app.start();
            app.post();
        }
        agg5.addGrapher(apps.get(0).tsot);
        agg5.addGrapher(apps.get(1).tsot);
        SystemLayoutView v = new SystemLayoutView(apps.get(0).s);
        drawUI(agg1, agg2, agg3, agg4,
                new ProcessLayoutView(apps.get(0).pls.get(0)), agg5, v);

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

    public CopyOfCPTSO_DiffGenWAgg3Sites(boolean curtail) {
        this.curtail = curtail;
        s = Simulator.createSimulator(5200);
        // p = ProductionLine.createStaticCurtailableLayout();
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
                .addConsuming(3).addCurtailableShifted(6)
                .addCurtailableShifted(4).addConsuming(3)
                .addRFSteerableStation(1, 30).build();
        ProductionLine line2 = new ProductionLineBuilder()
                // 4800-2520
                .setWorkingConsumption(400).setIdleConsumption(210)
                .addConsuming(3).addCurtailableShifted(6)
                .addCurtailableShifted(3).addConsuming(3).build();
        ProductionLine line3 = new ProductionLineBuilder()
                // 8400-1400
                .setWorkingConsumption(600).setIdleConsumption(100)
                .addConsuming(3).addCurtailableShifted(4)
                .addCurtailableShifted(4).addConsuming(3).build();
        ProductionLine line4 = new ProductionLineBuilder()
                // 8000-2400
                .setWorkingConsumption(500).setIdleConsumption(150)
                .setRfHighConsumption(900).setRfLowConsumption(450)
                .addConsuming(4).addCurtailableShifted(4)
                .addCurtailableShifted(5).addConsuming(3)
                .addRFSteerableStation(1, 30).build();

        ProductionLine line5 = new ProductionLineBuilder()
                // 8400-1400
                .setWorkingConsumption(600).setIdleConsumption(100)
                .addConsuming(3).addCurtailableShifted(4)
                .addCurtailableShifted(4).addConsuming(3).build();
        ProductionLine line6 = new ProductionLineBuilder()
                // 8000-2400
                .setWorkingConsumption(500).setIdleConsumption(150)
                .setRfHighConsumption(900).setRfLowConsumption(450)
                .addConsuming(4).addCurtailableShifted(4)
                .addCurtailableShifted(5).addConsuming(3)
                .addRFSteerableStation(1, 30).build();

        line1.deliverResources(ResourceFactory.createBulkMPResource(3000, 4, 4,
                4, 4, 40));
        line2.deliverResources(ResourceFactory.createBulkMPResource(3000, 3, 3,
                3, 3));
        line3.deliverResources(ResourceFactory.createBulkMPResource(3000, 5, 5,
                5, 5));
        line4.deliverResources(ResourceFactory.createBulkMPResource(3000, 4, 3,
                3, 4, 40));
        line5.deliverResources(ResourceFactory.createBulkMPResource(3000, 5, 5,
                5, 5));
        line6.deliverResources(ResourceFactory.createBulkMPResource(3000, 4, 3,
                3, 4, 40));

        Site site1 = new SiteImpl(line1, line2);
        Site site2 = new SiteImpl(line3, line4);
        Site site3 = new SiteImpl(line5, line6);
        FinanceTrackerImpl t3 = FinanceTrackerImpl.createDefault(site1);
        FinanceTrackerImpl t4 = FinanceTrackerImpl.createDefault(site2);
        FinanceTrackerImpl t5 = FinanceTrackerImpl.createDefault(site3);
        SteeringSignal ss;
        ss = new RandomTSO(-700, 300, s.getRandom());
        if (curtail) {
            tso = new CopperPlateTSO(17000, ss, site1, site2, site3);
        } else {
            tso = new CopperPlateTSO(25000, ss, site1, site2, site3);
        }
        agg = new AggregatorImpl(tso, 15);
        agg.registerClient(site1);
        agg.registerClient(site2);
        agg.registerClient(site3);
        tsot = new TSOSteersignalGrapher(tso);

        s.register(agg);
        s.register(tso);
        s.register(site2);
        s.register(site2);
        s.register(site3);
        // s.register(t1);
        // s.register(t2);
        s.register(t3);
        s.register(t4);
        s.register(t5);

        pls.add(line1);
        pls.add(line2);
        pls.add(line3);
        pls.add(line4);

        // fts.add(t1);
        // fts.add(t2);
        fts.add(t3);
        fts.add(t4);
        sites.add(site2);
        sites.add(site2);
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
