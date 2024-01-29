package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.SetActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class KillController implements ActionControllerAPI {

    @FXML
    private Label mainEntLabel;

    @FXML
    private Label secEntLabel;

    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        mainEntLabel.setText(actionDTO.getMainEntityName());
        if (actionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(actionDTO.getSecondaryEntityName());
        }
    }
}
