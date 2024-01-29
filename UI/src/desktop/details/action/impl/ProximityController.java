package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.ProximityActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProximityController implements ActionControllerAPI {


    @FXML
    private Label mainEntLabel;

    @FXML
    private Label targetLabel;

    @FXML
    private Label secEntLabel;

    @FXML
    private Label ofLabel;

    @FXML
    private Label thenLabel;


    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        ProximityActionDTO proximityActionDTO = (ProximityActionDTO)actionDTO;
        mainEntLabel.setText(proximityActionDTO.getMainEntityName());
        if (proximityActionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(proximityActionDTO.getSecondaryEntityName());
        }
        targetLabel.setText(proximityActionDTO.getTargetEntityName());
        ofLabel.setText(proximityActionDTO.getOfExpression());
        thenLabel.setText(String.valueOf(proximityActionDTO.getNumOfThenActions()));
    }
}
