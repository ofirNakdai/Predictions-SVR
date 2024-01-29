package engine.property;

import engine.range.Range;

import java.io.Serializable;

public class PropertyDefinition implements Serializable, Cloneable {
    private final String name;
    private final PropertyType type;
    private Range valueRange;
    private boolean isInitializedRandomly;
    private String initValue;

    public PropertyDefinition(String name, PropertyType type, Range valueRange, boolean isInitializedRandomly, String initValue) {
        this.name = name;
        this.type = type;
        this.valueRange = valueRange;
        this.isInitializedRandomly = isInitializedRandomly;
        this.initValue = initValue;
    }

    public PropertyDefinition(PropertyDefinition copy) {
        name = copy.name;
        type = copy.type;
        valueRange = new Range(copy.valueRange.getFrom().doubleValue(), copy.valueRange.getTo().doubleValue());
        isInitializedRandomly = copy.isInitializedRandomly;
        initValue = copy.initValue;
    }

    public String getName() {
        return name;
    }

    public PropertyType getType() {
        return type;
    }

    public Range getValueRange() {
        return valueRange;
    }

    public boolean isInitializedRandomly() {
        return isInitializedRandomly;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitializedRandomly(boolean initializedRandomly) {
        isInitializedRandomly = initializedRandomly;
    }

    public void setInitValue(String initValue) {

        checkValidValueType(initValue);
        checkValueInRange(initValue);
        this.initValue = initValue;
    }

    private void checkValidValueType(String newValue) {
        switch (this.type) {
            case BOOLEAN:
                if (!(newValue.equals("true") || newValue.equals("false"))) {
                    throw new ClassCastException("Type is not matching, expecting boolean!");
                }
                break;
            case DECIMAL:
                try {
                    Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Type is not matching, expecting decimal!");
                }
                break;
            case FLOAT:
                try {
                    Float.parseFloat(newValue);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Type is not matching, expecting float!");
                }
                break;
        }
    }

    private void checkValueInRange(String newValue) {
        if (this.valueRange != null) {
            if (this.type == PropertyType.DECIMAL) {
                int decimalValue = Integer.parseInt(newValue);
                if (!(decimalValue >= valueRange.getFrom().intValue() && decimalValue <= valueRange.getTo().intValue())) {
                    throw new RuntimeException("Value not in range (" + valueRange.getFrom().intValue() + " - " + valueRange.getTo().intValue() + ")");
                }
            } else if (this.type == PropertyType.FLOAT) {
                float floatValue = Float.parseFloat(newValue);
                if (!(floatValue >= valueRange.getFrom().floatValue() && floatValue <= valueRange.getTo().floatValue())) {
                    throw new RuntimeException("Value not in range (" + valueRange.getFrom() + " - " + valueRange.getTo() + ")");
                }
            }
        }
    }

//    @Override
//    public PropertyDefinition clone() {
//        try {
//            PropertyDefinition clone = (PropertyDefinition) super.clone();
//            clone.name = name;
//            clone.type = type;
//            clone.valueRange = new Range(copy.valueRange.getFrom().doubleValue(), copy.valueRange.getTo().doubleValue());
//            clone.isInitializedRandomly = copy.isInitializedRandomly;
//            clone.initValue = copy.initValue;
//            return clone;
//        } catch (CloneNotSupportedException e) {
//            throw new AssertionError();
//        }
//    }

    @Override
    public PropertyDefinition clone() {
        try {
            PropertyDefinition clone = (PropertyDefinition) super.clone();
            if(valueRange != null) {
                clone.valueRange = new Range(valueRange.getFrom().doubleValue(), valueRange.getTo().doubleValue());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
