package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.config.DatabaseConfig;
import com.guessing.gamemaster.utils.PlayerScore;
import com.guessing.gamemaster.utils.SceneManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderboardViewController implements Initializable {

    @FXML
    private TableView<PlayerScore> tableView;

    @FXML
    private TableColumn<PlayerScore, Integer> colRank;

    @FXML
    private TableColumn<PlayerScore, String> colPlayer;

    @FXML
    private TableColumn<PlayerScore, Integer> colScore;

    @FXML
    private TableColumn<PlayerScore, String> colDate;

    @FXML
    private ComboBox<String> timeFilter;

    @FXML
    private TextField nameSearchField;

    private ArrayList<PlayerScore> playerScores = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // call onNameSearch each time the text changes
        nameSearchField.textProperty().addListener((obs, oldText, newText) -> onNameSearch());

        // Initialize time filter
        ObservableList<String> filters = FXCollections.observableArrayList();
        filters.add("Past day");
        filters.add("Past Week");
        timeFilter.setItems(filters);

        // Prefer lambda factories (avoids reflection / module issues)
        colRank.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getRank()));
        colPlayer.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getPlayerName()));
        colScore.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getScore()));
        colDate.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getDate()));

        // Add styled cell factories so CSS classes apply
        colRank.setCellFactory(tc -> {
            TableCell<PlayerScore, Integer> cell = new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                }
            };
            cell.getStyleClass().add("rank-cell"); // applies .rank-cell CSS
            return cell;
        });

        colPlayer.setCellFactory(tc -> {
            TableCell<PlayerScore, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String playerName, boolean empty) {
                    super.updateItem(playerName, empty);
                    setText(empty || playerName == null ? null : playerName);
                }
            };
            cell.getStyleClass().add("player-cell"); // applies .player-cell CSS
            return cell;
        });

        colScore.setCellFactory(tc -> {
            TableCell<PlayerScore, Integer> cell = new TableCell<>() {
                @Override
                protected void updateItem(Integer score, boolean empty) {
                    super.updateItem(score, empty);
                    setText(empty || score == null ? null : String.valueOf(score));
                }
            };
            cell.getStyleClass().add("score-cell");
            return cell;
        });

        colDate.setCellFactory(tc -> {
            TableCell<PlayerScore, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String date, boolean empty) {
                    super.updateItem(date, empty);
                    setText(empty || date == null ? null : date);
                }
            };
            cell.getStyleClass().add("date-cell");
            return cell;
        });

        // Load player scores
        loadScoresFromDB();

        // visual niceties
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadScoresFromDB() {
        playerScores.clear();
        ObservableList<PlayerScore> scores = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT player_name, score, date FROM scores ORDER BY score DESC;\n")) {

            int rank = 0;
            while (rs.next()) {
                rank += 1;
                String name = rs.getString("player_name");
                int score = rs.getInt("score");
                String date = rs.getTimestamp("date").toString();
                PlayerScore ps = new PlayerScore(rank, name, score, date);
                scores.add(ps);
                playerScores.add(ps);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set the table items to the full list by default
        tableView.setItems(scores);
    }

    /**
     * Filtered results are re-ranked 1..N so the UI shows contiguous ranks.
     */
    private void applyFilters() {
        String raw = (nameSearchField == null) ? "" : nameSearchField.getText();
        String query = (raw == null) ? "" : raw.trim().toLowerCase();

        String timeChoice = (timeFilter == null) ? null : timeFilter.getValue();
        LocalDateTime cutoff = null;
        if ("Past day".equals(timeChoice)) {
            cutoff = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        } else if ("Past Week".equals(timeChoice)) {
            cutoff = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        }

        List<PlayerScore> matched = new ArrayList<>();

        for (PlayerScore ps : playerScores) {
            // name filter
            boolean nameMatches = true;
            if (!query.isEmpty()) {
                String name = ps.getPlayerName();
                nameMatches = (name != null && name.toLowerCase().contains(query));
            }

            if (!nameMatches) continue;

            // time filter
            boolean timeMatches = true;
            if (cutoff != null) {
                try {
                    Timestamp ts = Timestamp.valueOf(ps.getDate());
                    LocalDateTime when = ts.toLocalDateTime();
                    timeMatches = when.isAfter(cutoff) || when.isEqual(cutoff);
                } catch (Exception ex) {
                    // if parsing fails, skip this row (or keep it - choose to skip)
                    timeMatches = false;
                }
            }

            if (timeMatches) matched.add(ps);
        }

        // re-rank matched results so ranks are contiguous in the filtered view
        ObservableList<PlayerScore> filtered = FXCollections.observableArrayList();
        for (int i = 0; i < matched.size(); i++) {
            PlayerScore ps = matched.get(i);
            filtered.add(new PlayerScore(i + 1, ps.getPlayerName(), ps.getScore(), ps.getDate()));
        }

        tableView.setItems(filtered);
        tableView.getSelectionModel().clearSelection();
        if (!filtered.isEmpty()) tableView.scrollTo(0);
    }

    @FXML public void openMainMenu(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "/com/guessing/gamemaster/ui/main-view.fxml");
    }

    @FXML public void onNameSearch(){
        // simply re-apply combined filters when the name changes
        applyFilters();
    }


    @FXML public void onTimeFilter(){
        // when the time filter selection changes re-apply filters
        applyFilters();
    }


    @FXML public void onRefresh(){
        // reload from database and re-apply filters
        loadScoresFromDB();
        applyFilters();
    }

    @FXML public void onExport(){
        // export current table view to CSV using a FileChooser
        ObservableList<PlayerScore> items = tableView.getItems();
        if (items == null || items.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "No data to export.", ButtonType.OK);
            a.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Leaderboard");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("leaderboard.csv");

        Window window = tableView.getScene() == null ? null : tableView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);
        if (file == null) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // header
            bw.write("Rank,Player,Score,Date");
            bw.newLine();

            for (PlayerScore ps : items) {
                String line = String.format("%d,%s,%d,%s",
                        ps.getRank(), escapeCsv(ps.getPlayerName()), ps.getScore(), escapeCsv(ps.getDate()));
                bw.write(line);
                bw.newLine();
            }

            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Exported successfully to " + file.getAbsolutePath(), ButtonType.OK);
            ok.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert err = new Alert(Alert.AlertType.ERROR, "Failed to export: " + e.getMessage(), ButtonType.OK);
            err.showAndWait();
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        // double internal quotes and wrap in quotes if there are commas or quotes or newlines
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
