package desktop.execution.envvar.impl;

import desktop.AppController;
import desktop.execution.envvar.api.EnvVarControllerAPI;
import engineAnswers.PropertyDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FloatEnvVarController implements EnvVarControllerAPI {


    @FXML private Label nameLabel;
    @FXML private Label typeLabel;
    @FXML private Label rangeLabel;
    @FXML private TextField valueTextField;
    private AppController mainController;
    private Float from;
    private Float to;
    private String oldTextValue;

    @Override
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setValue(String value) {
        valueTextField.setText(value);
    }

    @Override
    public void setDataFromDTO(PropertyDTO envVarDTO) {
        nameLabel.setText(envVarDTO.getName());
        typeLabel.setText(envVarDTO.getType());
        from = envVarDTO.getFrom().floatValue();
        to = envVarDTO.getTo().floatValue();
        rangeLabel.setText( from + " to " + to);
    }


    @FXML
    private void initialize(){
        valueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Float.parseFloat(newValue);
                valueTextField.setText(newValue);
            }
            catch (NumberFormatException ex) {
                if (!newValue.equals("")){
                    valueTextField.setText(oldValue);
                }
            }
        });

        valueTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !isValidInput()) { // Focus lost (user finished editing) and the value invalid
                mainController.showPopUpAlert("Value Out Of Range", "In environment variable " +  nameLabel.getText(),  "The value: " + valueTextField.getText() + " is not in the range: " + this.from + " to " + this.to + System.lineSeparator() + "going back to the previous value");
                valueTextField.setText(this.oldTextValue);
            }
            else{
                this.oldTextValue = valueTextField.getText();
            }
        });
    }

    private boolean isValidInput() {
        try {
            float enteredValue = Float.parseFloat(valueTextField.getText());
            if (enteredValue < this.from || enteredValue > this.to) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return valueTextField.getText().equals("");
        }
    }
    @Override
    public PropertyDTO createEnvVarDTO(){

        return new PropertyDTO(nameLabel.getText(), typeLabel.getText(), from, to, valueTextField.getText().isEmpty(), valueTextField.getText());

    }
}
