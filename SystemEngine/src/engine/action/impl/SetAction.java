package engine.action.impl;

import engine.action.api.AbstractAction;
import engine.action.api.ActionType;
import engine.context.Context;
import engine.entity.EntityInstance;
import engine.expression.Expression;
import engine.property.api.PropertyInstance;
import engine.property.impl.BooleanProperty;
import engine.property.impl.DecimalProperty;
import engine.property.impl.FloatProperty;
import engine.property.impl.StringProperty;
import engine.world.factory.SecondaryEntityDetails;

public class SetAction extends AbstractAction {
    private final String propertyName;
    private final String valueExpression;
    public SetAction(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String propertyName, String valueExpression){
        super(ActionType.SET, mainEntityName, secondaryEntityDetails);
        this.propertyName = propertyName;
        this.valueExpression = valueExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    @Override
    public void Run(Context context) {
        Expression valueAsExpression = new Expression(valueExpression, context);

//        PropertyInstance EntityPropertyInstance = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
        EntityInstance mainEntity = getMainEntityInstance(context);
        PropertyInstance mainEntityPropertyInstance = mainEntity.getPropertyByName(propertyName);

        String valueFromExpression = valueAsExpression.praseExpressionToValueString(mainEntityPropertyInstance.getType());

        try {
            if (mainEntityPropertyInstance instanceof DecimalProperty) {
                ((DecimalProperty) mainEntityPropertyInstance).setValue(Integer.parseInt(valueFromExpression), context.getCurrentTick());
            }
            else if (mainEntityPropertyInstance instanceof FloatProperty) {
                ((FloatProperty) mainEntityPropertyInstance).setValue(Float.parseFloat(valueFromExpression), context.getCurrentTick());
            }
            else if (mainEntityPropertyInstance instanceof BooleanProperty) {
                if (valueFromExpression.equals("true") || valueFromExpression.equals("false"))
                {
                    ((BooleanProperty) mainEntityPropertyInstance).setValue(Boolean.valueOf(valueFromExpression), context.getCurrentTick());
                }
                else{
                    throw new IllegalArgumentException("Error in Set action! Can not set the value " + valueFromExpression + " to the boolean property: " + mainEntityPropertyInstance.getName());
                }
            }
            else if (mainEntityPropertyInstance instanceof StringProperty) {
                ((StringProperty) mainEntityPropertyInstance).setValue(valueFromExpression, context.getCurrentTick());
            }
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("Error in Set action! Can not set the value " + valueFromExpression + " to the " + mainEntityPropertyInstance.getType() + " property: " + mainEntityPropertyInstance.getName());
        }
    }
}
