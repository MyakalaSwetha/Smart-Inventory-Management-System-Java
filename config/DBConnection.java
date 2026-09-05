package com.inventory.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Author  : Myakala Swetha
 * Project : Smart Inventory Management System
 */
public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:Free";
    private static final String USER = "system";
    private static final String PASSWORD = "your_oracle_password"; // Security feature for GitHub

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
