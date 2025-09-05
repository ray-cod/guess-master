package com.guessing.gamemaster.utils;

public class PlayerScore {
    private final Integer rank;
    private final String playerName;
    private final Integer score;
    private final String date;

    public PlayerScore(Integer rank, String playerName, Integer score, String date) {
        this.rank = rank;
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    public Integer getRank() {
        return rank;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Integer getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}
