package com.guessing.gamemaster.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInit {
    public static void initialize() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            // create table
            String ddl = "CREATE TABLE IF NOT EXISTS scores (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "player_name VARCHAR(255) NOT NULL, " +
                    "score INT NOT NULL, " +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(ddl);

            // check empty (ResultSet in try-with-resources)
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM scores")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO scores(player_name, score) VALUES ('Raimi Dikamona', 123)");
                    stmt.executeUpdate("INSERT INTO scores(player_name, score) VALUES ('Ariana Verena', 97)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

