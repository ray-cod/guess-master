package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    // === Identity row ===
    @FXML private TextField playerNameField;

    // === Mode selector ===
    @FXML private ToggleGroup modeToggleGroup;
    @FXML private RadioButton rbSingle;
    @FXML private RadioButton rbLocal2;
    @FXML private RadioButton rbOnline;

    // === Options panel ===
    @FXML private Spinner<Integer> roundsSpinner;
    @FXML private Slider rangeSlider;
    @FXML private Label rangeLabel;
    @FXML private Spinner<Integer> attemptsSpinner;

    // === Primary action ===
    @FXML private Button startButton;
    @FXML private Button demoBtn;
    @FXML private Button shuffleBtn;

    // === Footer ===
    @FXML private Button multiplayerBtn;
    @FXML private Button leaderboardBtn;
    @FXML private Button exitBtn;

    // === screen ===
    private Parent root;
    private Stage stage;
    @FXML private AnchorPane mainAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set spinners values
        SpinnerValueFactory<Integer> roundValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        SpinnerValueFactory<Integer> attemptValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        roundsSpinner.setValueFactory(roundValueFactory);
        attemptsSpinner.setValueFactory(attemptValueFactory);

        // Listen to slider value changes
        rangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            rangeLabel.setText(Integer.toString(newValue.intValue()));
        });
    }

    // ===Methods===
    @FXML public void onStartClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/guessing/gamemaster/ui/game-view.fxml"));
        root = loader.load();

        GameViewController gameViewController = loader.getController();
        String username = playerNameField.getText();
        int range = (int) rangeSlider.getValue();
        int rounds = roundsSpinner.getValue();
        int attempts = attemptsSpinner.getValue();

        gameViewController.setDataFromMainView(username, rounds, range, attempts);

        // Switch to game screen
        SceneManager.switchSceneWithData(event, root);
    }

    @FXML
    public void onDemoClicked(){

    }

    @FXML
    public void onRandomPresetClicked(){

    }

    @FXML
    public void openLeaderboard(){

    }

    @FXML
    public void onExitClicked(ActionEvent event){
        Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        closeAlert.setTitle("Exit");
        closeAlert.setHeaderText("Game Exit");
        closeAlert.setContentText("Are you sure that you want to leave?");

        if(closeAlert.showAndWait().get() == ButtonType.OK){
            stage = (Stage) mainAnchorPane.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void openMultiplayer(){

    }
}
