package engine.rule;

import engine.action.api.Action;
import engine.context.Context;
import engine.context.ContextImpl;
import engine.entity.EntityInstance;
import engine.entity.manager.api.EntityInstanceManager;
import engine.environment.active.ActiveEnvironmentVariables;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RuleImpl implements Rule, Serializable {
    private final String name;
    private int howManyTicksForActivation = 1;
    private double probabilityForActivation = 1;
    private final Collection<Action> actions = new ArrayList<>();

    public RuleImpl(String name, Integer howManyTicksForActivation, Double probabilityForActivation) {
        this.name = name;
        if (howManyTicksForActivation != null) {
            this.howManyTicksForActivation = howManyTicksForActivation;
        }
        if (probabilityForActivation != null) {
            this.probabilityForActivation = probabilityForActivation;
        }
    }
    public RuleImpl(Rule ruleToCopy){
        this(ruleToCopy.getName(),ruleToCopy.getTicksForActivations(),ruleToCopy.getProbForActivations());
        this.actions.addAll(ruleToCopy.getActions());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isActive(int currTick) {
        if(currTick % howManyTicksForActivation == 0){
            Random random = new Random();
            return random.nextDouble() < this.probabilityForActivation;
        }
        return false;
    }

    @Override
    public int getTicksForActivations() {
        return this.howManyTicksForActivation;
    }

    @Override
    public double getProbForActivations() {
        return this.probabilityForActivation;
    }

    @Override
    public Collection<Action> getActions() {
        return this.actions;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    @Override
    public void runRule(EntityInstanceManager manager, ActiveEnvironmentVariables activeEnvironmentVariables, int currentTick)
    {
        for (Action action: actions) {
            for (EntityInstance entityInstance : manager.getInstancesListByName(action.getMainEntityName()))
            {
//                Context context = new ContextImpl(entityInstance, manager, activeEnvironmentVariables, currentTick);
                Context context = new ContextImpl(entityInstance,entityInstance, manager, activeEnvironmentVariables, currentTick);
                try {
                    action.Run(context);
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + "\n" +"Error occurred with entity: " +
                                                            entityInstance.getName() +
                                                            ", in rule: " + this.name);
                }
            }

            manager.killEntities();
            manager.createScratchEntities();
            manager.createDerivedEntities();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RuleImpl:\n");
        stringBuilder.append("name='").append(name).append("'\n");
        stringBuilder.append("howManyTicksForActivation=").append(howManyTicksForActivation).append("\n");
        stringBuilder.append("probabilityForActivation=").append(probabilityForActivation).append("\n");
        stringBuilder.append("number of actions=").append(actions.size()).append("\n");

        if(!actions.isEmpty()) {
            stringBuilder.append("actions names:").append("\n");
            for (Action action : actions) {
                stringBuilder.append(action.getClass().getSimpleName()).append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
