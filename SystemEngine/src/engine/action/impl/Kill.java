package engine.action.impl;

import engine.action.api.AbstractAction;
import engine.action.api.ActionType;
import engine.context.Context;
import engine.entity.EntityInstance;
import engine.world.factory.SecondaryEntityDetails;

public class Kill extends AbstractAction {

    public Kill(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails){
        super(ActionType.KILL, mainEntityName, secondaryEntityDetails);
    }

    @Override
    public void Run(Context context) {
        EntityInstance mainEntity = getMainEntityInstance(context);
        context.getEntityInstanceManager().killEntity(mainEntity);
    }
}
