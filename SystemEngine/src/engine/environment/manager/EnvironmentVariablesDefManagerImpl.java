package engine.environment.manager;

import engine.property.PropertyDefinition;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentVariablesDefManagerImpl implements EnvironmentVariablesDefManager, Serializable {

    private final Map<String, PropertyDefinition> name2PropertyDef;

    public EnvironmentVariablesDefManagerImpl() {
        this.name2PropertyDef = new HashMap<>();
    }

    @Override
    public void addEnvironmentVariableDef(PropertyDefinition propertyDefinition) {
        name2PropertyDef.put(propertyDefinition.getName(), propertyDefinition);
    }

    @Override
    public Collection<PropertyDefinition> getEnvironmentVariablesDefinitions() {
        return this.name2PropertyDef.values();
    }

    @Override
    public PropertyDefinition getEnvironmentVariableDefByName(String name) throws IllegalArgumentException {
        if(this.name2PropertyDef.containsKey(name)){
            return this.name2PropertyDef.get(name);
        }
        throw new IllegalArgumentException("Environment variable: " + name + " does not exist!");
    }
}
