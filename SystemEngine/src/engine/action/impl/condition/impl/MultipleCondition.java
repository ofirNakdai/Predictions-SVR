package engine.action.impl.condition.impl;

import engine.action.impl.condition.LogicalOperator;
import engine.action.impl.condition.api.Condition;
import engine.context.Context;
import engine.world.factory.SecondaryEntityDetails;

import java.util.ArrayList;
import java.util.List;

public class MultipleCondition extends ConditionImpl implements Condition {
    final List<Condition> conditionList;
    final LogicalOperator logicalOperator;

    public MultipleCondition(String mainEntityName, SecondaryEntityDetails secondaryEntityDetails, LogicalOperator logicalOperator){
        super(mainEntityName, secondaryEntityDetails);
        this.conditionList = new ArrayList<>();
        this.logicalOperator = logicalOperator;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public int getNumOfConditions(){
        return conditionList.size();
    }

    @Override
    public void Run(Context context) {
        if (evaluateCondition(context)) {
            invokeThenActions(context);
        } else {
            invokeElseActions(context);
        }
    }

    @Override
    public boolean evaluateCondition(Context context) {
        boolean res = logicalOperator == LogicalOperator.AND;

        for(Condition condition : this.conditionList){
            switch (logicalOperator){
                case OR:
                    res = res || condition.evaluateCondition(context);
                    break;
                case AND:
                    res = res && condition.evaluateCondition(context);
                    break;
            }
        }
        return res;
    }

    public void addCondition(Condition conditionToAdd){
        this.conditionList.add(conditionToAdd);
    }

}
