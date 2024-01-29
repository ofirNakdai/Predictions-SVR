package engine.world.factory;

import engine.action.impl.replace.CreationMode;
import engine.action.impl.replace.Replace;
import engine.range.Range;
import engine.world.WorldDefinition;
import engine.action.api.Action;
import engine.action.api.ClacType;
import engine.action.impl.*;
import engine.action.impl.condition.ConditionOp;
import engine.action.impl.condition.LogicalOperator;
import engine.action.impl.condition.impl.ConditionImpl;
import engine.action.impl.condition.impl.MultipleCondition;
import engine.action.impl.condition.impl.SingleCondition;
import engine.entity.EntityDefinition;
import engine.property.PropertyDefinition;
import engine.property.PropertyType;
import engine.rule.Rule;
import engine.rule.RuleImpl;
import exceptions.xml.NotExistingEntityException;
import exceptions.xml.NotExistingPropertyException;
import exceptions.xml.NotUniqueEnvVarException;
import exceptions.xml.NotUniquePropertyException;
import jaxb.generated2.*;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class WorldDefFactoryImpl implements WorldDefFactory, Serializable {

    private WorldDefinition currWorkingWorld;

    @Override
    public void insertDataToWorldDefinition(WorldDefinition simulationDef, PRDWorld generatedWorld) {
        this.currWorkingWorld = simulationDef;

        try{
            addGrid(generatedWorld);
            addEnvironmentVariables(generatedWorld); // done
            addEntitiesDefinitions(generatedWorld); // done
            addRules(generatedWorld);
            addTerminationSettings(generatedWorld);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void addGrid(PRDWorld generatedWorld) {
        int rows = generatedWorld.getPRDGrid().getRows();
        int columns = generatedWorld.getPRDGrid().getColumns();

        if (rows < 10 || 100 < rows)
        {
            throw new RuntimeException("The number of rows in the grid must be between 10 to 100!" + System.lineSeparator() + "Got: " + rows);
        }
        if (columns < 10 || 100 < columns)
        {
            throw new RuntimeException("The number of columns in the grid must be between 10 to 100!" + System.lineSeparator() + "Got: " + + columns);
        }

        currWorkingWorld.setNumOfRowsInGrid(rows);
        currWorkingWorld.setNumOfColsInGrid(columns);
    }

    private void addEnvironmentVariables(PRDWorld generatedWorld) {
        int environmentVariablesCount = generatedWorld.getPRDEnvironment().getPRDEnvProperty().size();
        Set<String> EnvVarsNames = new HashSet<>();

        for (int i = 0; i < environmentVariablesCount; i++) {
            PRDEnvProperty prdEnvProperty = generatedWorld.getPRDEnvironment().getPRDEnvProperty().get(i);
            if (EnvVarsNames.contains(prdEnvProperty.getPRDName())) {
                throw new NotUniqueEnvVarException(prdEnvProperty.getPRDName());
            }
            else
            {
                EnvVarsNames.add(prdEnvProperty.getPRDName());
            }

            PropertyDefinition newEnvVarDef;

            if (prdEnvProperty.getPRDRange() != null) {
                newEnvVarDef = new PropertyDefinition(prdEnvProperty.getPRDName(), PropertyType.valueOf(prdEnvProperty.getType().toUpperCase()),
                        new Range(prdEnvProperty.getPRDRange().getFrom(), prdEnvProperty.getPRDRange().getTo()),
                        true, "");
            }
            else{
                newEnvVarDef = new PropertyDefinition(prdEnvProperty.getPRDName(), PropertyType.valueOf(prdEnvProperty.getType().toUpperCase()),
                        null, true, "");
            }

            currWorkingWorld.addEnvironmentVariableDef(newEnvVarDef);
        }
    }

    private void addEntitiesDefinitions(PRDWorld generatedWorld) {
        int entitiesCount = generatedWorld.getPRDEntities().getPRDEntity().size();
        for (int i = 0; i < entitiesCount; i++) {
            PRDEntity prdEntity = generatedWorld.getPRDEntities().getPRDEntity().get(i);

            EntityDefinition newEntityDef = new EntityDefinition(prdEntity.getName(), 0);

            int entityPropertiesCount = prdEntity.getPRDProperties().getPRDProperty().size();
            Set<String> entityPropertiesNames = new HashSet<>();
            for (int j = 0; j < entityPropertiesCount; j++) {
                PRDProperty prdProperty = prdEntity.getPRDProperties().getPRDProperty().get(j);

                if (entityPropertiesNames.contains(prdProperty.getPRDName())) {
                    throw new NotUniquePropertyException(prdProperty.getPRDName(), prdEntity.getName());
                }
                else
                {
                    entityPropertiesNames.add(prdProperty.getPRDName());
                }

                PropertyDefinition newPropertyDef = createPropertyDefinitionFromPrdProperty(prdProperty);
                newEntityDef.addPropertyDefinition(newPropertyDef);
            }
            currWorkingWorld.addEntityDefinition(newEntityDef);
        }
    }

    private PropertyDefinition createPropertyDefinitionFromPrdProperty(PRDProperty prdProperty)
    {
        PropertyDefinition resPropertyDef;

        if (prdProperty.getPRDRange() != null) {
            resPropertyDef = new PropertyDefinition(prdProperty.getPRDName(), PropertyType.valueOf(prdProperty.getType().toUpperCase()),
                    new Range(prdProperty.getPRDRange().getFrom(), prdProperty.getPRDRange().getTo()),
                    prdProperty.getPRDValue().isRandomInitialize(), prdProperty.getPRDValue().getInit());
        }
        else{
            resPropertyDef = new PropertyDefinition(prdProperty.getPRDName(), PropertyType.valueOf(prdProperty.getType().toUpperCase()),
                    null, prdProperty.getPRDValue().isRandomInitialize(), prdProperty.getPRDValue().getInit());
        }

        return resPropertyDef;
    }

    private void addTerminationSettings(PRDWorld generatedWorld)
    {
        currWorkingWorld.setTerminationByUser(generatedWorld.getPRDTermination().getPRDByUser() != null);

        for (Object obj: generatedWorld.getPRDTermination().getPRDBySecondOrPRDByTicks()) {
            if (obj instanceof PRDByTicks) {
                PRDByTicks ticks = (PRDByTicks) obj;
                currWorkingWorld.setMaxNumberOfTicks(ticks.getCount());
            } else if (obj instanceof PRDBySecond) {
                PRDBySecond seconds = (PRDBySecond) obj;
                currWorkingWorld.setSecondsToTerminate(seconds.getCount());
            }
        }
    }

    private void addRules(PRDWorld generatedWorld) {
        for (PRDRule prdRule : generatedWorld.getPRDRules().getPRDRule()) {

            Rule currentRule;
            if (prdRule.getPRDActivation() != null) {
                currentRule = new RuleImpl(prdRule.getName(), prdRule.getPRDActivation().getTicks(), prdRule.getPRDActivation().getProbability());
            }
            else {
                currentRule = new RuleImpl(prdRule.getName(), null, null);
            }

            // Adding Rule Actions
            for (PRDAction prdAction : prdRule.getPRDActions().getPRDAction()) {
                Action newAction = createActionFromPrd(prdAction);
                currentRule.addAction(newAction);
            }
            currWorkingWorld.addRule(currentRule);
        }
    }


    private Action createActionFromPrd(PRDAction prdAction)
    {
        Action resAction = null;

            if (prdAction.getType().equals("proximity"))//IN PROXIMITY, THE ENTITIES ARE: SOURCE AND TARGET
            {
                if (currWorkingWorld.getEntityDefinitionByName(prdAction.getPRDBetween().getSourceEntity()) == null) {
                    throw new NotExistingEntityException(prdAction.getPRDBetween().getSourceEntity(), prdAction.getType());
                }
                if (currWorkingWorld.getEntityDefinitionByName(prdAction.getPRDBetween().getTargetEntity()) == null) {
                    throw new NotExistingEntityException(prdAction.getPRDBetween().getTargetEntity(), prdAction.getType());
                }
            } else if (prdAction.getType().equals("replace")) { //IN REPLACE, THE ENTITIES ARE: KILL AND CREATE
                if (currWorkingWorld.getEntityDefinitionByName(prdAction.getKill()) == null) {
                    throw new NotExistingEntityException(prdAction.getKill(), prdAction.getType());
                }
                if (currWorkingWorld.getEntityDefinitionByName(prdAction.getCreate()) == null) {
                    throw new NotExistingEntityException(prdAction.getCreate(), prdAction.getType());
                }
            } else {
                if (currWorkingWorld.getEntityDefinitionByName(prdAction.getEntity()) == null) {
                    throw new NotExistingEntityException(prdAction.getEntity(), prdAction.getType());
                }
            }


        SecondaryEntityDetails secondaryEntityDetails;
        try {
            secondaryEntityDetails = getSecondaryEntityDetails(prdAction.getPRDSecondaryEntity());
        }catch (NotExistingEntityException e){
            throw new NotExistingEntityException(e.getEntityName(), prdAction.getType());
        }

        switch (prdAction.getType()) {
            case "increase":
            case "decrease":

                checkForValidIncreaseDecreaseArguments(prdAction);

                if (prdAction.getType().equals("increase")) {
                    resAction = new Increase(prdAction.getEntity(), secondaryEntityDetails, prdAction.getProperty(), prdAction.getBy());
                } else {
                    resAction = new Decrease(prdAction.getEntity(), secondaryEntityDetails, prdAction.getProperty(), prdAction.getBy());
                }
                break;

            case "calculation":

                if (!isExistingPropertyInEntity(prdAction.getEntity(), prdAction.getResultProp())) {
                    throw new NotExistingPropertyException(prdAction.getResultProp(), prdAction.getType(), prdAction.getEntity());
                }
                else if (!isNumericPropertyInEntity(prdAction.getEntity(), prdAction.getResultProp()))
                {
                    throw new IllegalArgumentException("Property in " + prdAction.getType() + " must be of a numeric type.");
                }

                if (prdAction.getPRDMultiply() != null) {
                    if (!(isNumericArg(prdAction.getEntity(), prdAction.getPRDMultiply().getArg1()) && isNumericArg(prdAction.getEntity(), prdAction.getPRDMultiply().getArg2()))) {
                        throw new IllegalArgumentException("Arguments to " + prdAction.getType() + " action must be numeric.");
                    } else {
                        resAction = new Calculation(prdAction.getEntity(), secondaryEntityDetails, prdAction.getResultProp(), prdAction.getPRDMultiply().getArg1(), prdAction.getPRDMultiply().getArg2(), ClacType.MULTIPLY);
                    }
                } else if (prdAction.getPRDDivide() != null) {
                    if (!(isNumericArg(prdAction.getEntity(), prdAction.getPRDDivide().getArg1()) && isNumericArg(prdAction.getEntity(), prdAction.getPRDDivide().getArg2()))) {
                        throw new IllegalArgumentException("Arguments to " + prdAction.getType() + " action must be numeric.");
                    } else {
                        resAction = new Calculation(prdAction.getEntity(), secondaryEntityDetails, prdAction.getResultProp(), prdAction.getPRDDivide().getArg1(), prdAction.getPRDDivide().getArg2(), ClacType.DIVIDE);
                    }
                } else {
                    throw new IllegalArgumentException("Calculation action supports MULTIPLY or DIVIDE only!");
                }
                break;

            case "condition":
                ConditionImpl resCondition = createConditionAction(prdAction.getEntity(), prdAction.getPRDCondition(), secondaryEntityDetails);

                for (PRDAction prdActionInThen : prdAction.getPRDThen().getPRDAction()) {
                    resCondition.addActionToThen(createActionFromPrd(prdActionInThen));
                }

                if (prdAction.getPRDElse() != null) {
                    for (PRDAction prdActionInElse : prdAction.getPRDElse().getPRDAction()) {
                        resCondition.addActionToElse(createActionFromPrd(prdActionInElse));
                    }
                }

                resAction = resCondition;
                break;

            case "set":
                if (!isExistingPropertyInEntity(prdAction.getEntity(), prdAction.getProperty())) {
                    throw new NotExistingPropertyException(prdAction.getProperty(), prdAction.getType(), prdAction.getEntity());
                }
                resAction = new SetAction(prdAction.getEntity(), secondaryEntityDetails, prdAction.getProperty(), prdAction.getValue());
                break;

            case "kill":
                resAction = new Kill(prdAction.getEntity(), secondaryEntityDetails);
                break;

            case "replace":
                resAction = new Replace(prdAction.getKill(), prdAction.getCreate(), secondaryEntityDetails, CreationMode.valueOf(prdAction.getMode().toUpperCase()));
                break;
            case "proximity":
                if (!isNumericArg(prdAction.getPRDBetween().getSourceEntity(), prdAction.getPRDEnvDepth().getOf())) {
                    throw new IllegalArgumentException("Argument: " + prdAction.getPRDEnvDepth().getOf() + ", to action " + prdAction.getType() + " - expected to be numerical!");
                } else if (!isNumericArg(prdAction.getPRDBetween().getTargetEntity(), prdAction.getPRDEnvDepth().getOf())) {
                    throw new IllegalArgumentException("Argument: " + prdAction.getPRDEnvDepth().getOf() + ", to action " + prdAction.getType() + " - expected to be numerical!");
                }
                Proximity proximityAction = new Proximity(prdAction.getPRDBetween().getSourceEntity(), secondaryEntityDetails, prdAction.getPRDBetween().getTargetEntity(), prdAction.getPRDEnvDepth().getOf());

                for (PRDAction prdActionInThen : prdAction.getPRDActions().getPRDAction()) {
                    proximityAction.addActionToThen(createActionFromPrd(prdActionInThen));
                }

                resAction = proximityAction;
                break;
        }

        return resAction;
    }

    private SecondaryEntityDetails getSecondaryEntityDetails(PRDAction.PRDSecondaryEntity prdSecondaryEntity) {
        if (prdSecondaryEntity == null){
            return null;
        }

        if (currWorkingWorld.getEntityDefinitionByName(prdSecondaryEntity.getEntity()) == null) {
            throw new NotExistingEntityException(prdSecondaryEntity.getEntity(), null);
        }

        SecondaryEntityDetails res = new SecondaryEntityDetails();

        res.setName(prdSecondaryEntity.getEntity());

        if (prdSecondaryEntity.getPRDSelection().getCount().equalsIgnoreCase("ALL")){
            res.setMaxCount(null);
        }
        else{
            res.setMaxCount(Integer.parseInt(prdSecondaryEntity.getPRDSelection().getCount()));
        }

        if(prdSecondaryEntity.getPRDSelection().getPRDCondition() != null){
            res.setCondition(createConditionAction(prdSecondaryEntity.getEntity(), prdSecondaryEntity.getPRDSelection().getPRDCondition(), null));
        }

        return res;
    }

    private ConditionImpl createConditionAction(String actionEntityName, PRDCondition prdCondition, SecondaryEntityDetails secondaryEntityDetails) //ASSUMING ALL CONDITIONS CONTAIN THE SAME THEN AND ELSE
    {
        ConditionImpl resCondition;
        if (prdCondition.getSingularity().equals("single")) {
            ConditionOp conditionOp = null;
            switch (prdCondition.getOperator()) {
                case "=":
                    conditionOp = ConditionOp.EQUALS;
                    break;
                case "!=":
                    conditionOp = ConditionOp.NOTEQUALS;
                    break;
                case "bt":
                    conditionOp = ConditionOp.BT;
                    break;
                case "lt":
                    conditionOp = ConditionOp.LT;
                    break;
            }

            resCondition = new SingleCondition(prdCondition.getEntity(),secondaryEntityDetails, prdCondition.getProperty(), conditionOp, prdCondition.getValue());
        }
        else //singularity: multiple
        {
            MultipleCondition multipleCondition = new MultipleCondition(actionEntityName,secondaryEntityDetails, LogicalOperator.valueOf(prdCondition.getLogical().toUpperCase()));
            for (PRDCondition prdSubCondition : prdCondition.getPRDCondition()) {
                multipleCondition.addCondition(createConditionAction(actionEntityName, prdSubCondition, secondaryEntityDetails));
            }

            resCondition = multipleCondition;
        }

        return resCondition;
    }

    private void checkForValidIncreaseDecreaseArguments(PRDAction prdAction)
    {

        if (!isExistingPropertyInEntity(prdAction.getEntity(), prdAction.getProperty())) {
            throw new NotExistingPropertyException(prdAction.getProperty(), prdAction.getType(), prdAction.getEntity());
        }
        else if (!isNumericArg(prdAction.getEntity(), prdAction.getBy())) {
            throw new IllegalArgumentException("Argument: " + prdAction.getBy() + ", to action " + prdAction.getType() + " - expected to be numerical!");
        }
        else if (!isNumericPropertyInEntity(prdAction.getEntity(), prdAction.getProperty()))
        {
            throw new IllegalArgumentException("Property: " + prdAction.getProperty() + ", in action " + prdAction.getType() + " - expected to be numerical!");
        }
    }

    private boolean isNumericPropertyInEntity(String entityName, String propertyName) {
        PropertyType inputPropertyType = this.currWorkingWorld.getEntityDefinitionByName(entityName).getPropertyDefinitionByName(propertyName).getType();
        return (inputPropertyType == PropertyType.DECIMAL || inputPropertyType == PropertyType.FLOAT);
    }

    private boolean isExistingPropertyInEntity(String entityName, String propertyName) {
        return this.currWorkingWorld.getEntityDefinitionByName(entityName).getPropertyDefinitionByName(propertyName) != null;
    }

    private boolean isNumericArg(String mainEntityName, String arg) {
        if (arg.startsWith("environment")){
            String envVarName = arg.substring(arg.indexOf('(') + 1, arg.indexOf(')'));
            return (this.currWorkingWorld.getEnvironmentVariableDefByName(envVarName).getType() == PropertyType.DECIMAL
                    || this.currWorkingWorld.getEnvironmentVariableDefByName(envVarName).getType() == PropertyType.FLOAT);
        }
        else if(arg.startsWith("evaluate")){
            String innerArgument = arg.substring(arg.indexOf('(') + 1, arg.indexOf(')'));
            String entityName = innerArgument.substring(0, innerArgument.indexOf('.'));
            String propertyName = innerArgument.substring(innerArgument.indexOf('.') + 1);


            if(this.currWorkingWorld.getEntityDefinitionByName(entityName) == null){
                throw new NotExistingEntityException(mainEntityName, "evaluate expression");
            }
            return isExistingPropertyInEntity(entityName, propertyName) && isNumericPropertyInEntity(entityName, propertyName);
        }
        else if(arg.startsWith("random") || arg.startsWith("percent") || arg.startsWith("ticks")){
            return true;
        }
        else if(isExistingPropertyInEntity(mainEntityName, arg) && isNumericPropertyInEntity(mainEntityName, arg))
        {
            return true;
        }
        else {
            return isNumericStr(arg);
        }
    }

    private boolean isNumericStr(String str)
    {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
