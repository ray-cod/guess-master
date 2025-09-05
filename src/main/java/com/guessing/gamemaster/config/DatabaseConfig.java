package com.guessing.gamemaster.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // Embedded file DB stored at user home: ~/test.mv.db
    private static final String URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
