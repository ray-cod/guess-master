package com.guessing.gamemaster.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class GameViewController {

    @FXML private ListView<String> guessHistory;
    @FXML private Label userLabel;
    @FXML private Label roundLabel;
    @FXML private Label instructionLabel;
    @FXML private Label attemptLabel;

    @FXML
    public void initialize() {
        guessHistory.setPlaceholder(new Label("No guesses yet..."));
    }

    public void setDataFromMainView(String username, int rounds, int range, int attempts){
        userLabel.setText("Welcome " + username + "!");
        roundLabel.setText("Round: 1 / " + rounds);
        instructionLabel.setText("Instruction: Guess a number between 1 and " + range + ".");
        attemptLabel.setText("1 / " + attempts);

    }
}
