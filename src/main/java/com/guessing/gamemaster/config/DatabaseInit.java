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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

