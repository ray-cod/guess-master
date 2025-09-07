package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.services.GameService;
import com.guessing.gamemaster.utils.GuessResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.time.LocalTime;

public class GameViewController {

    @FXML private ListView<String> guessHistory;
    @FXML private Label userLabel;
    @FXML private Label roundLabel;
    @FXML private Label instructionLabel;
    @FXML private Label attemptLabel;
    @FXML private TextField guessField;
    @FXML private Label feedbackLabel;
    @FXML private Label scoreLabel;
    @FXML private ProgressBar attemptProgress;

    /*==== Game properties ===*/
    private int target;
    private int guess;
    private int score;
    private GuessResult guessResult;
    private int totalAttempts;
    private int playerAttempts;
    private int rangeLimit;
    private LocalTime playTime = LocalTime.of(0, 0, 50);

    @FXML
    public void initialize() {
        guessHistory.setPlaceholder(new Label("No guesses yet..."));
    }

    public void setDataFromMainView(String username, int rounds, int range, int attempts){
        userLabel.setText("Welcome " + username + "!");
        roundLabel.setText("Round: 1 / " + rounds);
        instructionLabel.setText("Instruction: Guess a number between 1 and " + range + ".");
        attemptLabel.setText("1 / " + attempts);

        // ==== Setting game properties ===
        this.target = GameService.generateTargetNumber(1, range);
        this.totalAttempts = attempts;
        this.rangeLimit = range;
    }

    @FXML
    public void onGuessClicked() {
        playerAttempts += 1;
        guess = Integer.parseInt(guessField.getText());

        guessResult = GameService.checkGuess(guess, target);
        if (guessResult.isCorrect()){
            score += GameService.calculateScore(playerAttempts, playTime, rangeLimit);
            feedbackLabel.setText(guessResult.message());
        } else {
            score -= 2;
            feedbackLabel.setText(guessResult.message());
        }

        scoreLabel.setText("Score: " + score);
        attemptLabel.setText((playerAttempts + 1) + " / " + totalAttempts);
        attemptProgress.setProgress((double) playerAttempts / totalAttempts);
        guessField.setText("");
    }
}
