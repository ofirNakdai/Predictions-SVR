package desktop.results.simulations.running;

import desktop.AppController;
import desktop.results.simulations.simulationControllerAPI;
import engine.world.SimulationStatus;
import engineAnswers.ActiveEnvVarDTO;
import engineAnswers.EntityCountDTO;
import ex2.runningSimulationDTO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.Collection;

public class RunningSimulationController implements simulationControllerAPI {

    // ~~~~~~~~ Entity Table view ~~~~~~~~
    @FXML
    private TableView<EntityCountDTO> entityTableView;

    @FXML
    private TableColumn<EntityCountDTO, String> entityColumn;

    @FXML
    private TableColumn<EntityCountDTO, Integer> entityCountAtBegColumn;

    @FXML
    private TableColumn<EntityCountDTO, Integer> entityCountAtEndColumn;

    // ~~~~~~~~ ^ Entity Table view ^ ~~~~~~~~

    // ~~~~~~~~ Env Var Table view ~~~~~~~~

    @FXML
    private TableView<ActiveEnvVarDTO> envVarsTableView;

    @FXML
    private TableColumn<ActiveEnvVarDTO, String> envVarNameCol;

    @FXML
    private TableColumn<ActiveEnvVarDTO, String> envVarValueCol;

    // ~~~~~~~~ ^ Env Var Table view ^ ~~~~~~~~

    @FXML
    private Label statusLBL;

    @FXML
    private Label tickLBL;

    @FXML
    private Label timeLBL;

    @FXML
    private ProgressBar ticksProgressBar;

    @FXML
    private Label ticksPercentLBL;

    @FXML
    private ProgressBar timeProgressBar;

    @FXML
    private Label timePercentLBL;

    @FXML
    private Label errorLBL;

    @FXML
    private HBox timeProgressHBox;

    @FXML
    private HBox ticksProgressHBox;

    @FXML
    private Button pauseBTN;

    @FXML
    private Button stopBTN;

    @FXML
    private Button resumeBTN;
    @FXML
    private Button nextBTN;

    private int simulationID;
    private SimpleIntegerProperty currTick = new SimpleIntegerProperty(0);
    private SimpleLongProperty runTime = new SimpleLongProperty(0);
    private SimpleStringProperty status = new SimpleStringProperty();

    private SimpleBooleanProperty showingTimeProgress = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty showingTicksProgress = new SimpleBooleanProperty(false);
    private SimpleDoubleProperty ticksProgress = new SimpleDoubleProperty(0);
    private SimpleDoubleProperty timeProgress = new SimpleDoubleProperty(0);
    private AppController mainController = null;
    boolean isCurrSimulationTerminatesByUser = false; //TODO: need this now?
    private SimpleBooleanProperty disablePause = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty disableStop = new SimpleBooleanProperty(true);
    private SimpleBooleanProperty disableResume = new SimpleBooleanProperty(true);


    @Override
    public void setSimulationID(int simulationID){
        this.simulationID = simulationID;
    }
    @Override
    public void setDataFromDTO(runningSimulationDTO simulationDTO) {
        currTick.set(simulationDTO.getCurrentTick());
        runTime.set(simulationDTO.getCurrentSeconds());
        status.set(simulationDTO.getStatus());

        bindDataToEntityTableView(simulationDTO);

        boolean isTicksProgress = simulationDTO.getTotalTicks() != null;
        boolean isTimeProgress = simulationDTO.getTotalSeconds() != null;

        if(isTimeProgress) {
            this.showingTimeProgress.set(true);
            setTimeProgressBar(simulationDTO.getCurrentSeconds(), simulationDTO.getTotalSeconds());
        }
        else
            this.showingTimeProgress.set(false);

        if(isTicksProgress) {
            this.showingTicksProgress.set(true);
            setTicksProgressBar(simulationDTO.getCurrentTick(), simulationDTO.getTotalTicks());
        }
        else
            this.showingTicksProgress.set(false);

        calcDisableValueToAllBTNs(simulationDTO.getStatus());
        this.isCurrSimulationTerminatesByUser = simulationDTO.isTerminateByUser();

    }
    @Override
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setExtraDetails() {
        setEnvVarsTable();
    }

    private void setTicksProgressBar(int currTick, int totalTicks){
        double progress = (double) currTick /totalTicks;
        ticksProgressBar.setProgress(progress);
        ticksProgress.set(progress  * 100);
    }

    private void setTimeProgressBar(long currTime, long totalTime){
        double progress = (double) currTime /totalTime;
        timeProgressBar.setProgress(progress);
        timeProgress.set(progress * 100);
    }

    private void bindDataToEntityTableView(runningSimulationDTO simulationDTO){
        Collection<EntityCountDTO> entityCountDTOCollection = simulationDTO.getEntityCountDTOS();
        ObservableList<EntityCountDTO> data = FXCollections.observableArrayList();
        data.clear();
        data.addAll(entityCountDTOCollection);
        entityTableView.setItems(data);
    }

    private void calcDisableValueToAllBTNs(String status){
        switch (SimulationStatus.valueOf(status)) {
            case TERMINATED:
            case CREATED:
                disablePause.set(true);
                disableStop.set(true);
                disableResume.set(true);
                break;
            case PAUSED:
                disablePause.set(true);
                disableStop.set(false);
                disableResume.set(false);
                break;
            case RUNNING:
                disablePause.set(false);
                disableStop.set(false);
                disableResume.set(true);
                break;
        }

    }
    @FXML
    public void initialize() {
        entityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        entityCountAtBegColumn.setCellValueFactory(new PropertyValueFactory<>("populationAtStart"));
        entityCountAtEndColumn.setCellValueFactory(new PropertyValueFactory<>("populationAtEnd"));
        entityCountAtBegColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        entityCountAtEndColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        this.tickLBL.textProperty().bind(currTick.asString());
        this.timeLBL.textProperty().bind(runTime.asString());
        this.statusLBL.textProperty().bind(status);
        this.ticksPercentLBL.textProperty().bind(ticksProgress.asString("%.2f").concat(" %"));
        this.timePercentLBL.textProperty().bind(timeProgress.asString("%.2f").concat(" %"));
        this.timeProgressHBox.visibleProperty().bind(showingTimeProgress);
        this.ticksProgressHBox.visibleProperty().bind(showingTicksProgress);

        this.pauseBTN.disableProperty().bind(disablePause);
        this.resumeBTN.disableProperty().bind(disableResume);
        this.stopBTN.disableProperty().bind(disableStop);


    }

    @FXML
    void pauseButtonPressed(ActionEvent event) {
        mainController.getSystemEngine().pauseSimulation(simulationID);
    }

    @FXML
    void resumeButtonPressed(ActionEvent event) {
        mainController.getSystemEngine().resumeSimulation(simulationID);
    }
    @FXML
    void stopButtonPressed(ActionEvent event) {
        mainController.getSystemEngine().stopSimulation(simulationID);
    }

    public void setEnvVarsTable(){
        envVarNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        envVarValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        Collection<ActiveEnvVarDTO> envVarCollection = mainController.getSystemEngine().getActiveEnvVarsDto(simulationID);

        ObservableList<ActiveEnvVarDTO> data = FXCollections.observableArrayList();
        data.clear();
        data.addAll(envVarCollection);
        envVarsTableView.setItems(data);
    }
}
