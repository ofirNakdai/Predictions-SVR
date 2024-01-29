package engine.action.impl.condition.impl;


import engine.action.impl.condition.ConditionOp;
import engine.action.impl.condition.api.Condition;
import engine.context.Context;
import engine.expression.Expression;
import engine.property.PropertyType;
import engine.world.factory.SecondaryEntityDetails;

public class SingleCondition extends ConditionImpl implements Condition
{
    final String firstArgExpression;
    final ConditionOp operator;
    final String secondArgExpression;

    public SingleCondition(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String firstArgExpression, ConditionOp conditionOperator, String secondArgExpression) {
        super(mainEntityName, secondaryEntityDetails);
        this.firstArgExpression = firstArgExpression;
        this.operator = conditionOperator;
        this.secondArgExpression = secondArgExpression;
    }

    public String getFirstArgExpression() {
        return firstArgExpression;
    }

    public ConditionOp getOperator() {
        return operator;
    }

    public String getSecondArgExpression() {
        return secondArgExpression;
    }

    @Override
    public void Run(Context context) {
        if (evaluateCondition(context)) {
            invokeThenActions(context);
        }
        else {
            invokeElseActions(context);
        }
    }

    @Override
    public boolean evaluateCondition(Context context) {
        boolean result = false;

        Expression firstArgAsExpression = new Expression(firstArgExpression, context);
        PropertyType firstArgType = firstArgAsExpression.evaluateExpressionType();//FIND ITS TYPE, AND THIS WILL BE THE REQUIRED TYPE FOR OTHER VALUETOCOMPARE

        String firstArg = firstArgAsExpression.praseExpressionToValueString(firstArgType);

        //figuring value out of expression
        Expression secondArgAsExpression = new Expression(secondArgExpression, context);
        String secondArg = secondArgAsExpression.praseExpressionToValueString(firstArgType);

        try {
            switch (firstArgType) {
                case BOOLEAN:
                    result = operator.eval(firstArg, secondArg, PropertyType.BOOLEAN);
                    break;
                case DECIMAL:
                    result = operator.eval(firstArg, secondArg, PropertyType.DECIMAL);
                    break;
                case FLOAT:
                    result = operator.eval(firstArg, secondArg, PropertyType.FLOAT);
                    break;
                case STRING:
                    result = operator.eval(firstArg, secondArg, PropertyType.STRING);
                    break;
            }
        }catch (ClassCastException | NumberFormatException e)
        {
            throw new IllegalArgumentException("not-matching argument to single condition action, the first argument: " + firstArg + " is of type: " + firstArgType + " and the second one is: " + secondArg);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalArgumentException("not-matching argument to single condition action, cannot perform bt or lt comparison on the argument: " + firstArg + " because it is of type: " + firstArgType);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("not-matching argument to single condition action, the first argument: " + firstArg + " is of type: " + firstArgType + " and the second one is: " + secondArg);
        }
        return result;
    }
}
