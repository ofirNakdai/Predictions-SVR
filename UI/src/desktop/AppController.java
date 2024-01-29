package desktop;

import desktop.details.DetailsController;
import desktop.execution.ExecutionController;
import desktop.header.HeaderController;
import desktop.results.ResultsController;
import engine.system.SystemEngine;
import engine.system.SystemEngineImpl;
import engineAnswers.pastSimulationDTO;
import ex2.runningSimulationDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AppController {
    private SystemEngine systemEngine = new SystemEngineImpl();

    @FXML
    private VBox headerComponent;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private GridPane detailsComponent;
    @FXML
    private DetailsController detailsComponentController;
    @FXML
    private GridPane executionComponent;
    @FXML
    private ExecutionController executionComponentController;
    @FXML
    private GridPane resultsComponent;
    @FXML
    private ResultsController resultsComponentController;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab detailsTab;

    @FXML
    private Tab newExecutionTab;

    @FXML
    private Tab resultsTab;

    private SimpleBooleanProperty isFileLoaded;

    public boolean getIsFileLoaded() {
        return isFileLoaded.get();
    }

    public boolean isAnimation() {
        return headerComponentController.isAnimation();
    }

    public void startRectangleAnimation(){
        headerComponentController.startRectangleAnimation();
    }

    public SimpleBooleanProperty isFileLoadedProperty() {
        return isFileLoaded;
    }

    public void setIsFileLoaded(boolean isFileLoaded) {
        this.isFileLoaded.set(isFileLoaded);
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && detailsComponentController != null && executionComponentController != null && resultsComponentController != null) {
            headerComponentController.setMainController(this);
            detailsComponentController.setMainController(this);
            executionComponentController.setMainController(this);
            resultsComponentController.setMainController(this);
        }
    }

    public void changeSkin(String skinName) {
        String skinPath = null ;
        scrollPane.getStylesheets().clear();
        if (skinName != null) {
            if (!skinName.equals("default")) {
//                skinPath = getClass().getResource("./stylesheets/" + skinName + ".css").toExternalForm();
                skinPath = getClass().getResource("/desktop/stylesheets/" + skinName + ".css").toExternalForm();
                scrollPane.getStylesheets().add(skinPath);
            }
        }

    }

    public AppController(){
        isFileLoaded = new SimpleBooleanProperty(false);
    }

    public void addDataToSimulationTreeView()
    {
        detailsComponentController.addDataToSimulationTreeView();
    }

    public SystemEngine getSystemEngine() {
        return systemEngine;
    }

    public void moveToResultsTab(pastSimulationDTO pastSimulationDTO){
//        tabPane.getSelectionModel().select(resultsTab);
        resultsComponentController.addItemToSimulationsList(pastSimulationDTO);
        tabPane.getSelectionModel().select(resultsTab);
    }
    public void moveToDetailsTab(){
        tabPane.getSelectionModel().select(detailsTab);
    }

    public void clearResultsTab() {
        resultsComponentController.clearExecutionList();
    }

    public void addDataToExecutionTab() {
        executionComponentController.addDataToEntitiesTable();
        executionComponentController.addDataToEnvVarsListView();
        executionComponentController.setGridSize(systemEngine.getGridSize());
    }

    public void showPopUpAlert(String title, String HeaderText, String setContentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(HeaderText);
        alert.setContentText(setContentText);
        alert.initOwner(this.headerComponent.getScene().getWindow());
        alert.showAndWait();
    }

    public void reRunSimulation(int simulationID){
        executionComponentController.setDetailsToReRun(simulationID);
        tabPane.getSelectionModel().select(newExecutionTab);
    }
}
