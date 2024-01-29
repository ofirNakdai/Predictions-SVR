package engine.action.api;

import engine.action.impl.condition.api.Condition;
import engine.context.Context;
import engine.world.factory.SecondaryEntityDetails;

public interface Action {
    void Run(Context context);
    ActionType getActionType();
    String getMainEntityName();
    SecondaryEntityDetails getSecondaryEntityDetails();
}
