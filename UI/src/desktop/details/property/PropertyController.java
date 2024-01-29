package desktop.details.property;

import engineAnswers.PropertyDTO;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class PropertyController {
    @FXML
    private Label rangeTitleLabel;

    @FXML
    private Label IsRandomTitleLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label rangeLabel;

    @FXML
    private CheckBox IsRandomCB;
    public void setDataFromDTO(PropertyDTO propertyDTO, boolean isEnvVar) {
        nameLabel.setText(propertyDTO.getName());
        typeLabel.setText(propertyDTO.getType());
        if (propertyDTO.getFrom() != null) { //there is range
            if (propertyDTO.getType().equals("DECIMAL")) {
                rangeLabel.setText(propertyDTO.getFrom().intValue() + " to " + propertyDTO.getTo().intValue());
            } else {
                rangeLabel.setText(propertyDTO.getFrom() + " to " + propertyDTO.getTo());
            }
        }
        else{
            rangeTitleLabel.setVisible(false);
            rangeLabel.setVisible(false);
        }

        if (isEnvVar) {
            IsRandomTitleLabel.setVisible(false);
            IsRandomCB.setVisible(false);
        } else {
            IsRandomCB.setSelected(propertyDTO.isInitialisedRandomly());
        }
    }
}
