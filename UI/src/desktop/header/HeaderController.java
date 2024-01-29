package desktop.header;

import desktop.AppController;
import desktop.results.simulations.running.grid.GridController;
import ex2.ThreadpoolDTO;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.nio.file.Path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderController {

    @FXML private Button loadFileBTN;
    @FXML private TextField loadFileTF;
    @FXML
    private Label waitingLabel;

    @FXML
    private HBox animationSymbolsHB;

    @FXML
    private ComboBox<String> skinsCB;
    @FXML
    private Label runningLabel;

    @FXML
    private Label endedLabel;
    @FXML
    private HBox headerHBox;
    @FXML
    private CheckBox animationsCB;
    private AppController mainController;
    private String lastAccessedFolderPath = "";
    private SimpleStringProperty selectedFileProperty;
    private Circle circle;
//    private Arc halfCircle;
    private Rectangle rect;
    private Thread ThreadpoolDataPullingThread;
    private BooleanProperty animation = new SimpleBooleanProperty(false);

    public boolean isAnimation() {
        return animation.get();
    }

    public BooleanProperty animationProperty() {
        return animation;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize(){
        loadFileTF.textProperty().bind(selectedFileProperty);

        ThreadpoolDataPullingThread = new Thread(() -> {
            while (true) {
                ThreadpoolDTO threadpoolData = mainController.getSystemEngine().getThreadpoolData();

                Platform.runLater(() -> {
                    waitingLabel.setText(String.valueOf(threadpoolData.getWaitingSimulations()));
                    runningLabel.setText(String.valueOf(threadpoolData.getRunningSimulations()));
                    endedLabel.setText(String.valueOf(threadpoolData.getEndedSimulations()));
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        createAnimationShapes();
        insertSkinNamesToComboBox();



    }

    private void insertSkinNamesToComboBox() {
        skinsCB.getItems().add("default");
        skinsCB.getItems().add("dark-blue");
        skinsCB.getItems().add("light-red");
    }

    private void createAnimationShapes() {
        circle = new Circle(100, 100, 10);
        circle.setVisible(false);


        rect = new Rectangle(100, 100, 20, 20);
        rect.setFill(Color.BLUE);
        rect.setArcWidth(5);
        rect.setArcHeight(5);
        rect.setVisible(false);

        animation.bind(animationsCB.selectedProperty());
        animation.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                animationSymbolsHB.getChildren().add(circle);
                circle.setVisible(false);
                animationSymbolsHB.getChildren().add(rect);
                rect.setVisible(false);
            } else {
                if (animationSymbolsHB.getChildren().contains(circle)) {
                    animationSymbolsHB.getChildren().remove(circle);
                }
                if (animationSymbolsHB.getChildren().contains(rect)) {
                    animationSymbolsHB.getChildren().remove(rect);
                }
            }

        });
    }

    public HeaderController(){
        selectedFileProperty = new SimpleStringProperty("No file loaded");
    }
    @FXML
    void loadFileButtonActionListener(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Simulation File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

            // Set the initial directory to the last accessed folder
            if (!lastAccessedFolderPath.isEmpty()) {
                fileChooser.setInitialDirectory(new File(lastAccessedFolderPath));
            }

            // Show the file dialog and get the selected file
            File selectedFile = fileChooser.showOpenDialog(loadFileBTN.getScene().getWindow());

            if (selectedFile == null) {
                return;
            }

            // Update the last accessed folder's path
            lastAccessedFolderPath = selectedFile.getParent();


            // Load the selected file using the systemEngine
            mainController.getSystemEngine().loadSimulation(selectedFile.getAbsolutePath());

            if(mainController.getIsFileLoaded()){ //already had working one
                mainController.clearResultsTab();
                mainController.getSystemEngine().clearPastSimulations();
            }

            if (!ThreadpoolDataPullingThread.isAlive()){
                ThreadpoolDataPullingThread.start();
            }

            mainController.setIsFileLoaded(true);

            // Update the text field with the selected file path
            selectedFileProperty.set(selectedFile.getAbsolutePath());


//            System.out.println("The xml file has loaded successfully!" + System.lineSeparator());

            if (animation.get()){
                startCircleAnimation();
            }

            mainController.addDataToSimulationTreeView();
            mainController.addDataToExecutionTab();
//            mainController.moveToDetailsTab();

        }
        catch (Exception e) {
            mainController.showPopUpAlert("Invalid xml file", null, e.getMessage());
        }
    }

    private void startCircleAnimation(){
        FillTransition ft = new FillTransition(Duration.millis(1000), circle, Color.RED, Color.YELLOW);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);
        circle.setVisible(true);
        ft.play();
    }

    public void startRectangleAnimation(){
        RotateTransition rt = new RotateTransition(Duration.millis(1500), rect);
        rt.setByAngle(360);
        rect.setVisible(true);
        rt.play();

    }

    @FXML
    void skinSelected(ActionEvent event) {
        String skinName = skinsCB.getSelectionModel().getSelectedItem();
        mainController.changeSkin(skinName);
    }
}
