package desktop.results.simulations;

import desktop.AppController;
import ex2.runningSimulationDTO;

public interface simulationControllerAPI {
    void setSimulationID(int simulationID);

    void setDataFromDTO(runningSimulationDTO simulationDTO);

    void setMainController(AppController mainController);

    void setExtraDetails();
}
