package engine.environment.active;

import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;

import java.util.Collection;

public interface ActiveEnvironmentVariables {
    PropertyInstance getEvnVariable(String name);
    Collection<PropertyInstance> getEvnVariables();
    void createEvnVariableFromDef(PropertyDefinition EvnVarDef);
}
