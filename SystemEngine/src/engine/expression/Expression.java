package engine.expression;

import engine.context.Context;
import engine.environment.active.ActiveEnvironmentVariables;
import engine.property.PropertyType;

import java.util.Random;

public class Expression {
    protected final String rawExpression;
    private final Context context;

    public Expression(String rawExpression, Context context) {
        this.rawExpression = rawExpression;
        this.context = context;
    }

    public String praseExpressionToValueString(PropertyType typeOfExpectedValue)
    {
        if ((rawExpression.startsWith("environment")) || (rawExpression.startsWith("random")) || (rawExpression.startsWith("percent")) || (rawExpression.startsWith("ticks")) || (rawExpression.startsWith("evaluate"))) {
            return convertHelpFunctionsToStr(typeOfExpectedValue);
        }

        else if(context.getPrimaryEntityInstance().getPropertyByName(rawExpression) != null){
            return context.getPrimaryEntityInstance().getPropertyByName(rawExpression).getValue();
        }

        else if(context.getSecondaryEntityInstance() != null && context.getSecondaryEntityInstance().getPropertyByName(rawExpression) != null) {
            return context.getSecondaryEntityInstance().getPropertyByName(rawExpression).getValue();
        }

        else {
            return rawExpression;
        }
    }
    private String convertHelpFunctionsToStr(PropertyType typeOfExpectedValue)
    {
        ActiveEnvironmentVariables environmentVariables = context.getActiveEnvironmentVariables();
        String mainEntityName = context.getPrimaryEntityInstance().getName();
        String secondaryEntityName = "";
        if (context.getSecondaryEntityInstance() != null)
        {
            secondaryEntityName = context.getSecondaryEntityInstance().getName();
        }

        if (rawExpression.startsWith("environment")){
            String envVarName = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));
            if(environmentVariables.getEvnVariable(envVarName).getType() == typeOfExpectedValue){
                return environmentVariables.getEvnVariable(envVarName).getValue();
            }
            else{
                throw new IllegalArgumentException("Expected environment variable of type: " + typeOfExpectedValue + " and received environment variable of type: " + environmentVariables.getEvnVariable(envVarName).getType() + " in function environment");
            }

        }
        else if(rawExpression.startsWith("random")){
            String value = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));
            try {
                int val = Integer.parseInt(value);
                Random random = new Random();
                if(typeOfExpectedValue == PropertyType.DECIMAL || typeOfExpectedValue == PropertyType.FLOAT) {
                    return String.valueOf(random.nextInt(val) + 1);
                } else{
                    throw new IllegalArgumentException("Error, trying to insert a decimal value (from random function) to a property of type: " + typeOfExpectedValue);
                }
            }
            catch (NumberFormatException e){
                throw new RuntimeException(e + "\n random function must get a decimal number as argument");
            }

        }
        else if (rawExpression.startsWith("evaluate")) {
            String value = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));
            String propertyName = value.substring(value.indexOf('.') + 1);

            if (value.startsWith(mainEntityName + ".")){
                if (context.getPrimaryEntityInstance().getPropertyByName(propertyName) != null){
                    return context.getPrimaryEntityInstance().getPropertyByName(propertyName).getValue();
                } else{
                    throw new IllegalArgumentException("In evaluate function, the entity: " + mainEntityName + " does not have the property: " + propertyName);
                }
            } else if (value.startsWith(secondaryEntityName + ".")){
                if(context.getSecondaryEntityInstance().getPropertyByName(propertyName) != null){
                    return context.getSecondaryEntityInstance().getPropertyByName(propertyName).getValue();
                } else{
                    throw new IllegalArgumentException("In evaluate function, the entity: " + secondaryEntityName + " does not have the property: " + propertyName);
                }

            } else{
                throw new IllegalArgumentException("evaluate function got the entity: " + value.substring(0, value.indexOf('.')) + " which is not in it's context");
            }

        }
        else if(rawExpression.startsWith("percent")){
            Expression innerExp1 = new Expression(rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(',')), context);
            Expression innerExp2 = new Expression(rawExpression.substring(rawExpression.indexOf(',') + 1, rawExpression.lastIndexOf(')')), context);

            String innerValue1 = innerExp1.praseExpressionToValueString(PropertyType.FLOAT);
            String innerValue2 = innerExp2.praseExpressionToValueString(PropertyType.FLOAT);

            float numericExp1;
            float numericExp2;

            try {
                numericExp1 = Float.parseFloat(innerValue1);
                numericExp2 = Float.parseFloat(innerValue2);
            }
            catch(NumberFormatException e){
                throw new IllegalArgumentException("Percent got non numeric argument: " + e.getMessage());
            }

            return String.valueOf(numericExp1 * (numericExp2 / 100));
        }
        else //if(rawExpression.startsWith("ticks")){
        {
            String value = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));

            String propertyName = value.substring(value.indexOf('.') + 1);

            if (value.startsWith(mainEntityName + ".")) {
                if (context.getPrimaryEntityInstance().getPropertyByName(propertyName) != null) {
                    int lastTickThePropertyModified = context.getPrimaryEntityInstance().getPropertyByName(propertyName).getLastTickModified();
                    return String.valueOf(context.getCurrentTick() - lastTickThePropertyModified);
                }else{
                    throw new IllegalArgumentException("In ticks function, the entity: " + mainEntityName + " does not have the property: " + propertyName);
                }
            }
            else if (value.startsWith(secondaryEntityName + ".")) {
                if (context.getSecondaryEntityInstance().getPropertyByName(propertyName) != null) {
                    int lastTickThePropertyModified = context.getSecondaryEntityInstance().getPropertyByName(propertyName).getLastTickModified();
                    return String.valueOf(context.getCurrentTick() - lastTickThePropertyModified);
                }else{
                    throw new IllegalArgumentException("In ticks function, the entity: " + mainEntityName + " does not have the property: " + propertyName);
                }
            }
            else{
                throw new IllegalArgumentException("ticks function got the entity: " + value.substring(0, value.indexOf('.')) + " which is not in it's context");
            }
        }
    }

    public PropertyType evaluateExpressionType(){
        String mainEntityName = context.getPrimaryEntityInstance().getName();
        String secondaryEntityName = "";
        if (context.getSecondaryEntityInstance() != null)
        {
            secondaryEntityName = context.getSecondaryEntityInstance().getName();
        }

        if (rawExpression.startsWith("environment")){
            String envVarName = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));
            return context.getActiveEnvironmentVariables().getEvnVariable(envVarName).getType();
        }

        else if(rawExpression.startsWith("evaluate")){
            String value = rawExpression.substring(rawExpression.indexOf('(') + 1, rawExpression.indexOf(')'));
            String propertyName = value.substring(value.indexOf('.') + 1);

            if (value.startsWith(mainEntityName + ".")){
                if (context.getPrimaryEntityInstance().getPropertyByName(propertyName) != null){
                    return context.getPrimaryEntityInstance().getPropertyByName(propertyName).getType();
                } else{
                    throw new IllegalArgumentException("In evaluate function, the entity: " + mainEntityName + " does not have the property: " + propertyName);
                }
            } else if (rawExpression.startsWith(secondaryEntityName + ".")){
                if(context.getSecondaryEntityInstance().getPropertyByName(propertyName) != null){
                    return context.getSecondaryEntityInstance().getPropertyByName(propertyName).getType();
                } else{
                    throw new IllegalArgumentException("In evaluate function, the entity: " + secondaryEntityName + " does not have the property: " + propertyName);
                }

            } else{
                throw new IllegalArgumentException("evaluate function got the entity: " + value.substring(0, value.indexOf('.')) + " which is not in it's context");
            }
        }

        else if ((rawExpression.startsWith("random")) || (rawExpression.startsWith("percent")) || (rawExpression.startsWith("ticks"))){
            return PropertyType.FLOAT;
        }

        else if(context.getPrimaryEntityInstance().getPropertyByName(rawExpression) != null){
            return context.getPrimaryEntityInstance().getPropertyByName(rawExpression).getType();
        }

        else if(context.getSecondaryEntityInstance() != null && context.getSecondaryEntityInstance().getPropertyByName(rawExpression) != null) {
            return context.getSecondaryEntityInstance().getPropertyByName(rawExpression).getType();
        }

        else {
            return PropertyType.STRING;
        }

    }
}
