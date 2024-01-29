package ex2.actions;

import engineAnswers.ActionDTO;

public class MultipleConditionDTO extends ActionDTO {
    private final String logicalOperator;
    private final int numOfConditions;
    private final int numOfThenActions;
    private final int numOfElseActions;

    public MultipleConditionDTO(String type, String mainEntityName, String secondaryEntityName, String logicalOperator, int numOfConditions, int numOfThenActions, int numOfElseActions) {
        super(type, mainEntityName, secondaryEntityName);
        this.logicalOperator = logicalOperator;
        this.numOfConditions = numOfConditions;
        this.numOfThenActions = numOfThenActions;
        this.numOfElseActions = numOfElseActions;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public int getNumOfConditions() {
        return numOfConditions;
    }

    public int getNumOfThenActions() {
        return numOfThenActions;
    }

    public int getNumOfElseActions() {
        return numOfElseActions;
    }
}
