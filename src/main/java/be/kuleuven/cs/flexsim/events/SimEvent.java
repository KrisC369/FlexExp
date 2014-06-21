package be.kuleuven.cs.flexsim.events;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import be.kuleuven.cs.flexsim.domain.factory.ProductionLine;
import be.kuleuven.cs.flexsim.simulation.SimulationComponent;
import be.kuleuven.cs.flexsim.simulation.SimulationContext;
import be.kuleuven.cs.flexsim.simulation.Simulator;

/**
 * Simulation events allowing some events to be triggered at specified
 * timesteps.
 * 
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 * 
 */
public abstract class SimEvent {

    private final Simulator sim;
    private final int trigger;
    private final ProductionLine p;

    protected SimEvent(Simulator s, ProductionLine pline, int timetick) {
        this.sim = s;
        this.trigger = timetick;
        this.p = pline;
    }

    void register() {
        sim.register(new SimulationComponent() {
            int ticks = 0;

            @Override
            public void initialize(SimulationContext context) {
            }

            @Override
            public void tick(int t) {
                ticks++;
            }

            @Override
            public void afterTick(int t) {
                if (ticks == trigger) {
                    doEventAction();
                }
            }

            @Override
            public @Nonnull
            List<SimulationComponent> getSimulationSubComponents() {
                return Collections.emptyList();
            }
        });
    }

    protected abstract void doEventAction();

    /**
     * @return the production line.
     */
    protected final ProductionLine getPline() {
        return p;
    }

    static final class ControlStation extends SimEvent {

        private final int consShift;
        private final int prodShift;
        private final boolean favorSpeed;

        ControlStation(Simulator s, ProductionLine pline, int timetick,
                int consShift, int prodShift, boolean favorSpeed) {
            super(s, pline, timetick);
            this.consShift = consShift;
            this.prodShift = prodShift;
            this.favorSpeed = favorSpeed;
        }

        @Override
        protected void doEventAction() {
            if (favorSpeed) {
                getPline().getSteerableStations().get(0)
                        .favorSpeedOverFixedEConsumption(consShift, prodShift);

            } else {
                getPline().getSteerableStations().get(0)
                        .favorFixedEConsumptionOverSpeed(consShift, prodShift);
            }
        }
    }

    static final class StationFullCurtail extends SimEvent {

        private final int numberOfCurtInstances;

        StationFullCurtail(Simulator s, ProductionLine pline, int timetick,
                int instances) {
            super(s, pline, timetick);
            this.numberOfCurtInstances = instances;
        }

        @Override
        protected void doEventAction() {
            if (numberOfCurtInstances > getPline().getCurtailableStations()
                    .size())
                throw new IllegalArgumentException(
                        "Cannot curtail more stations than the amount that allows curtailment.");
            for (int i = 0; i < numberOfCurtInstances; i++) {
                getPline().getCurtailableStations().get(i).doFullCurtailment();
            }
        }
    }

    static final class StationRestore extends SimEvent {

        private final int numberOfCurtInstances;

        StationRestore(Simulator s, ProductionLine pline, int timetick,
                int instances) {
            super(s, pline, timetick);
            this.numberOfCurtInstances = instances;
        }

        @Override
        protected void doEventAction() {
            if (numberOfCurtInstances > getPline().getCurtailableStations()
                    .size())
                throw new IllegalArgumentException(
                        "Cannot Restore more stations than the amount that allows curtailment.");
            for (int i = 0; i < numberOfCurtInstances; i++) {
                getPline().getCurtailableStations().get(i).restore();
            }
        }
    }
}
