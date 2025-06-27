/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.subdomain.UserRole;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class ITService {
    private Connection connection;
    private HRService hrService;

    public ITService(Connection connection) {
        try {
            this.connection = DatabaseConnection.getConnection();
            this.hrService = new HRService(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger in real applications
        }
    }
    
    //CORRECTED
    public IT getUserAccount(String username, String password){
        IT employeeAccount = null ;
        if (connection != null) {
            String Query = "SELECT * FROM employee_account where username = ? and password = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,username);
                preparedStatement.setString(2,password);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if(resultSet.next()){
                    employeeAccount = new IT();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = getByRolesId(roleID);
                    employeeAccount.setUserRole(role);
                    
                    int empID = resultSet.getInt("employee_id");
                    Person employeeDetails = hrService.getByEmpID(empID);
                    employeeAccount.setEmpDetails(employeeDetails);
                    employeeAccount.setEmpID(empID);
                    
                }
                 
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return employeeAccount;
    }
    
    //CORRECTED
    public IT getByEmpID(int empID){
        IT employeeAccount = null;
        if (connection != null) {
            String Query = "SELECT * FROM public.employee_account where employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();

                // CORRECTED: check if a row exists first
                if(resultSet.next()){
                    employeeAccount = new IT(); // Create the object now that we know there's data
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));

                    int roleID = resultSet.getInt("role_id");
                    UserRole role = getByRolesId(roleID);
                    employeeAccount.setUserRole(role);

                    Person employeeDetails = hrService.getByEmpID(empID);
                    employeeAccount.setEmpDetails(employeeDetails);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return employeeAccount;
    }
    
    //ERROR
    /*public IT getByEmpID(int empID){
        IT employeeAccount = null ;
            if (connection != null) {
            String Query = "SELECT * FROM public.employee_account where employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeAccount = new IT();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = getByRolesId(roleID);
                    employeeAccount.setUserRole(role);

                    Person employeeDetails = hrService.getByEmpID(empID);
                    employeeAccount.setEmpDetails(employeeDetails);
                }
                
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return employeeAccount;
    }*/
    
    public void updateEmployeeCredentials(IT empAccount){
        if(connection !=null){
            String Query = "UPDATE public.employee_account SET username = ?, password = ? WHERE employee_id = ?";
        
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empAccount.getEmpUserName());
                preparedStatement.setString(2,empAccount.getEmpPassword());
                preparedStatement.setInt(3,empAccount.getEmpID());
                

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }                                   
        }
    }
    
    public void updateEmployeeAccountWithRole(IT empAccount) {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not established.");
        }

        Integer roleId = (empAccount.getUserRole() != null) ? empAccount.getUserRole().getId() : null;
        String query = "UPDATE public.employee_account SET username = ?, password = ?, role_id = ? WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, empAccount.getEmpUserName());
            preparedStatement.setString(2, empAccount.getEmpPassword());

            if (roleId != null) {
                preparedStatement.setInt(3, roleId);
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }

            preparedStatement.setInt(4, empAccount.getEmpID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
    
     
    public List<IT> getAllUserAccount(){
        List<IT> allEmployeeAccount = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.employee_account";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    IT employeeAccount = new IT();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = getByRolesId(roleID);
                    employeeAccount.setUserRole(role);

                    int empID = resultSet.getInt("employee_id");
                    Person employeeDetails = hrService.getByEmpID(empID);
                    employeeAccount.setEmpDetails(employeeDetails);
                    allEmployeeAccount.add(employeeAccount);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }   
        }                          
        return allEmployeeAccount;
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

    public boolean employeeExistsByName(Person empDetails) {
        if (connection == null) {
        }
        String query = "SELECT COUNT(*) FROM public.employee WHERE firstname = ? AND lastname = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, empDetails.getFirstName());
            preparedStatement.setString(2, empDetails.getLastName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Returns true if an employee exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Employee does not exist
    }
    
    public Person saveUserAccount(IT empAccount, Person empDetails) throws SQLException {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not established.");
        }

        String query = "INSERT INTO public.employee_account (employee_id, username, password, role_id) VALUES (?, ?, ?, ?)";

        // Remove the try-catch block to let exceptions propagate up
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, empDetails.getEmpID());
        preparedStatement.setString(2, empAccount.getEmpUserName());
        preparedStatement.setString(3, empAccount.getEmpPassword());
        preparedStatement.setInt(4, 2); // Default role_id = 2

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empAccount.setAccountID(generatedKeys.getInt(1));
                } else {
                    // It's better to throw an exception here if the ID isn't returned
                    throw new SQLException("Creating user account failed, no ID obtained.");
                }
            }
        } else {
            throw new SQLException("Creating user account failed, no rows affected.");
        }

        // Don't forget to close the preparedStatement
        preparedStatement.close();

        return empDetails;
    }
    
    public void deleteEmpAccount(int empID){
        if (connection != null) {
            String Query = "delete from public.employee_account where employee_id =?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,empID);    
                
                preparedStatement.executeUpdate();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }           
        }                              
    }   
       
    
    public void changePassword(IT empAccount){
        if(connection !=null){
            String Query = "UPDATE public.employee_account SET password = ? where employee_id = ?";
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empAccount.getEmpPassword());
                preparedStatement.setInt(2,empAccount.getEmpID());

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }                                   
        }
    }
   
}
