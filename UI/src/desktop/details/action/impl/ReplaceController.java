package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.ReplaceActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReplaceController implements ActionControllerAPI {


    @FXML
    private Label mainEntLabel;

    @FXML
    private Label createLabel;

    @FXML
    private Label secEntLabel;

    @FXML
    private Label modeLabel;


    @Override
    public void setDataFromDTO(ActionDTO actionDTO) {
        ReplaceActionDTO replaceActionDTO = (ReplaceActionDTO)actionDTO;
        mainEntLabel.setText(replaceActionDTO.getMainEntityName());
        if (replaceActionDTO.getSecondaryEntityName() == null) {
            secEntLabel.setText("--Undefined--");
        } else {
            secEntLabel.setText(replaceActionDTO.getSecondaryEntityName());
        }
        createLabel.setText(replaceActionDTO.getEntityToCreateName());
        modeLabel.setText(replaceActionDTO.getCreationMode());
    }
}
