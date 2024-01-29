package engine.action.api;

import engine.context.Context;
import engine.entity.EntityInstance;
import engine.world.factory.SecondaryEntityDetails;

import java.io.Serializable;

public abstract class AbstractAction implements Action, Serializable {
    private final ActionType actionType;
    protected final String mainEntityName;
    protected final SecondaryEntityDetails secondaryEntityDetails;



    protected AbstractAction(ActionType actionType, String mainEntityName, SecondaryEntityDetails secondaryEntityDetails) {
        this.actionType = actionType;
        this.mainEntityName = mainEntityName;
        this.secondaryEntityDetails = secondaryEntityDetails;
    }

    @Override
    public String getMainEntityName() {return mainEntityName;}

    @Override
    public ActionType getActionType() { return actionType; }

    @Override
    public SecondaryEntityDetails getSecondaryEntityDetails(){ return secondaryEntityDetails;}

    protected EntityInstance getMainEntityInstance(Context context){
        if (mainEntityName.equals(context.getPrimaryEntityInstance().getName()))
            return context.getPrimaryEntityInstance();
        else if (context.getSecondaryEntityInstance() != null && mainEntityName.equals(context.getSecondaryEntityInstance().getName()))
            return context.getSecondaryEntityInstance();
        else {
            throw new RuntimeException("The entity: " + mainEntityName + " is not in the context of the action, in action: " + actionType);
        }
    }
}
