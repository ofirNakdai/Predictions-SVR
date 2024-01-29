package desktop.execution.envvar.api;

import desktop.AppController;
import engineAnswers.PropertyDTO;

public interface EnvVarControllerAPI {
    void setDataFromDTO(PropertyDTO envVarDTO);
    PropertyDTO createEnvVarDTO();
    void setMainController(AppController mainController);
    void setValue(String value);
}
