package desktop.execution.tasks;

import engine.system.SystemEngine;
import engine.world.WorldInstance;
import javafx.concurrent.Task;

public class SimulationTask extends Task<Boolean> {
    private SystemEngine engine;

    public SimulationTask(SystemEngine engine) {
        this.engine = engine;
    }

    @Override
    protected Boolean call() throws Exception {

        this.engine.runSimulation();

        return null;
    }
}
