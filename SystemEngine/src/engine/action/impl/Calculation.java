package engine.action.impl;

import engine.action.api.AbstractAction;
import engine.action.api.ActionType;
import engine.action.api.ClacType;
import engine.context.Context;
import engine.entity.EntityInstance;
import engine.expression.Expression;
import engine.property.api.PropertyInstance;
import engine.property.impl.DecimalProperty;
import engine.property.impl.FloatProperty;
import engine.world.factory.SecondaryEntityDetails;

public class Calculation extends AbstractAction {
    private final String propertyName;
    private final String arg1Expression;
    private final String arg2Expression;
    private final ClacType calcType;

    public Calculation(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String propertyName, String arg1Expression, String arg2Expression, ClacType calcType) {
        super(ActionType.CALCULATION, mainEntityName, secondaryEntityDetails);
        this.propertyName = propertyName;
        this.arg1Expression = arg1Expression;
        this.arg2Expression = arg2Expression;
        this.calcType = calcType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getArg1Expression() {
        return arg1Expression;
    }

    public String getArg2Expression() {
        return arg2Expression;
    }

    public ClacType getCalcType() {
        return calcType;
    }

    @Override
    public void Run(Context context) {

        Expression arg1AsExpression = new Expression(arg1Expression, context);
        Expression arg2AsExpression = new Expression(arg2Expression, context);

        EntityInstance mainEntityInstance = getMainEntityInstance(context);
        PropertyInstance mainEntityPropertyInstance = mainEntityInstance.getPropertyByName(propertyName);

        String value1FromExpression = arg1AsExpression.praseExpressionToValueString(mainEntityPropertyInstance.getType());
        String value2FromExpression = arg2AsExpression.praseExpressionToValueString(mainEntityPropertyInstance.getType());



        if (mainEntityPropertyInstance instanceof DecimalProperty)
        {
            try {
                switch (calcType) {
                    case MULTIPLY:
                        ((DecimalProperty) mainEntityPropertyInstance).setValue((Integer.parseInt(value1FromExpression) * Integer.parseInt(value2FromExpression)) ,  context.getCurrentTick());
                        break;
                    case DIVIDE:
                        if (Integer.parseInt(value2FromExpression) == 0) {
                            throw new ArithmeticException("Can not divide by zero in action calculation");
                        } else {
                            ((DecimalProperty) mainEntityPropertyInstance).setValue((Integer.parseInt(value1FromExpression) / Integer.parseInt(value2FromExpression)) , context.getCurrentTick());
                        }
                        break;

                }
            }
            catch(NumberFormatException e)
            {
                throw new IllegalArgumentException("Cannot receive float argument: " + value1FromExpression + ", to action Calculation with decimal property: " + mainEntityPropertyInstance.getName());
            }

        }
        else if (mainEntityPropertyInstance instanceof FloatProperty)
        {
            switch (calcType) {
                case MULTIPLY:
                    ((FloatProperty) mainEntityPropertyInstance).setValue((Float.parseFloat(value1FromExpression) * Float.parseFloat(value2FromExpression)) , context.getCurrentTick());
                    break;
                case DIVIDE:
                    if (Float.parseFloat(value2FromExpression) == 0){
                        throw new ArithmeticException("Can not divide by zero in action calculation");
                    }
                    else{
                        ((FloatProperty) mainEntityPropertyInstance).setValue((Float.parseFloat(value1FromExpression) / Float.parseFloat(value2FromExpression)) , context.getCurrentTick());
                    }
                    break;
            }
        }
    }
}





