/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author leniejoice
 */
public class DatabaseConnection {
    Connection connection;
    private final String url = "jdbc:postgresql://localhost:5432/postgres"; // 
    private final String username = "postgres"; 
    private final String password = "postgres"; 

    public Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
    
///Database connection
    
    public PreparedStatement prepareStatement(String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Optionally, attempt to reconnect or throw an exception if not connected
            connect();
        }
        return connection.prepareStatement(query);
    }

}
