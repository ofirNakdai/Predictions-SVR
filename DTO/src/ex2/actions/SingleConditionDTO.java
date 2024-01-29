package ex2.actions;

import engineAnswers.ActionDTO;

public class SingleConditionDTO extends ActionDTO {
    private final String firstArgExpression;
    private final String operator;
    private final String secondArgExpression;
    private final int numOfThenActions;
    private final int numOfElseActions;

    public SingleConditionDTO(String type, String mainEntityName, String secondaryEntityName, String firstArgExpression, String operator, String secondArgExpression, int numOfThenActions, int numOfElseActions) {
        super(type, mainEntityName, secondaryEntityName);
        this.firstArgExpression = firstArgExpression;
        this.operator = operator;
        this.secondArgExpression = secondArgExpression;
        this.numOfThenActions = numOfThenActions;
        this.numOfElseActions = numOfElseActions;
    }

    public String getFirstArgExpression() {
        return firstArgExpression;
    }

    public String getOperator() {
        return operator;
    }

    public String getSecondArgExpression() {
        return secondArgExpression;
    }

    public int getNumOfThenActions() {
        return numOfThenActions;
    }

    public int getNumOfElseActions() {
        return numOfElseActions;
    }

}
