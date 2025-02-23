/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.UserRole;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author leniejoice
 */
public class EmployeeRolesService {
    
    private Connection connection;
    private DatabaseConnection dbConnection;
    
    
    public EmployeeRolesService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();    
    }
    
    public UserRole getByRolesId(int id){
        UserRole userRole =null;
        if (connection != null) {
            String Query = "SELECT * FROM public.user_roles where id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                userRole = new UserRole();
                while (resultSet.next()) {
                    userRole.setId(resultSet.getInt("id"));
                    userRole.setRole(resultSet.getString("role"));
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }             
        }                          
        return userRole;
    } 
    
    public List<UserRole> getAllUserRole(){
        List<UserRole> userRoles = new ArrayList<>();
        if (connection != null) {
        String Query = "SELECT * FROM public.user_roles";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    UserRole userRole = new UserRole();
                    userRole.setId(resultSet.getInt("id"));
                    userRole.setRole(resultSet.getString("role"));
                    userRoles.add(userRole);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return userRoles;
    }
}
