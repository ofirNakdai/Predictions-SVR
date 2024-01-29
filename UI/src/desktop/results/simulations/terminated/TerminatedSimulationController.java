package desktop.results.simulations.terminated;

import desktop.AppController;
import desktop.results.simulations.terminated.statistics.HistogramController;
import engine.world.TerminationReason;
import engineAnswers.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import desktop.results.simulations.simulationControllerAPI;
import ex2.runningSimulationDTO;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;

public class TerminatedSimulationController implements simulationControllerAPI
{
    private int currentChosenSimulationID;

    // ~~~~~~~~ Table view ~~~~~~~~
    @FXML
    private TableView<EntityCountDTO> entityTableView;

    @FXML
    private TableColumn<EntityCountDTO, String> entityColumn;

    @FXML
    private TableColumn<EntityCountDTO, Integer> entityCountAtBegColumn;

    @FXML
    private TableColumn<EntityCountDTO, Integer> entityCountAtEndColumn;

    // ~~~~~~~~ ^ Table view ^ ~~~~~~~~

    @FXML
    private Label statusLBL;

    @FXML
    private Label tickLBL;

    @FXML
    private Label timeLBL;

    @FXML
    private Label reasonLBL;

    @FXML
    private Label resultLBL;

    // ~~~~~~~~ Line Chart ~~~~~~~~
    @FXML
    private NumberAxis ticsAxis;
    @FXML
    private NumberAxis countAxis;
    @FXML
    private LineChart<Number, Number> entitiesPopulationLC;
    // ~~~~~~~~ ^ Line Chart ^ ~~~~~~~~

    @FXML
    private ComboBox<String> entityComboBox = new ComboBox<>();
    private String selectedEntity = null;

    @FXML
    private ComboBox<String> propertyComboBox = new ComboBox<>();
    private String selectedProperty = null;

    @FXML
    private ComboBox<String> functionComboBox;
    private String selectedFunction = null;

    @FXML
    private Button rerunBTN;

    @FXML
    private VBox resultVBox;

    private AppController mainController;

    Map<String, Node> name2ExtraDetailsComponent = new HashMap<>();


    private SimpleIntegerProperty currTick = new SimpleIntegerProperty(0);
    private SimpleLongProperty runTime = new SimpleLongProperty(0);
    private SimpleStringProperty status = new SimpleStringProperty("");
    private SimpleStringProperty reason = new SimpleStringProperty("");

    private Stage HistogramStage;
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
        this.reasonLBL.textProperty().bind(reason);

        propertyComboBox.setDisable(true);
        functionComboBox.setDisable(true);
        functionComboBox.getItems().add("1. Histogram");
        functionComboBox.getItems().add("2. Consistency");
        functionComboBox.getItems().add("3. Average");
    }

    private void showAverage(){
        try {
            AveragePropertyValueDTO avgDTO = mainController.getSystemEngine().getAverageOfPropertyInTerminatedSimulation(currentChosenSimulationID, selectedEntity, selectedProperty);
            resultLBL.setVisible(true);
            resultLBL.setText("Average: " + avgDTO.getValue());
        }
        catch (Exception e){
            resultLBL.setVisible(false);
            mainController.showPopUpAlert("Error", null, e.getMessage());
        }

    }

    private void showConsistency(){
        try {
            PropertyConsistencyDTO consistencyDTO = mainController.getSystemEngine().getConsistencyOfPropertyInTerminatedSimulation(currentChosenSimulationID, selectedEntity, selectedProperty);
            resultLBL.setVisible(true);
            resultLBL.setText("Consistency: " + consistencyDTO.getValue());
        }
        catch (Exception e){
            resultLBL.setVisible(false);
            mainController.showPopUpAlert("Error", null, e.getMessage());
        }
    }

    private void showHistogram() {
        HistogramDTO histogramDTO = mainController.getSystemEngine().getHistogramOfPropertyInTerminatedSimulation(currentChosenSimulationID,selectedEntity,selectedProperty);
            createHistogramComponent(histogramDTO);

        // Create a new window for the histogram
        if (HistogramStage != null) {
            HistogramStage.close();
        }

        HistogramStage = new Stage();
        HistogramStage.setTitle("Histogram Preview");
        name2ExtraDetailsComponent.get("Histogram").getStyleClass().add("Back");
        name2ExtraDetailsComponent.get("Histogram").getStyleClass().add("bar-chart");
        Scene scene = new Scene((Parent) name2ExtraDetailsComponent.get("Histogram"),600,600);

        HistogramStage.setScene(scene);

        HistogramStage.show();
        //setExtraDetailsComponent(name2ExtraDetailsComponent.get("Histogram"));

        resultLBL.setVisible(false);
    }

    private void setExtraDetailsComponent(Node component) {
        if(!resultVBox.getChildren().isEmpty()) {
            resultVBox.getChildren().clear();
        }

        resultVBox.getChildren().add(component);
    }

    private void createHistogramComponent(HistogramDTO histogramDTO) {
        FXMLLoader loaderComponent = new FXMLLoader();
        Node component = null;
        HistogramController simulationController = null;
        loaderComponent.setLocation(getClass().getResource("/desktop/results/simulations/terminated/statistics/Histogram.fxml"));


        try {
            component = loaderComponent.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        simulationController = loaderComponent.getController();

        name2ExtraDetailsComponent.put("Histogram",component);
        simulationController.setDataFromDTO(histogramDTO);
    }

    @Override
    public void setDataFromDTO(runningSimulationDTO simulationDTO) {

        currTick.set(simulationDTO.getCurrentTick());
        runTime.set(simulationDTO.getCurrentSeconds());
        status.set(simulationDTO.getStatus());
        if(simulationDTO.getTerminationReason() != null)
            setReasonFromDTO(simulationDTO);

        bindDataToEntityTableView(simulationDTO);


        entityComboBox.getItems().clear();
        mainController.getSystemEngine().getEntitiesListDTO().forEach(entityDTO -> entityComboBox.getItems().add(entityDTO.getName()));

//        if (this.entitiesPopulationLC.getData().isEmpty())
        insertDataTolineChart(mainController.getSystemEngine().getEntitiesPopByTicks(currentChosenSimulationID).getEntitiesPopByTicks(), simulationDTO.getCurrentTick());
    }

//    INSERT IN INTERVALS:
    private void insertDataTolineChart(Map<String, Map<Integer, Integer>> entitiesPopByTicks, int totalTicks) {
        int interval = totalTicks / 1000;

        // Iterate through the entities and their population data
        for (String entity : entitiesPopByTicks.keySet()) {
            Map<Integer, Integer> data = entitiesPopByTicks.get(entity);

            // Create a data series for the entity
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(entity);

            // Add data points to the series
            for (Map.Entry<Integer, Integer> entry : data.entrySet()) {
                if(interval == 0 || entry.getKey() % interval == 0)
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            // Add the series to the line chart
            entitiesPopulationLC.getData().add(series);
        }
    }


    private void setReasonFromDTO(runningSimulationDTO simulationDTO) {
        String res = null;
        switch (TerminationReason.valueOf(simulationDTO.getTerminationReason())){
            case ENDEDBYUSER:
                res = "Terminated by user";
                break;
            case SECONDSREACHED:
                res = "Time out";
                break;
            case MAXTICKSREACHED:
                res = "Maximum Ticks reached";
                break;
            case ERROR:
                res = "ERROR: " + simulationDTO.getErrorMassage();
                break;
            default:
                res = "";
        }
        this.reason.set(res);
    }
    @Override
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setExtraDetails() {

    }

    @Override
    public void setSimulationID(int simulationID ){
        this.currentChosenSimulationID = simulationID;
    }
    @FXML
    public void ExtraDetailsSelected(ActionEvent event) {
        selectedFunction = functionComboBox.getSelectionModel().getSelectedItem();
        decodeAnaliticsFunction();
    }
    @FXML
    void entitySelected(ActionEvent event) {
        selectedEntity = entityComboBox.getSelectionModel().getSelectedItem();
        selectedProperty = selectedFunction = null;
        if (selectedEntity != null) {
            propertyComboBox.getItems().clear();
            propertyComboBox.setDisable(false);
            propertyComboBox.getSelectionModel().clearSelection();
            //propertyComboBox.setPromptText("Property");
            functionComboBox.getSelectionModel().clearSelection();
            //functionComboBox.setPromptText("Extra Details");
            functionComboBox.setDisable(true);
            resultLBL.setVisible(false);



            for(EntityDTO entity : mainController.getSystemEngine().getEntitiesListDTO() ){
                if(entity.getName().equals(selectedEntity))
                    entity.getProperties().forEach(property -> propertyComboBox.getItems().add(property.getName()));
            }
        }
    }
    @FXML
    void propertySelected(ActionEvent event) {
        selectedProperty = propertyComboBox.getSelectionModel().getSelectedItem();
        functionComboBox.setDisable(false);
        decodeAnaliticsFunction();
    }
    private void decodeAnaliticsFunction(){
        if(selectedFunction != null && selectedProperty != null && selectedEntity != null) {
            if (selectedFunction.startsWith("1")) {
                showHistogram();
            } else if (selectedFunction.startsWith("2")) {
                showConsistency();
            } else if (selectedFunction.startsWith("3")) {
                showAverage();
            }
        }
    }
    private void bindDataToEntityTableView(runningSimulationDTO simulationDTO){
        Collection<EntityCountDTO> entityCountDTOCollection = simulationDTO.getEntityCountDTOS();
        ObservableList<EntityCountDTO> data = FXCollections.observableArrayList();
        data.clear();
        data.addAll(entityCountDTOCollection);
        entityTableView.setItems(data);
    }
    @FXML
    void reRunButtonPressed(ActionEvent event) {
        mainController.reRunSimulation(this.currentChosenSimulationID);
    }
}
