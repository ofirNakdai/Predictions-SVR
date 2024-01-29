package engine.rule;

import engine.action.api.Action;
import engine.entity.manager.api.EntityInstanceManager;
import engine.environment.active.ActiveEnvironmentVariables;

import java.util.Collection;

public interface Rule {
    String getName();
    boolean isActive(int currTick);
    int getTicksForActivations();
    double getProbForActivations();
    Collection<Action> getActions();
    void addAction(Action actionToAdd);
    void runRule(EntityInstanceManager manager, ActiveEnvironmentVariables activeEnvironmentVariables, int currentTick);

}
