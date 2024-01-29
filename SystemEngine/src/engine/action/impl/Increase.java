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

public class Increase extends AbstractAction {
    private final String propertyName;
    private final String byExpression;

    public Increase(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String propertyName, String byExpression) {
        super(ActionType.INCREASE, mainEntityName, secondaryEntityDetails);
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
//        PropertyInstance mainEntityPropertyInstance;
//        if (mainEntityName.equals(context.getPrimaryEntityInstance().getName()))
//            mainEntityPropertyInstance = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
//        else if (context.getSecondaryEntityInstance() != null && mainEntityName.equals(context.getSecondaryEntityInstance().getName()))
//            mainEntityPropertyInstance = context.getSecondaryEntityInstance().getPropertyByName(propertyName);
//        else {
//            throw new RuntimeException("The entity: " + mainEntityName + " is not in the context of the action"); //NEEDED? OR ALREADY CHECKED IN XML PARSING?
//        }

        //USING PROTECTED METHOD:
        EntityInstance mainEntity = getMainEntityInstance(context);

        PropertyInstance mainEntityPropertyInstance = mainEntity.getPropertyByName(propertyName);

        String byFromExpression = byAsExpression.praseExpressionToValueString(mainEntityPropertyInstance.getType());


        try {
            if (mainEntityPropertyInstance instanceof DecimalProperty) {
                ((DecimalProperty) mainEntityPropertyInstance).setValue( (Integer.parseInt(mainEntityPropertyInstance.getValue()) + Integer.parseInt(byFromExpression)) , context.getCurrentTick());
            } else if (mainEntityPropertyInstance instanceof FloatProperty) {
                ((FloatProperty) mainEntityPropertyInstance).setValue( (Float.parseFloat(mainEntityPropertyInstance.getValue()) + Float.parseFloat(byFromExpression)) , context.getCurrentTick());
            }
        }catch (NumberFormatException e)
        {
            throw new NumberFormatException("can not add float to integer in action Increase\n" + e.getMessage());
        }
    }
}
