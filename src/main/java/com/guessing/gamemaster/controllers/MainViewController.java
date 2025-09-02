package com.guessing.gamemaster.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

public class MainViewController {

    // === Identity row ===
    @FXML private TextField playerNameField;
    @FXML private Button avatarButton;

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
    @FXML private Label versionLabel;
    @FXML private Button settingsBtn;
    @FXML private Button exitBtn;

    @FXML private Region backdrop; // optional if you plan to style or animate the backdrop
    @FXML private Region card;


    // ===Methods===
    @FXML
    public void onStartClicked(){

    }

    @FXML
    public void onAvatarClicked(){

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
    public void onExitClicked(){

    }

    @FXML
    public void openSettings(){

    }

    @FXML
    public void openMultiplayer(){

    }
}
