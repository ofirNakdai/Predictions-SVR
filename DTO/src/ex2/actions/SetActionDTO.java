package ex2.actions;

import engineAnswers.ActionDTO;

public class SetActionDTO extends ActionDTO {
    private final String propertyName;
    private final String valueExpression;

    public SetActionDTO(String type, String mainEntityName, String secondaryEntityName, String propertyName, String valueExpression) {
        super(type, mainEntityName, secondaryEntityName);
        this.propertyName = propertyName;
        this.valueExpression = valueExpression;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getValueExpression() {
        return valueExpression;
    }
}
