package desktop.results;

import desktop.AppController;
import desktop.results.simulations.running.RunningSimulationController;
import desktop.results.simulations.simulationControllerAPI;
import engineAnswers.pastSimulationDTO;
import ex2.runningSimulationDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ResultsController {
    @FXML
    private ListView<pastSimulationDTO> executionList;


    private AppController mainController;
    Thread dataPullingThread;
    int currentChosenSimulationID = -1;

    @FXML
    private HBox simulationHBox;

    private Map<Integer, Node> id2simulationComponent = new HashMap<>();
    private Map<Integer, simulationControllerAPI> id2simulationController = new HashMap<>();

    boolean terminatedIsOn = false; // true if the terminated component is on the screen

    private volatile boolean stopRequested = false;

    public void addItemToSimulationsList(pastSimulationDTO pastSimulationDTO) { //TO RUN WHEN CLICK RUN
        executionList.getItems().add(pastSimulationDTO);
        stopRequested = false;
        executionList.getSelectionModel().select(pastSimulationDTO);
    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void initialize(){


        executionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            setChosenID(newValue);
//            if(this.id2simulationController.containsKey(newValue.getId())){
//                this.id2simulationController.get(newValue.getId()).setCurrentChosenSimulationID(newValue.getId());
//            }
        });

//        dataPullingThread = new Thread(() -> {        //WORKED LIKE THAT ONLY IN DEBUG! DONT KNOW WHY NOT IN REAL....
//            while (true) {
//                if (currentChosenSimulationID != -1) { // if there is chosen option
//                    try {
//                        sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(!stopRequested) {
//                        runningSimulationDTO testINFO = mainController.getSystemEngine().pullData(currentChosenSimulationID);
//                        // Update UI using Platform.runLater()
//                        Platform.runLater(() -> {
//                            // Update UI with the collected data:
//                            if (testINFO.getStatus().equals("TERMINATED")) {
//                                if (!terminatedIsOn) {
//                                    showTerminatedSimulationDetails(testINFO, currentChosenSimulationID);
//                                    terminatedIsOn = true;
//                                }
//                            } else {
//                                showRunningSimulationDetails(testINFO, currentChosenSimulationID);
//                                terminatedIsOn = false;
//                            }
//
//                        });
//                    }
//                }
//            }
//        });
    }
    private void setChosenID(pastSimulationDTO newValue) {

        if(newValue == null){
            currentChosenSimulationID = -1;
        }
        else{
            if(currentChosenSimulationID != newValue.getId())
                terminatedIsOn = false;
            this.currentChosenSimulationID = newValue.getId();
            if (dataPullingThread == null || !dataPullingThread.isAlive()){
                dataPullingThread = new Thread(() -> {
                    while (!stopRequested) {
                        if (currentChosenSimulationID != -1) { // if there is chosen option
                            try {
                                sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!stopRequested) {
                                if (!terminatedIsOn) {
                                    runningSimulationDTO testINFO = mainController.getSystemEngine().pullData(currentChosenSimulationID);
                                    // Update UI using Platform.runLater()
                                    Platform.runLater(() -> {
                                        // Update UI with the collected data:
                                        if (testINFO.getStatus().equals("TERMINATED")) {
                                            showTerminatedSimulationDetails(testINFO, currentChosenSimulationID);
                                            terminatedIsOn = true;
                                        } else {
                                            showRunningSimulationDetails(testINFO, currentChosenSimulationID);
                                            terminatedIsOn = false;
                                        }

                                    });
                                }
                            }
                        }
                    }
                });
                dataPullingThread.start();
            }
        }
    }
    private void showRunningSimulationDetails(runningSimulationDTO simulationDTO, int simulationID) {
        if(simulationID != -1) {
            if (!id2simulationComponent.containsKey(simulationID)) {
                createRunningSimulationComponent(simulationDTO, simulationID);
            } else {
                id2simulationController.get(simulationID).setDataFromDTO(simulationDTO);
            }

            if(!simulationHBox.getChildren().contains(id2simulationComponent.get(simulationID))) {
                if (!simulationHBox.getChildren().isEmpty()) {
                    simulationHBox.getChildren().clear();
                }
                simulationHBox.getChildren().add(id2simulationComponent.get(simulationID));
            }
        }

    }
    private void showTerminatedSimulationDetails(runningSimulationDTO simulationDTO, int simulationID) {
        if (!id2simulationComponent.containsKey(simulationID)) {
            createTerminatedSimulationComponent(simulationDTO, simulationID);
        }

        if (id2simulationController.get(simulationID) instanceof RunningSimulationController) {
            createTerminatedSimulationComponent(simulationDTO, simulationID);
        }

        if (!simulationHBox.getChildren().isEmpty()) {
            simulationHBox.getChildren().clear();
        }

        simulationHBox.getChildren().add(id2simulationComponent.get(simulationID));
    }
    private void createTerminatedSimulationComponent(runningSimulationDTO simulationDTO, int simulationID) {
        try {
            FXMLLoader loaderComponent = new FXMLLoader();
            Node component = null;
            simulationControllerAPI simulationController = null;
            loaderComponent.setLocation(getClass().getResource("/desktop/results/simulations/terminated/terminatedSimulations.fxml"));


            component = loaderComponent.load();
            simulationController = loaderComponent.getController();
            simulationController.setMainController(this.mainController);
            simulationController.setSimulationID(simulationID);
            simulationController.setDataFromDTO(simulationDTO);
            id2simulationController.put(simulationID, simulationController);
            id2simulationComponent.put(simulationID, component);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void createRunningSimulationComponent(runningSimulationDTO simulationDTO, int simulationID) {
        try {
            FXMLLoader loaderComponent = new FXMLLoader();
            Node component = null;
            simulationControllerAPI simulationController = null;
            loaderComponent.setLocation(getClass().getResource("/desktop/results/simulations/running/runningProgressedSimulation.fxml"));


            component = loaderComponent.load();
            simulationController = loaderComponent.getController();
            simulationController.setMainController(this.mainController);
            simulationController.setSimulationID(simulationID);
            simulationController.setDataFromDTO(simulationDTO);
            simulationController.setExtraDetails();
            id2simulationController.put(simulationID, simulationController);
            id2simulationComponent.put(simulationID, component);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearExecutionList() {
        stopRequested = true;
//        dataPullingThread.interrupt();// added at last, not sure if needed
        executionList.getItems().clear();
        if (!simulationHBox.getChildren().isEmpty()) {
            simulationHBox.getChildren().clear();
        }
        id2simulationController.clear();
        id2simulationComponent.clear();
        currentChosenSimulationID = -1;

    }
}
