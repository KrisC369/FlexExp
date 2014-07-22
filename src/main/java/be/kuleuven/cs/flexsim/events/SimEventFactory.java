package be.kuleuven.cs.flexsim.events;

import be.kuleuven.cs.flexsim.domain.process.ProductionLine;
import be.kuleuven.cs.flexsim.simulation.Simulator;

/**
 * Factory for creating events for a simulation instance.
 * 
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 * 
 */
public class SimEventFactory {
    private final Simulator s;
    private final ProductionLine p;
    /**
     * The fixed scaling factor for consumption shift units.
     */
    public final static int CONSUMPTIONSHIFT = 100; // 800 prev.
    /**
     * The fixed scaling factor for production shift units.
     */
    public final static int PRODUCTIONSHIFT = 10;

    /**
     * Default constructor for this factory.
     * 
     * @param s
     *            The simulator to create events for.
     * @param p
     *            The productionline in this simulator to create events for.
     */
    public SimEventFactory(Simulator s, ProductionLine p) {
        this.s = s;
        this.p = p;
    }

    /**
     * Create a station control event favoring speed over a fixed consumption
     * rate.
     * 
     * @param trigger
     *            The timestep to trigger this event at.
     * @param weight
     *            the amount of control to exert.
     * @return the event instance that will be fired.
     */
    public SimEvent controlStationFavorSpeed(int trigger, int weight) {
        SimEvent e = new SimEvent.ControlStation(s, p, trigger, weight
                * CONSUMPTIONSHIFT, weight * PRODUCTIONSHIFT, true);
        e.register();
        return e;
    }

    /**
     * Create a station control event favoring a fixed consumption rate over
     * speed.
     * 
     * @param trigger
     *            The timestep to trigger this event at.
     * @param weight
     *            the amount of control to exert.
     * @return the event instance that will be fired.
     */
    public SimEvent controlStationFavorConsumption(int trigger, int weight) {
        SimEvent e = new SimEvent.ControlStation(s, p, trigger, weight
                * CONSUMPTIONSHIFT, weight * PRODUCTIONSHIFT, false);
        e.register();
        return e;
    }

    /**
     * Create an event that fully curtails a station.
     * 
     * @param trigger
     *            The timestep to trigger this event at.
     * @param instances
     *            the amount of instances to curtail.
     * @return the event instance that will be fired.
     */
    public SimEvent curtailFully(int trigger, int instances) {
        SimEvent e = new SimEvent.StationFullCurtail(s, p, trigger, instances);
        e.register();
        return e;
    }

    /**
     * Create an event that fully curtails a station.
     * 
     * @param trigger
     *            The timestep to trigger this event at.
     * @return the event instance that will be fired.
     */
    public SimEvent setHigh(int trigger) {
        SimEvent e = new SimEvent.StationSteerHighCurtail(s, p, trigger);
        e.register();
        return e;
    }

    /**
     * Create an event that fully curtails a station.
     * 
     * @param trigger
     *            The timestep to trigger this event at.
     * @return the event instance that will be fired.
     */
    public SimEvent setLow(int trigger) {
        SimEvent e = new SimEvent.StationSteerLowCurtail(s, p, trigger);
        e.register();
        return e;
    }
}
