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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                scores.add(new PlayerScore(rank, name, score, date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tableView.setItems(scores);

        // visual niceties
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML public void openMainMenu(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "/com/guessing/gamemaster/ui/main-view.fxml");
    }

}
