package engine.property.impl;

import engine.property.PropertyDefinition;
import engine.property.api.PropertyInstance;

import java.util.Random;

public class StringProperty extends PropertyInstance {

    public StringProperty(PropertyDefinition propertyDefinition) {
        super(propertyDefinition.getName(), propertyDefinition.getType());

        if (propertyDefinition.isInitializedRandomly()) {
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            int valueLen = random.nextInt(50) + 1;
            String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 !?,-_().";

            for (int i = 0; i < valueLen; i++) {
                int randomIndex = random.nextInt(allowedCharacters.length());
                char randomChar = allowedCharacters.charAt(randomIndex);
                sb.append(randomChar);
            }
            this.value = sb.toString();
        }
        else{
            this.value = propertyDefinition.getInitValue();
        }
    }

    public void setValue(String value, Integer currTick) {
        String oldValue = this.value;

        this.value = value;

        if (!this.value.equals(oldValue)){ //value has changed
            setLastTickModified(currTick);
        }
    }
}
