package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.SetActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SetController implements ActionControllerAPI {

    @FXML
    private Label mainEntLabel;

    @FXML
    private Label secEntLabel;

    @FXML
    private Label propertyLabel;

    @FXML
    private Label valueLabel;


    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        SetActionDTO setActionDTO = (SetActionDTO)actionDTO;
        mainEntLabel.setText(setActionDTO.getMainEntityName());
        if (setActionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(setActionDTO.getSecondaryEntityName());
        }
        propertyLabel.setText(setActionDTO.getPropertyName());
        valueLabel.setText(setActionDTO.getValueExpression());
    }
}
