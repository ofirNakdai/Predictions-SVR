package engine.environment.manager;

import engine.property.PropertyDefinition;

import java.util.Collection;

public interface EnvironmentVariablesDefManager {
    void addEnvironmentVariableDef(PropertyDefinition propertyDefinition);
    Collection<PropertyDefinition> getEnvironmentVariablesDefinitions();
    PropertyDefinition getEnvironmentVariableDefByName(String name) throws IllegalArgumentException;
}
