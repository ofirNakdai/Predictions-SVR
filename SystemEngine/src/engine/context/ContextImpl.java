package engine.context;

import engine.entity.EntityInstance;
import engine.entity.manager.api.EntityInstanceManager;
import engine.environment.active.ActiveEnvironmentVariables;
import engine.property.api.PropertyInstance;

import java.io.Serializable;

public class ContextImpl implements Context, Serializable {
    private final EntityInstance primaryEntityInstance;
    private final EntityInstance secondaryEntityInstance;
    private final int currentTick;
    private final EntityInstanceManager entityInstanceManager;
    private final ActiveEnvironmentVariables activeEnvironment;

    public ContextImpl(EntityInstance primaryEntityInstance,EntityInstance secondaryEntityInstance, EntityInstanceManager entityInstanceManager, ActiveEnvironmentVariables activeEnvironment, int currentTick) {
        this.primaryEntityInstance = primaryEntityInstance;
        this.secondaryEntityInstance = secondaryEntityInstance;
        this.entityInstanceManager = entityInstanceManager;
        this.activeEnvironment = activeEnvironment;
        this.currentTick = currentTick;
    }

    @Override
    public EntityInstance getPrimaryEntityInstance() {
        return this.primaryEntityInstance;
    }

    @Override
    public PropertyInstance getEnvironmentVariable(String name) {
        return activeEnvironment.getEvnVariable(name);
    }

    @Override
    public EntityInstanceManager getEntityInstanceManager() {
        return entityInstanceManager;
    }

    @Override
    public ActiveEnvironmentVariables getActiveEnvironmentVariables()
    {return activeEnvironment;}

    @Override
    public EntityInstance getSecondaryEntityInstance() {
        return secondaryEntityInstance;
    }

    @Override
    public int getCurrentTick() {
        return currentTick;
    }

}
