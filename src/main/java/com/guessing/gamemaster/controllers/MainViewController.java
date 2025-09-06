package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {

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

    // ===Methods===
    @FXML public void onStartClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/guessing/gamemaster/ui/game-view.fxml"));
        root = loader.load();

        GameViewController gameViewController = loader.getController();
        String username = playerNameField.getText();

        gameViewController.setDataFromMainView(username);

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

    }

    @FXML
    public void openMultiplayer(){

    }
}
