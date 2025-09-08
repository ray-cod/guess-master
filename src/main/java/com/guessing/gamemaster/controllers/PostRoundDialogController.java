package com.guessing.gamemaster.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PostRoundDialogController implements Initializable {

    // Root + backdrop
    @FXML private StackPane dialogRoot;      // root StackPane from post-round FXML
    @FXML private Region backdrop;        // full-screen Rectangle that blocks clicks

    // Card content
    @FXML private ImageView resultIcon;
    @FXML private Label resultLabel;
    @FXML private Label subtitleLabel;

    @FXML private Label pointsLabel;
    @FXML private Label totalLabel;
    @FXML private Label attemptsLabel;
    @FXML private Label timeLabel;

    @FXML private TitledPane breakdownPane;
    @FXML private Label breakdownLabel;

    // Action buttons
    @FXML private Button nextButton;
    @FXML private Button replayButton;
    @FXML private Button leaderboardButton;
    @FXML private Button shareButton;
    @FXML private Button mainMenuButton;
    @FXML private Button closeButton;

    // Callbacks assigned by the game controller
    private Runnable onNext = null;
    private Runnable onReplay = null;
    private Runnable onMainMenu = null;
    private Consumer<Result> onShare = null; // --> share handler

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // dialog hidden by default; ensure it doesn't block when invisible
        if (dialogRoot != null) dialogRoot.setVisible(false);

        // consume backdrop clicks so underlying UI doesn't get them
        if (backdrop != null) {
            backdrop.setOnMouseClicked(evt -> evt.consume());
        }

        // wire simple button handlers to call external callbacks (if provided)
        if (nextButton != null) nextButton.setOnAction(e -> { if (onNext != null) onNext.run(); close(); });
        if (replayButton != null) replayButton.setOnAction(e -> { if (onReplay != null) onReplay.run(); close(); });
        if (mainMenuButton != null) mainMenuButton.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); close(); });
        if (closeButton != null) closeButton.setOnAction(e -> close());
        if (shareButton != null) shareButton.setOnAction(e -> {
            if (onShare != null) onShare.accept(lastShownResult);
        });

        // keyboard handling: Enter -> Next (if enabled), Esc -> close
        if (dialogRoot != null) {
            dialogRoot.setOnKeyPressed(evt -> {
                if (evt.getCode() == KeyCode.ESCAPE) {
                    close();
                } else if (evt.getCode() == KeyCode.ENTER) {
                    if (nextButton != null && !nextButton.isDisabled()) {
                        nextButton.fire();
                    }
                }
            });
        }
    }

    // keep last shown result for share / inspect
    private Result lastShownResult = null;

    /**
     * Populate the dialog and show it with an entrance animation.
     */
    public void show(Result result) {
        Objects.requireNonNull(result);
        lastShownResult = result;

        // populate text
        if (resultLabel != null) resultLabel.setText(result.title);
        if (subtitleLabel != null) subtitleLabel.setText(result.subtitle != null ? result.subtitle : "");
        if (pointsLabel != null) pointsLabel.setText((result.points >= 0 ? "+" : "") + result.points + " pts");
        if (totalLabel != null) totalLabel.setText(String.valueOf(result.totalScore));
        if (attemptsLabel != null) attemptsLabel.setText(result.attempts + " / " + result.maxAttempts);
        if (timeLabel != null) timeLabel.setText(result.timeString != null ? result.timeString : "-");
        if (breakdownLabel != null) breakdownLabel.setText(result.breakdownText != null ? result.breakdownText : "");
        if (breakdownPane != null) breakdownPane.setExpanded(false);

        // set which controls are available
        if (nextButton != null) {
            nextButton.setDisable(!result.hasNextRound);
            nextButton.setText(result.hasNextRound ? "Next Round" : "Summary");
        }

        // set icon (trophy for win, sad for lose) - try to load resources; fallback is no image
        if (resultIcon != null) {
            Image icon = loadIcon(result.isWin ? "/com/guessing/gamemaster/images/trophy.png" : "/com/guessing/gamemaster/images/sad.png");
            resultIcon.setImage(icon);
        }

        // show node and play animation
        if (dialogRoot != null) {
            dialogRoot.setOpacity(0);
            dialogRoot.setScaleX(0.98);
            dialogRoot.setScaleY(0.98);
            dialogRoot.setVisible(true);
            dialogRoot.toFront();

            // request focus so key events work
            dialogRoot.requestFocus();

            Timeline show = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dialogRoot.opacityProperty(), 0),
                            new KeyValue(dialogRoot.scaleXProperty(), 0.98),
                            new KeyValue(dialogRoot.scaleYProperty(), 0.98)
                    ),
                    new KeyFrame(Duration.millis(260),
                            new KeyValue(dialogRoot.opacityProperty(), 1),
                            new KeyValue(dialogRoot.scaleXProperty(), 1),
                            new KeyValue(dialogRoot.scaleYProperty(), 1)
                    )
            );
            show.play();

            // subtle pop on card's first child (if any)
            Node card = (dialogRoot.getChildren().size() > 1) ? dialogRoot.getChildren().get(1) : null;
            if (card != null) {
                ScaleTransition st = new ScaleTransition(Duration.millis(320), card);
                st.setFromX(0.98);
                st.setFromY(0.98);
                st.setToX(1);
                st.setToY(1);
                st.setInterpolator(Interpolator.EASE_OUT);
                st.play();
            }

            // focus primary button
            if (nextButton != null) nextButton.requestFocus();
        }
    }

    /**
     * Hide the dialog with a short exit animation.
     */
    public void close() {
        if (dialogRoot == null) return;

        Timeline hide = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(dialogRoot.opacityProperty(), dialogRoot.getOpacity()),
                        new KeyValue(dialogRoot.scaleXProperty(), dialogRoot.getScaleX()),
                        new KeyValue(dialogRoot.scaleYProperty(), dialogRoot.getScaleY())
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(dialogRoot.opacityProperty(), 0),
                        new KeyValue(dialogRoot.scaleXProperty(), 0.98),
                        new KeyValue(dialogRoot.scaleYProperty(), 0.98)
                )
        );
        hide.setOnFinished(evt -> dialogRoot.setVisible(false));
        hide.play();
    }

    /** Set callbacks for dialog actions. */
    public void setHandlers(Runnable onNext, Runnable onReplay, Runnable onMainMenu) {
        this.onNext = onNext;
        this.onReplay = onReplay;
        this.onMainMenu = onMainMenu;
    }

    /** --> set a handler to process share requests (receives the shown Result). */
    public void setShareHandler(Consumer<Result> shareHandler) {
        this.onShare = shareHandler;
    }

    // simple icon loader with safe fallback (null if not found)
    private Image loadIcon(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return new Image(is);
        } catch (Exception e) {
            return null;
        }
    }

    // FXML button handlers (if you wired onAction in FXML instead of programmatically)
    @FXML private void onNextRound() { if (onNext != null) onNext.run(); close(); }
    @FXML private void onReplay() { if (onReplay != null) onReplay.run(); close(); }
    @FXML private void onLeaderboard() { if (onMainMenu != null) onMainMenu.run(); close(); }
    @FXML private void onShare() { if (onShare != null) onShare.accept(lastShownResult); }
    @FXML private void onClose() { close(); }

    /**
     * Immutable result object used to populate the dialog.
     * Use the compact constructor below when creating results.
     */
    public static final class Result {
        public final boolean isWin;
        public final String title;
        public final String subtitle;
        public final int points;
        public final int totalScore;
        public final int attempts;
        public final int maxAttempts;
        public final String timeString;
        public final String breakdownText;
        public final boolean hasNextRound;

        public Result(boolean isWin, String title, String subtitle,
                      int points, int totalScore,
                      int attempts, int maxAttempts,
                      String timeString, String breakdownText,
                      boolean hasNextRound) {
            this.isWin = isWin;
            this.title = title;
            this.subtitle = subtitle;
            this.points = points;
            this.totalScore = totalScore;
            this.attempts = attempts;
            this.maxAttempts = maxAttempts;
            this.timeString = timeString;
            this.breakdownText = breakdownText;
            this.hasNextRound = hasNextRound;
        }

        // convenience builders
        public static Result win(int points, int totalScore, int attempts, int maxAttempts, String timeString, String breakdown, boolean hasNext) {
            return new Result(true, "Correct — You Win!", "Nice work!", points, totalScore, attempts, maxAttempts, timeString, breakdown, hasNext);
        }

        public static Result loss(int points, int totalScore, int attempts, int maxAttempts, String timeString, String breakdown, boolean hasNext) {
            return new Result(false, "Round Over", "Out of attempts", points, totalScore, attempts, maxAttempts, timeString, breakdown, hasNext);
        }
    }
}
