package engine.action.impl;

import engine.action.api.AbstractAction;
import engine.action.api.ActionType;
import engine.context.Context;
import engine.entity.EntityInstance;
import engine.expression.Expression;
import engine.property.api.PropertyInstance;
import engine.property.impl.DecimalProperty;
import engine.property.impl.FloatProperty;
import engine.world.factory.SecondaryEntityDetails;

public class Decrease extends AbstractAction {
    private final String propertyName;
    private final String byExpression;

    public Decrease(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String propertyName, String byExpression) {
        super(ActionType.DECREASE, mainEntityName, secondaryEntityDetails);
        this.propertyName = propertyName;
        this.byExpression = byExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getByExpression() {
        return byExpression;
    }

    @Override
    public void Run(Context context) {
        Expression byAsExpression = new Expression(byExpression, context);

        EntityInstance mainEntity = getMainEntityInstance(context);

        PropertyInstance mainEntityPropertyInstance = mainEntity.getPropertyByName(propertyName);

        String byFromExpression = byAsExpression.praseExpressionToValueString(mainEntityPropertyInstance.getType());


        try {
            if (mainEntityPropertyInstance instanceof DecimalProperty) {
                ((DecimalProperty) mainEntityPropertyInstance).setValue( (Integer.parseInt(mainEntityPropertyInstance.getValue()) - Integer.parseInt(byFromExpression)) , context.getCurrentTick());
            } else if (mainEntityPropertyInstance instanceof FloatProperty) {
                ((FloatProperty) mainEntityPropertyInstance).setValue( (Float.parseFloat(mainEntityPropertyInstance.getValue()) - Float.parseFloat(byFromExpression)) , context.getCurrentTick());
            }
        }catch (NumberFormatException e)
        {
            throw new NumberFormatException("can not add float to integer in action Increase\n" + e.getMessage());
        }
    }
}
