package desktop.details;

import desktop.AppController;
import desktop.details.action.api.ActionControllerAPI;
import desktop.details.property.PropertyController;
import engine.action.api.ActionType;
import engineAnswers.*;
import ex2.actions.SingleConditionDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetailsController {

    @FXML
    private TreeView<String> simulationTV;
    @FXML
    private VBox detailsVBox;
    private AppController mainController;

    public void initialize() {
        simulationTV.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDetails(newValue)
        );
    }

    private void showDetails(TreeItem<String> selectedNode) {
        SimulationDetailsDTO simulationDetailsDTO = mainController.getSystemEngine().getSimulationDetails();
        detailsVBox.getChildren().clear();

        if (selectedNode != null && selectedNode.isLeaf()) {
            String selectedNodeValue = selectedNode.getValue();
            if (selectedNodeValue.equals("Termination Conditions"))
            {
                if (simulationDetailsDTO.getMaxNumberOfTicks() != null) {
                    detailsVBox.getChildren().add(new Label("Number Of Ticks: " + simulationDetailsDTO.getMaxNumberOfTicks()));

                }
                if (simulationDetailsDTO.getSecondsToTerminate() != null){
                    detailsVBox.getChildren().add(new Label("Number Of Seconds: " + simulationDetailsDTO.getSecondsToTerminate()));

                }
                if(simulationDetailsDTO.isTerminatedByUser()){
                    detailsVBox.getChildren().add(new Label("Terminate By User"));
                }
            }
            else if (selectedNodeValue.equals("Activation")) {
                String selectedNodeRuleName = selectedNode.getParent().getValue();
                for (RuleDTO ruleDTO :simulationDetailsDTO.getRules()) {
                    if (ruleDTO.getName().equals(selectedNodeRuleName)) {
                        detailsVBox.getChildren().add(new Label("Ticks for activation: " + ruleDTO.getTicksForActivation()));
                        detailsVBox.getChildren().add(new Label("Probability for activation: " + ruleDTO.getProbabilityForActivation()));
                    }
                }
            }

            else if (selectedNodeValue.equals("Actions")) {
                String selectedNodeRuleName = selectedNode.getParent().getValue();
                for (RuleDTO ruleDTO : simulationDetailsDTO.getRules()) {
                    if (ruleDTO.getName().equals(selectedNodeRuleName)) {
                        for (ActionDTO actionDTO : ruleDTO.getActions()) {
                            detailsVBox.getChildren().add(createActionComponent(actionDTO));
                            detailsVBox.getChildren().add(new Label());
                        }
                    }
                }
            }


            else{
                switch (selectedNode.getParent().getValue()) {
                    case "Properties":
                        String selectedNodeEntityName = selectedNode.getParent().getParent().getValue();
                        for (EntityDTO entityDTO : simulationDetailsDTO.getEntities()) {
                            if (entityDTO.getName().equals(selectedNodeEntityName)) {
                                for (PropertyDTO propertyDTO : entityDTO.getProperties())
                                {
                                    if (propertyDTO.getName().equals(selectedNodeValue)) {
                                        detailsVBox.getChildren().add(createPropertyComponent(propertyDTO, false));
                                    }
                                }
                            }
                        }
                        break;
                    case "Environment Variables":
                        for (PropertyDTO envVarDTO : simulationDetailsDTO.getEnvironmentVariables()) {
                            if (envVarDTO.getName().equals(selectedNodeValue)) {
                                detailsVBox.getChildren().add(createPropertyComponent(envVarDTO, true));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void addDataToSimulationTreeView() {

        SimulationDetailsDTO simulationDetailsDTO = mainController.getSystemEngine().getSimulationDetails();
        TreeItem<String> rootItem = new TreeItem<>("Loaded Simulation Data");

        TreeItem<String> entitiesItem = new TreeItem<>("Entities");
        for (EntityDTO entityDTO : simulationDetailsDTO.getEntities()) {
            TreeItem<String> entityItem = new TreeItem<>(entityDTO.getName());

            TreeItem<String> propertiesItem = new TreeItem<>("Properties");
            for (PropertyDTO propertyDTO : entityDTO.getProperties()) {
                propertiesItem.getChildren().add(new TreeItem<>(propertyDTO.getName()));
            }

            entityItem.getChildren().add(propertiesItem);
            entitiesItem.getChildren().add(entityItem);
        }

        TreeItem<String> envVarsItem = new TreeItem<>("Environment Variables");
        for (PropertyDTO envVarDTO : simulationDetailsDTO.getEnvironmentVariables()) {
            envVarsItem.getChildren().add(new TreeItem<>(envVarDTO.getName()));
        }

        TreeItem<String> rulesItem = new TreeItem<>("Rules");

        for (RuleDTO ruleDTO : simulationDetailsDTO.getRules()) {
            TreeItem<String> ruleItem = new TreeItem<>(ruleDTO.getName());

            TreeItem<String> actionsItem = new TreeItem<>("Actions");
//            for (ActionDTO actionDTO: ruleDTO.getActions()) { //ADD INNER ACTIONS TO TREE:
//                actionsItem.getChildren().add(new TreeItem<>(actionDTO.getType()));
//            };

            ruleItem.getChildren().add(actionsItem);
            ruleItem.getChildren().add(new TreeItem<>("Activation"));
            rulesItem.getChildren().add(ruleItem);
        }


        TreeItem<String> terminationItem = new TreeItem<>("Termination Conditions");

        TreeItem<String> gridItem = new TreeItem<>("Grid: " + simulationDetailsDTO.getRowsInGrid() + " X " + simulationDetailsDTO.getColsInGrid());

        rootItem.getChildren().add(entitiesItem);
        rootItem.getChildren().add(envVarsItem);
        rootItem.getChildren().add(rulesItem);
        rootItem.getChildren().add(terminationItem);
        rootItem.getChildren().add(gridItem);
        this.simulationTV.setRoot(rootItem);
        this.simulationTV.refresh();
    }

    private Node createActionComponent(ActionDTO actionDTO)
    {
        try {
            FXMLLoader loaderComponent = new FXMLLoader();
            Node component = null;
            ActionControllerAPI actionController = null;

            switch (ActionType.valueOf(actionDTO.getType().toUpperCase())) {
                case INCREASE:
                case DECREASE:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/increase.fxml"));
                    break;
                case CALCULATION:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/calculation.fxml"));
                    break;
                case CONDITION:
                    if (actionDTO instanceof SingleConditionDTO)
                        loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/simpleCondition.fxml"));
                    else // (actionDTO instanceof MultipleConditionDTO)
                        loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/multipleCondition.fxml"));
                    break;
                case SET:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/set.fxml"));
                    break;
                case KILL:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/kill.fxml"));
                    break;
                case REPLACE:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/replace.fxml"));
                    break;
                case PROXIMITY:
                    loaderComponent.setLocation(getClass().getResource("/desktop/details/action/impl/proximity.fxml"));
                    break;
            }

            component = loaderComponent.load();
//            component.getStyleClass().add("back");
            actionController = loaderComponent.getController();
            actionController.setDataFromDTO(actionDTO);
            return component;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Node createPropertyComponent(PropertyDTO propertyDTO, boolean isEnvVar)
    {
        try {
            FXMLLoader loaderComponent = new FXMLLoader();
            loaderComponent.setLocation(getClass().getResource("/desktop/details/property/property.fxml"));

            Node component = loaderComponent.load();
            PropertyController propertyController = loaderComponent.getController();
            propertyController.setDataFromDTO(propertyDTO, isEnvVar);

            return component;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
