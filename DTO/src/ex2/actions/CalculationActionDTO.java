package ex2.actions;

import engineAnswers.ActionDTO;

public class CalculationActionDTO extends ActionDTO {
    private final String propertyName;
    private final String arg1Expression;
    private final String arg2Expression;
    private final String calcType;

    public CalculationActionDTO(String type, String mainEntityName, String secondaryEntityName, String calcType, String propertyName, String arg1Expression, String arg2Expression) {
        super(type, mainEntityName, secondaryEntityName);
        this.propertyName = propertyName;
        this.arg1Expression = arg1Expression;
        this.arg2Expression = arg2Expression;
        this.calcType = calcType;
    }

    public String getArg1Expression() {
        return arg1Expression;
    }

    public String getArg2Expression() {
        return arg2Expression;
    }

    public String getCalcType() {
        return calcType;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
