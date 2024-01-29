package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.MultipleConditionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MultipleConditionController implements ActionControllerAPI {

    @FXML
    private Label mainEntLabel;

    @FXML
    private Label secEntLabel;

    @FXML
    private Label opertarorLabel;

    @FXML
    private Label conditionsLabel;

    @FXML
    private Label thenLabel;

    @FXML
    private Label elseLabel;

    public MultipleConditionController(){

}

    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        MultipleConditionDTO multipleConditionDTO = (MultipleConditionDTO)actionDTO;
        mainEntLabel.setText(multipleConditionDTO.getMainEntityName());
        if (multipleConditionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(multipleConditionDTO.getSecondaryEntityName());
        }
        opertarorLabel.setText(multipleConditionDTO.getLogicalOperator());
        conditionsLabel.setText(String.valueOf(multipleConditionDTO.getNumOfConditions()));
        thenLabel.setText(String.valueOf(multipleConditionDTO.getNumOfThenActions()));
        elseLabel.setText(String.valueOf(multipleConditionDTO.getNumOfElseActions()));
    }
}
