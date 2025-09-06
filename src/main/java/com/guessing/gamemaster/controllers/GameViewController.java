package com.guessing.gamemaster.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class GameViewController {

    @FXML private ListView<String> guessHistory;
    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        guessHistory.setPlaceholder(new Label("No guesses yet..."));
    }

    public void setDataFromMainView(String username){
        userLabel.setText("Welcome " + username + "!");
    }
}
