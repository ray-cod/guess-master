package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.services.GameService;
import com.guessing.gamemaster.utils.GuessResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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
    @FXML private Label timerLabel;

    /*==== Game properties ===*/
    private int target;
    private int guess;
    private int score;
    private GuessResult guessResult;
    private int totalAttempts;
    private int playerAttempts;
    private int rangeLimit;

    private int timerStart = 180; // 3 minutes (seconds)
    private int remainingSeconds;  // remaining seconds for the countdown
    private LocalTime playTime = LocalTime.of(0, 0, 50);

    private Timeline timeline;

    @FXML
    public void initialize() {
        guessHistory.setPlaceholder(new Label("No guesses yet..."));
        // default UI state
        scoreLabel.setText("Score: " + score);
        feedbackLabel.setText("");
        attemptProgress.setProgress(0.0);
        timerLabel.setText(formatTime(timerStart));
    }

    public void setDataFromMainView(String username, int rounds, int range, int attempts){
        userLabel.setText("Welcome " + username + "!");
        roundLabel.setText("Round: 1 / " + rounds);
        instructionLabel.setText("Instruction: Guess a number between 1 and " + range + ".");
        // initialize attempts display
        this.playerAttempts = 0;
        this.totalAttempts = attempts;
        attemptLabel.setText(playerAttempts + " / " + totalAttempts);

        // ==== Setting game properties ===
        this.target = GameService.generateTargetNumber(1, range);
        this.rangeLimit = range;

        // setup and start timer
        this.remainingSeconds = timerStart;
        updateTimerLabel();
        startTimer();
    }

    @FXML
    public void onGuessClicked() {
        // validate input
        String text = guessField.getText();
        int parsed;
        try {
            parsed = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Please enter a valid number.");
            guessField.setText("");
            return;
        }

        // check if time already expired or input disabled
        if (guessField.isDisabled() || remainingSeconds <= 0) {
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("Can't guess now — time's up or input disabled.");
            return;
        }

        // accept guess
        playerAttempts += 1;
        guess = parsed;

        guessResult = GameService.checkGuess(guess, target);
        if (guessResult.isCorrect()){
            // compute elapsed time for scoring
            int secondsElapsed = timerStart - remainingSeconds;
            if (secondsElapsed < 0) secondsElapsed = 0;
            playTime = LocalTime.ofSecondOfDay(secondsElapsed);

            // stop timer (round finished)
            if (timeline != null) timeline.stop();

            score += GameService.calculateScore(playerAttempts, playTime, rangeLimit);
            feedbackLabel.setStyle("-fx-text-fill: green;");
            feedbackLabel.setText(guessResult.message());

            // disable further guesses for this round
            guessField.setDisable(true);

        } else {
            score -= 2;
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText(guessResult.message());
        }

        scoreLabel.setText("Score: " + score);
        attemptLabel.setText(playerAttempts + " / " + totalAttempts);
        attemptProgress.setProgress((double) playerAttempts / totalAttempts);
        guessHistory.getItems().add("Guess " + playerAttempts + ": " + guess + " -> " + guessResult.message());
        guessField.setText("");

        // if attempts exhausted, end round
        if (playerAttempts >= totalAttempts) {
            if (timeline != null) timeline.stop();
            feedbackLabel.setStyle("-fx-text-fill: red;");
            feedbackLabel.setText("No more attempts.");
            playTime = LocalTime.ofSecondOfDay(timerStart - remainingSeconds >= 0 ? timerStart - remainingSeconds : timerStart);
            guessField.setDisable(true);
        }
    }

    private void startTimer() {
        // stop any existing timeline
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            updateTimerLabel();

            // Turn timer label to red at 30 seconds
            if (remainingSeconds == 30){
                timerLabel.setStyle("-fx-text-fill: red;");
            }

            if (remainingSeconds <= 0) {
                // Time's up
                timeline.stop();
                remainingSeconds = 0;
                updateTimerLabel();
                onTimeExpired();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void onTimeExpired() {
        feedbackLabel.setStyle("-fx-text-fill: red;");
        feedbackLabel.setText("Time's up!");
        // mark playTime as total elapsed (whole timer)
        playTime = LocalTime.ofSecondOfDay(timerStart);
        // disable further input for this round
        guessField.setDisable(true);
    }

    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}
