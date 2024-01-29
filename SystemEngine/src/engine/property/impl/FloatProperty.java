package engine.property.impl;

import engine.range.Range;
import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;

import java.util.Random;

public class FloatProperty extends PropertyInstance {

    private final Range range;

    public FloatProperty(PropertyDefinition propertyDefinition) {
        super(propertyDefinition.getName(), propertyDefinition.getType());
        this.range = propertyDefinition.getValueRange();

        if(propertyDefinition.isInitializedRandomly()){
            Random random = new Random();
            if (range != null)
            {
                float from = range.getFrom().floatValue();
                float to = range.getTo().floatValue();
                this.value = String.valueOf(from + random.nextFloat() * (to - from));
            }
            else
            {
                this.value = String.valueOf(random.nextFloat());
            }
        }
        else {
            float valueAsFloat = Float.parseFloat(propertyDefinition.getInitValue());
            if (range == null || (valueAsFloat <= range.getTo().floatValue() && valueAsFloat >= range.getFrom().floatValue()))
            {
                this.value = propertyDefinition.getInitValue();
            }
            else
            {
                throw new RuntimeException();
            }
        }
    }

    public void setValue(Float value, Integer currTick) {
        String oldValue = this.value;

        if (range == null || (value <= range.getTo().floatValue() && value >= range.getFrom().floatValue()))
            this.value = value.toString();
        else if(value > range.getTo().floatValue())
        {
            this.value = String.valueOf(range.getTo().floatValue());
        }
        else if(value < range.getFrom().floatValue())
        {
            this.value = String.valueOf(range.getFrom().floatValue());
        }

        if (!this.value.equals(oldValue)){ //value has changed
            setLastTickModified(currTick);
        }
    }
}
