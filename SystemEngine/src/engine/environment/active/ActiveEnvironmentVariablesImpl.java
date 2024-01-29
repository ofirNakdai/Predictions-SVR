package engine.environment.active;

import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;
import engine.property.impl.BooleanProperty;
import engine.property.impl.DecimalProperty;
import engine.property.impl.FloatProperty;
import engine.property.impl.StringProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActiveEnvironmentVariablesImpl implements ActiveEnvironmentVariables, Serializable {
    private final Map<String, PropertyInstance> envVariables;

    public ActiveEnvironmentVariablesImpl(){
        this.envVariables = new HashMap<>();
    }
    @Override
    public PropertyInstance getEvnVariable(String name) {
        if(this.envVariables.containsKey(name))
            return this.envVariables.get(name);
        throw new IllegalArgumentException("Can't find environment variable: " + name);
    }

    @Override
    public void createEvnVariableFromDef(PropertyDefinition EvnVarDef) {
        switch (EvnVarDef.getType()){
            case BOOLEAN:
                this.envVariables.put(EvnVarDef.getName(), new BooleanProperty(EvnVarDef));
                break;
            case DECIMAL:
                this.envVariables.put(EvnVarDef.getName(), new DecimalProperty(EvnVarDef));
                break;
            case FLOAT:
                this.envVariables.put(EvnVarDef.getName(), new FloatProperty(EvnVarDef));
                break;
            case STRING:
                this.envVariables.put(EvnVarDef.getName(), new StringProperty(EvnVarDef));
                break;
        }
    }

    @Override
    public Collection<PropertyInstance> getEvnVariables() {
        return envVariables.values();
    }
}
