package desktop.execution;

import desktop.execution.envvar.api.EnvVarControllerAPI;
import desktop.results.simulations.running.RunningSimulationController;
import engine.property.PropertyType;
import engineAnswers.*;
import ex2.runningSimulationDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import desktop.AppController;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.*;

public class ExecutionController {

    @FXML
    private GridPane executionGrid;
    Map<String, Node> name2envVarComponent = new HashMap<>();
    Map<String, EnvVarControllerAPI> name2envVarController = new HashMap<>();

    @FXML
    private Button startBTN;
    @FXML
    private Button clearBTN;

    ////////////////////////////////////////////////////////
    @FXML
    private TableView<EntityDTO> entityPopulationTable;
    @FXML
    private TableColumn<EntityDTO, String> entityCol;
    @FXML
    private TableColumn<EntityDTO, Integer> populationCol;
    ////////////////////////////////////////////////////////
    @FXML
    private ListView<String> envListView;

    @FXML private HBox currentEnvVarDetailsHBox;

    private int gridSize = 0;
    private AppController mainController;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
        clearBTN.disableProperty().bind(mainController.isFileLoadedProperty().not());
        startBTN.disableProperty().bind(mainController.isFileLoadedProperty().not());
    }

    @FXML
    void startButtonActionListener(ActionEvent event) {
        if(mainController.isAnimation()){
            mainController.startRectangleAnimation();
        }
        for (EnvVarControllerAPI controller : name2envVarController.values()) {
            PropertyDTO envVarDTO = controller.createEnvVarDTO();
            mainController.getSystemEngine().setEnvVarDefFromDto(envVarDTO);
        }

        pastSimulationDTO pastSimulationDTO = mainController.getSystemEngine().runSimulation();
        mainController.moveToResultsTab(pastSimulationDTO);
    }

    @FXML
    void clrButtonActionListener(ActionEvent event) {
        mainController.getSystemEngine().setAllPopulationToZero();
        addDataToEntitiesTable();
        addDataToEnvVarsListView();
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void addDataToEntitiesTable()
    {
        Collection<EntityDTO> entityDTOCollection = mainController.getSystemEngine().getEntitiesListDTO();

        ObservableList<EntityDTO> data = FXCollections.observableArrayList();
        data.addAll(entityDTOCollection);

        entityPopulationTable.setItems(data);
    }

    public void addDataToEnvVarsListView()
    {
        envListView.getItems().clear();
        currentEnvVarDetailsHBox.getChildren().clear();
        name2envVarController.clear();
        name2envVarComponent.clear();
        List<PropertyDTO> envVarsDefinitionDto = mainController.getSystemEngine().getEnvVarsDefinitionDto();
        envVarsDefinitionDto.forEach((envVar) -> envListView.getItems().add(envVar.getName()));
    }

    public void initialize()
    {
        entityCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        populationCol.setCellValueFactory(new PropertyValueFactory<>("population"));
        populationCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        populationCol.setOnEditCommit(event -> {
            // Get the edited item
            EntityDTO oldEntityDTO = event.getRowValue();
            int newPopulation = event.getNewValue();

            if (newPopulation < 0){ //if inserted negative population:
                // Revert the cell value to the previous state
                event.getTableView().getItems().set(event.getTablePosition().getRow(), oldEntityDTO);

            } else {

                //check For Max Num Of Entities
                int currentTotalPopulation = calculateTotalPopulation();
                int updatedTotalPopulation = currentTotalPopulation - oldEntityDTO.getPopulation() + newPopulation;

                if (updatedTotalPopulation <= gridSize) {
                    // Update the population property of the Entity
                    EntityDTO newEntityDTO = new EntityDTO(oldEntityDTO.getName(), event.getNewValue(), oldEntityDTO.getProperties());

                    // updated value in engine
                    mainController.getSystemEngine().updateEntityDefPopulation(newEntityDTO);

                    //update value in table
                    event.getTableView().getItems().set(event.getTablePosition().getRow(), newEntityDTO);

                } else {
                    mainController.showPopUpAlert("Population Error", "Population exceeds the grid size limit.", "The total population cannot exceed the grid size limit:"
                            + System.lineSeparator() + "Entered total of: " + updatedTotalPopulation + " entities, while the grid size is: " + gridSize);

                    // Revert the cell value to the previous state
                    event.getTableView().getItems().set(event.getTablePosition().getRow(), oldEntityDTO);
                }
            }

            event.getTableView().getSelectionModel().clearSelection();
        });

        envListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showEnvVarDetails(newValue);
        });

    }

    private int calculateTotalPopulation() {
        int totalPopulation = 0;

        // Iterate through the items in the TableView
        for (EntityDTO entity : entityPopulationTable.getItems()) {
            totalPopulation += entity.getPopulation();
        }

        return totalPopulation;
    }

    private void showEnvVarDetails(String newValue) {
        for (PropertyDTO envVarDTO : mainController.getSystemEngine().getEnvVarsDefinitionDto()) {
            if (envVarDTO.getName().equals(newValue)) {
                if (!name2envVarComponent.containsKey(newValue)) {
                    createEnvVarComponent(envVarDTO);
                }

                if (currentEnvVarDetailsHBox.getChildren().size() != 0) {
                    currentEnvVarDetailsHBox.getChildren().clear();
                }
                currentEnvVarDetailsHBox.getChildren().add(name2envVarComponent.get(newValue));
            }
        }
    }

    private void createEnvVarComponent(PropertyDTO envVarDTO)
    {
        try {
        FXMLLoader loaderComponent = new FXMLLoader();
        Node component = null;
        EnvVarControllerAPI envVarController = null;

        switch (PropertyType.valueOf(envVarDTO.getType().toUpperCase())){
            case BOOLEAN:
                loaderComponent.setLocation(getClass().getResource("/desktop/execution/envvar/impl/BooleanEnvVar.fxml"));
                break;
            case DECIMAL:
            case FLOAT:
                loaderComponent.setLocation(getClass().getResource("/desktop/execution/envvar/impl/FloatEnvVar.fxml"));
                break;
            case STRING:
                loaderComponent.setLocation(getClass().getResource("/desktop/execution/envvar/impl/StringEnvVar.fxml"));
                break;
        }

        component = loaderComponent.load();
        envVarController = loaderComponent.getController();
        envVarController.setDataFromDTO(envVarDTO);
        envVarController.setMainController(this.mainController);
        name2envVarController.put(envVarDTO.getName(), envVarController);
        name2envVarComponent.put(envVarDTO.getName(), component);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDetailsToReRun(int simulationID){
        Collection<EntityDTO> entityDTOCollection = new ArrayList<>();
        Collection<EntityCountDTO> entityCountDTOCollection = mainController.getSystemEngine().getPastSimulationEntityCount(new pastSimulationDTO(null,simulationID));
        entityCountDTOCollection.forEach(entityCountDTO -> entityDTOCollection.add(new EntityDTO(entityCountDTO.getName(), entityCountDTO.getPopulationAtStart(), null)));

        ObservableList<EntityDTO> data = FXCollections.observableArrayList();
        data.addAll(entityDTOCollection);

        entityPopulationTable.setItems(data);

        List<ActiveEnvVarDTO> activeEnvVarList= mainController.getSystemEngine().getActiveEnvVarsDto(simulationID);
        activeEnvVarList.forEach(envVar -> {
                showEnvVarDetails(envVar.getName());
                name2envVarController.get(envVar.getName()).setValue(envVar.getValue());
        });
        currentEnvVarDetailsHBox.getChildren().clear();
        envListView.getSelectionModel().clearSelection();


    }
}
