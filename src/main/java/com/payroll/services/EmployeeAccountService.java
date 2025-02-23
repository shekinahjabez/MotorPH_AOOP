/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.EmployeeAccount;
import com.payroll.domain.EmployeeDetails;
import com.payroll.domain.UserRole;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.management.Query;

/**
 *
 * @author leniejoice
 */
public class EmployeeAccountService {
    private Connection connection;
    private DatabaseConnection dbConnection;
    private EmployeeDetailsService empDetailsService;
    private EmployeeRolesService empRolesService;
    private LeaveDetailsService leaveDetailsService;
    
    
    public EmployeeAccountService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();  
        this.empDetailsService = new EmployeeDetailsService(dbConnection);
        this.empRolesService = new EmployeeRolesService(dbConnection);
        this.leaveDetailsService = new LeaveDetailsService(dbConnection);
    }

    public EmployeeAccountService() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    

    public EmployeeAccount getUserAccount(String username, String password){
        EmployeeAccount employeeAccount = null ;
        if (connection != null) {
            String Query = "SELECT * FROM employee_account where username = ? and password = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,username);
                preparedStatement.setString(2,password);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                if(resultSet.next()){
                    employeeAccount = new EmployeeAccount();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = empRolesService.getByRolesId(roleID);
                    employeeAccount.setUserRole(role);
                    
                    int empID = resultSet.getInt("employee_id");
                    EmployeeDetails employeeDetails = empDetailsService.getByEmpID(empID);
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
    
    public EmployeeAccount getByEmpID(int empID){
        EmployeeAccount employeeAccount = null ;
            if (connection != null) {
            String Query = "SELECT * FROM public.employee_account where employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeAccount = new EmployeeAccount();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = empRolesService.getByRolesId(roleID);
                    employeeAccount.setUserRole(role);

                    EmployeeDetails employeeDetails = empDetailsService.getByEmpID(empID);
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
    
    public void updateEmployeeAccount(EmployeeAccount empAccount){
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
    
    public void updateEmployeeAccountIT(EmployeeAccount empAccount) {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not established.");
        }

        Integer roleId = (empAccount.getUserRole() != null) ? empAccount.getUserRole().getId() : null;
        String query = "UPDATE public.employee_account SET username = ?, password = ?, role_id = ? WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, empAccount.getEmpUserName());
            preparedStatement.setString(2, empAccount.getEmpPassword());

            // Handle roleId safely
            if (roleId != null) {
                preparedStatement.setInt(3, roleId);
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }

            preparedStatement.setInt(4, empAccount.getEmpID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging instead of printing stack trace
        }
    }
    
     
    public List<EmployeeAccount> getAllUserAccount(){
        List<EmployeeAccount> allEmployeeAccount = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.employee_account";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    EmployeeAccount employeeAccount = new EmployeeAccount();
                    employeeAccount.setAccountID(resultSet.getInt("account_id"));
                    employeeAccount.setEmpUserName(resultSet.getString("username"));
                    employeeAccount.setEmpPassword(resultSet.getString("password"));
                    
                    int roleID = resultSet.getInt("role_id");
                    UserRole role = empRolesService.getByRolesId(roleID);
                    employeeAccount.setUserRole(role);

                    int empID = resultSet.getInt("employee_id");
                    EmployeeDetails employeeDetails = empDetailsService.getByEmpID(empID);
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
   
    
    public EmployeeDetails saveUserAccount(EmployeeAccount empAccount,EmployeeDetails empDetails){
        if (connection != null) {
            String Query = "INSERT into public.employee_account (employee_id, username, password,role_id) VALUES (?, ?, ?,?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,empDetails.getEmpID());                
                preparedStatement.setString(2,empAccount.getEmpUserName());
                preparedStatement.setString(3,empAccount.getEmpPassword());
                preparedStatement.setInt(4,2);   
                int affectedrows = preparedStatement.executeUpdate();
                if(affectedrows > 0){
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                             empAccount.setAccountID(generatedKeys.getInt(1));
                        } else {
                             throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }
                };
                preparedStatement.close();
            } catch (SQLException e) {
                 e.printStackTrace();
            }           
         }                          
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
       
    
    public void changePassword(EmployeeAccount empAccount){
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