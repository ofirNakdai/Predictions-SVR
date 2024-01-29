package engine.action.impl;

import engine.action.api.AbstractAction;
import engine.action.api.Action;
import engine.action.api.ActionType;
import engine.context.Context;
import engine.context.ContextImpl;
import engine.entity.EntityInstance;
import engine.expression.Expression;
import engine.property.PropertyType;
import engine.world.factory.SecondaryEntityDetails;

import java.util.ArrayList;
import java.util.List;

public class Proximity extends AbstractAction {
    private final String ofExpression;
    private final String targetEntityName;
    protected final List<Action> thenActions;
    public Proximity(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, String targetEntityName, String ofExpression){
        super(ActionType.PROXIMITY, mainEntityName, secondaryEntityDetails);
        this.ofExpression = ofExpression;
        this.targetEntityName = targetEntityName;
        thenActions = new ArrayList<>();
    }

    public String getOfExpression() {
        return ofExpression;
    }

    public String getTargetEntityName() {
        return targetEntityName;
    }

    public int getNumOfThenActions(){
        return thenActions.size();
    }

    public void addActionToThen(Action actionToAdd) {
        this.thenActions.add(actionToAdd);
    }

    @Override
    public void Run(Context context) {
        Expression ofAsExpression = new Expression(ofExpression, context);
        String ofFromExpression = ofAsExpression.praseExpressionToValueString(PropertyType.FLOAT);

        EntityInstance sourceEntityInstance = getMainEntityInstance(context);

        for (EntityInstance targetEntity : context.getEntityInstanceManager().getInstancesListByName(targetEntityName)) {
            int depth = Float.valueOf(ofFromExpression).intValue(); //MAYBE ROUND UP/DOWN ACCORDINGLY
            if (context.getEntityInstanceManager().isEnt1NearEnt2(sourceEntityInstance, targetEntity, depth)) {
                //INVOKE ACTIONS:
                for (Action action : thenActions) {
                    try {
                        action.Run(new ContextImpl(sourceEntityInstance, targetEntity, context.getEntityInstanceManager(), context.getActiveEnvironmentVariables(), context.getCurrentTick()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                break; //found entity and invoked with her - no need to look for more
            }
        }
    }



}
