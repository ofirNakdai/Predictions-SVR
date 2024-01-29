package engine.entity;
import engine.property.PropertyDefinition;

import java.io.Serializable;
import java.util.*;

public class EntityDefinition implements Serializable, Cloneable {
    private final String name;
    private int population;
    private Map<String, PropertyDefinition> name2propertyDef = new HashMap<>();

    public EntityDefinition(String name, int population) {
        this.population = population;
        this.name = name;
    }

    public EntityDefinition(EntityDefinition copy){
        this.name = copy.name;
        this.population = copy.population;

        copy.name2propertyDef.values().forEach(propertyDef -> this.name2propertyDef.put(propertyDef.getName(), new PropertyDefinition(propertyDef)));
    }

    public void addPropertyDefinition(PropertyDefinition propertyDefinitionToAdd){
        name2propertyDef.put(propertyDefinitionToAdd.getName(), propertyDefinitionToAdd);
    }

    public PropertyDefinition getPropertyDefinitionByName(String propertyName){
        return name2propertyDef.get(propertyName);
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }
    public void setPopulation(int population) {
        this.population = population;
    }

    public Map<String, PropertyDefinition> getName2propertyDef() {
        return name2propertyDef;
    }

    @Override
    public EntityDefinition clone() {
        try {
            EntityDefinition clone = (EntityDefinition) super.clone();
            clone.name2propertyDef = new HashMap<>();
            for (Map.Entry<String, PropertyDefinition> entry : name2propertyDef.entrySet()) {
                clone.name2propertyDef.put(entry.getKey(), entry.getValue().clone());
            }
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
