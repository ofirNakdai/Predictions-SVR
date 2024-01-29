package desktop.details.action.impl;

import desktop.details.action.api.ActionControllerAPI;
import engineAnswers.ActionDTO;
import ex2.actions.CalculationActionDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

    public class CalculationController implements ActionControllerAPI {

        @FXML private Label calcTypeLabel;

        @FXML private Label mainEntLabel;

        @FXML private Label secEntLabel;

        @FXML private Label resPropLabel;

        @FXML private Label arg1Label;

        @FXML private Label arg2Label;

        public CalculationController(){

    }

        @Override
        public void setDataFromDTO(ActionDTO actionDTO) {
            CalculationActionDTO calculationActionDTO = (CalculationActionDTO)actionDTO;
            calcTypeLabel.setText(calculationActionDTO.getCalcType());
            mainEntLabel.setText(calculationActionDTO.getMainEntityName());
            if (calculationActionDTO.getSecondaryEntityName() == null) {
                secEntLabel.setText("--Undefined--");
            } else {
                secEntLabel.setText(calculationActionDTO.getSecondaryEntityName());
            }
            resPropLabel.setText(calculationActionDTO.getPropertyName());
            arg1Label.setText(calculationActionDTO.getArg1Expression());
            arg2Label.setText(calculationActionDTO.getArg2Expression());
        }
    }
