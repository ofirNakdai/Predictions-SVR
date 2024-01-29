package ex2.actions;

import engineAnswers.ActionDTO;

public class IncreaseActionDTO extends ActionDTO {
    private final String propertyName;
    private final String byExpression;

    public IncreaseActionDTO(String type, String mainEntityName, String secondaryEntityName, String propertyName, String byExpression) {
        super(type, mainEntityName, secondaryEntityName);
        this.propertyName = propertyName;
        this.byExpression = byExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getByExpression() {
        return byExpression;
    }
}
