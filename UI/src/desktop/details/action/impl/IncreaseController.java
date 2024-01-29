package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.IncreaseActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class IncreaseController implements ActionControllerAPI {

    @FXML
    private Label typeLabel;

    @FXML
    private Label mainEntLabel;

    @FXML private Label secEntLabel;

    @FXML
    private Label propertyLabel;

    @FXML
    private Label byLabel;

    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        IncreaseActionDTO increaseActionDTO = (IncreaseActionDTO)actionDTO;
        typeLabel.setText(increaseActionDTO.getType());
        mainEntLabel.setText(increaseActionDTO.getMainEntityName());
        if (increaseActionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(increaseActionDTO.getSecondaryEntityName());
        }
        propertyLabel.setText(increaseActionDTO.getPropertyName());
        byLabel.setText(increaseActionDTO.getByExpression());
    }
}
