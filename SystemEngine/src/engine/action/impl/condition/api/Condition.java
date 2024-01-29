package engine.action.impl.condition.api;

import engine.action.api.Action;
import engine.context.Context;

public interface Condition {
    boolean evaluateCondition(Context context);
    void addActionToThen(Action actionToAdd);
    void addActionToElse(Action actionToAdd);

}
