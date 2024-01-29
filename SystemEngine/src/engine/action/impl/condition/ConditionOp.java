package engine.action.impl.condition;

import engine.property.PropertyType;

public enum ConditionOp {

    EQUALS{
        @Override
        public boolean eval(String propertyValue, String value, PropertyType propertyType)
        {
                switch (propertyType) {
                    case BOOLEAN:
                        if (!(value.equals("true") || value.equals("false"))) {
                            throw new ClassCastException("Cannot compare" + value + "to boolean");
                        }
                        return Boolean.valueOf(propertyValue) == Boolean.valueOf(value);
                    case DECIMAL:
                        return Integer.parseInt(propertyValue) == Integer.parseInt(value);
                    case FLOAT:
                        return Float.parseFloat(propertyValue) == Float.parseFloat(value);
                    case STRING:
                        return propertyValue.equals(value);
                }
                return false;

        }
    },
    NOTEQUALS {
        @Override
        public boolean eval(String propertyValue, String value, PropertyType propertyType) {
                switch (propertyType){
                    case BOOLEAN:
                        if(!(value.equals("true") || value.equals("false")))
                        {
                            throw new ClassCastException("Cannot compare" + value + "to boolean");
                        }
                        return Boolean.valueOf(propertyValue) != Boolean.valueOf(value);
                    case DECIMAL:
                        return Integer.parseInt(propertyValue) != Integer.parseInt(value);
                    case FLOAT:
                        return Float.parseFloat(propertyValue) != Float.parseFloat(value);
                    case STRING:
                        return !(propertyValue.equals(value));
                }
                return false;
        }
    },
    BT {
        @Override
        public boolean eval(String propertyValue, String value, PropertyType propertyType) {
            switch (propertyType){
                case DECIMAL:
                    return Integer.parseInt(propertyValue) > Integer.parseInt(value);
                case FLOAT:
                    return Float.parseFloat(propertyValue) > Float.parseFloat(value);
                default:
                    throw new IllegalArgumentException();
            }

        }
    },
    LT {
        @Override
        public boolean eval(String propertyValue, String value, PropertyType propertyType) {
            switch (propertyType){
                case DECIMAL:
                    return Integer.parseInt(propertyValue) < Integer.parseInt(value);
                case FLOAT:
                    return Float.parseFloat(propertyValue) < Float.parseFloat(value);
                default:
                    throw new IllegalArgumentException();
            }
        }
    };

    public abstract boolean eval(String propertyValue, String value, PropertyType propertyType);
}
