package be.kuleuven.cs.flexsim.experiments;

import be.kuleuven.cs.flexsim.domain.factory.ProductionLine;
import be.kuleuven.cs.flexsim.domain.resource.ResourceFactory;
import be.kuleuven.cs.flexsim.simulation.Simulator;
import be.kuleuven.cs.flexsim.view.Grapher;


public class App 
{
    public static void main( String[] args )
    {
        //runExtended();
        runCustom();
        
    }

    private static void runCustom() {
        Simulator s = Simulator.createSimulator(120);
        ProductionLine p = ProductionLine.createCustomLayout(6,6,6);
        Grapher g1 = new Grapher.StepConsumptionGrapher();
        Grapher g2 = new Grapher.TotalComsumptionGrapher();
        Grapher g3 = new Grapher.BufferLevelGrapher();
        s.register(p);
        s.register(g1);
        s.register(g2);
        s.register(g3);
        p.deliverResources(ResourceFactory.createBulkMPResource(60, 3,1));
        s.start();
        g1.drawChart();
        g2.drawChart();
        g3.drawChart();        
    }

    private static void runExtended() {
        Simulator s = Simulator.createSimulator(120);
        ProductionLine p = ProductionLine.createCustomLayout(5,4,3,4,5);
        Grapher g1 = new Grapher.StepConsumptionGrapher();
        Grapher g2 = new Grapher.TotalComsumptionGrapher();
        Grapher g3 = new Grapher.BufferLevelGrapher();
        s.register(p);
        s.register(g1);
        s.register(g2);
        s.register(g3);
        p.deliverResources(ResourceFactory.createBulkMPResource(60, 3,1));
        s.start();
        g1.drawChart();
        g2.drawChart();
        g3.drawChart();
    }
}
