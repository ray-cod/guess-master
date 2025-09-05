package com.guessing.gamemaster.controllers;

import com.guessing.gamemaster.utils.PlayerScore;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.net.URL;
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

        // Dummy values
        ObservableList<PlayerScore> dummyData = FXCollections.observableArrayList(
                new PlayerScore(1, "Alice", 1500, "2025-09-01"),
                new PlayerScore(2, "Bob", 1200, "2025-08-29"),
                new PlayerScore(3, "Charlie", 950, "2025-08-28"),
                new PlayerScore(4, "Diana", 870, "2025-08-27"),
                new PlayerScore(5, "Evan", 650, "2025-08-25")
        );

        tableView.setItems(dummyData);

        // Optional: visual niceties
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

}
