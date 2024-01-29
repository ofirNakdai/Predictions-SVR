package engine.context;

import engine.entity.EntityInstance;
import engine.entity.manager.api.EntityInstanceManager;
import engine.environment.active.ActiveEnvironmentVariables;
import engine.property.api.PropertyInstance;

public interface Context {
    EntityInstance getPrimaryEntityInstance();

    PropertyInstance getEnvironmentVariable(String name);

    EntityInstanceManager getEntityInstanceManager();

    ActiveEnvironmentVariables getActiveEnvironmentVariables();

    EntityInstance getSecondaryEntityInstance();
    int getCurrentTick();
}
