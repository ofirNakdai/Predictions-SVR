package ex2.actions;

import engineAnswers.ActionDTO;

public class ProximityActionDTO extends ActionDTO {
    private final String targetEntityName;
    private final String ofExpression;
    private final int numOfThenActions;

    public ProximityActionDTO(String type, String mainEntityName, String secondaryEntityName, String targetEntityName, String ofExpression, int numOfThenActions) {
        super(type, mainEntityName, secondaryEntityName);
        this.targetEntityName = targetEntityName;
        this.ofExpression = ofExpression;
        this.numOfThenActions = numOfThenActions;
    }

    public String getTargetEntityName() {
        return targetEntityName;
    }

    public String getOfExpression() {
        return ofExpression;
    }

    public int getNumOfThenActions() {
        return numOfThenActions;
    }
}
