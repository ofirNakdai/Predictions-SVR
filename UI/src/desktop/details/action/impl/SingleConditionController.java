package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.SingleConditionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SingleConditionController implements ActionControllerAPI {

    @FXML
    private Label mainEntLabel;

    @FXML
    private Label secEntLabel;

    @FXML
    private Label arg1Label;

    @FXML
    private Label arg2Label;

    @FXML
    private Label opertarorLabel;

    @FXML
    private Label thenLabel;

    @FXML
    private Label elseLabel;


    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        SingleConditionDTO singleConditionDTO = (SingleConditionDTO)actionDTO;
        mainEntLabel.setText(singleConditionDTO.getMainEntityName());
        if (singleConditionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(singleConditionDTO.getSecondaryEntityName());
        }
        opertarorLabel.setText(singleConditionDTO.getOperator());
        arg1Label.setText(singleConditionDTO.getFirstArgExpression());
        arg2Label.setText(singleConditionDTO.getSecondArgExpression());
        thenLabel.setText(String.valueOf(singleConditionDTO.getNumOfThenActions()));
        elseLabel.setText(String.valueOf(singleConditionDTO.getNumOfElseActions()));
    }
}
