package desktop.execution.envvar.impl;

import desktop.AppController;
import desktop.execution.envvar.api.EnvVarControllerAPI;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import engineAnswers.PropertyDTO;

public class BooleanEnvVarController implements EnvVarControllerAPI {


    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private ComboBox<String> valueComboBox;
    private AppController mainController;

    @Override
    public void setDataFromDTO(PropertyDTO envVarDTO) {
        nameLabel.setText(envVarDTO.getName());
        typeLabel.setText(envVarDTO.getType());
    }



    @FXML
    private void initialize(){
        valueComboBox.getItems().addAll("Random", "True", "False");
        valueComboBox.setValue("Random");
    }

    @Override
    public PropertyDTO createEnvVarDTO(){

        return new PropertyDTO(nameLabel.getText(), typeLabel.getText(), null, null, valueComboBox.getValue().equals("Random"), valueComboBox.getValue().toLowerCase());

    }

    @Override
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setValue(String value) {
        valueComboBox.setValue(value);
    }
}
