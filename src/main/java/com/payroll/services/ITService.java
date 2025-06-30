/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.subdomain.UserRole;
import com.payroll.util.DatabaseConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<IT> getAllUserAccount() {
    List<IT> userAccounts = new ArrayList<>();

    if (connection != null) {
        String query = "SELECT * FROM employee_account";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                IT employeeAccount = new IT();
                employeeAccount.setAccountID(resultSet.getInt("account_id"));
                employeeAccount.setEmpUserName(resultSet.getString("username"));
                employeeAccount.setEmpPassword(resultSet.getString("password"));

                int roleID = resultSet.getInt("role_id");
                UserRole role = getByRolesId(roleID); // same as in getUserAccount
                employeeAccount.setUserRole(role);

                int empID = resultSet.getInt("employee_id");
                Person employeeDetails = hrService.getByEmpID(empID); // full employee info
                employeeAccount.setEmpDetails(employeeDetails);
                employeeAccount.setEmpID(empID);

                userAccounts.add(employeeAccount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return userAccounts;
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
    
    public List<String> getMissingRolesAfterUpdate(int empId, String newRoleName) {
        List<String> requiredRoles = Arrays.asList("HR", "Finance", "IT");
        Map<String, Integer> roleCounts = new HashMap<>();

        String query = "SELECT role, assigned_count FROM vw_role_assignment_count";


        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String role = resultSet.getString("role");
                int count = resultSet.getInt("assigned_count");
                roleCounts.put(role, count);
            }

            // Step 2: Get the current role of the employee being updated
            String currentRoleQuery = "SELECT role FROM vw_employee_roles WHERE employee_id = ?";
    

            try (PreparedStatement roleStmt = connection.prepareStatement(currentRoleQuery)) {
                roleStmt.setInt(1, empId);
                ResultSet roleRs = roleStmt.executeQuery();

                if (roleRs.next()) {
                    String currentRole = roleRs.getString("role");

                    // If changing to a new role and that role is different
                    if (!newRoleName.equalsIgnoreCase(currentRole)) {
                        // Decrease the count of the current role by 1 (as if the change happened)
                        roleCounts.put(currentRole, roleCounts.getOrDefault(currentRole, 0) - 1);
                        // Increase the new role count
                        roleCounts.put(newRoleName, roleCounts.getOrDefault(newRoleName, 0) + 1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Step 3: Check if any required role has 0 or less
        List<String> missingRoles = new ArrayList<>();
        for (String role : requiredRoles) {
            if (roleCounts.getOrDefault(role, 0) < 1) {
                missingRoles.add(role);
            }
        }

        return missingRoles;
    }
    
    public void updateEmployeeAccountWithRole(IT empAccount) {
        if (connection == null) {
             throw new IllegalStateException("Database connection is not established.");
         }

         Integer roleId = (empAccount.getUserRole() != null) ? empAccount.getUserRole().getId() : null;

         String sql = "CALL update_employee_account_with_role(?, ?, ?, ?)";

         try (CallableStatement stmt = connection.prepareCall(sql)) {
             stmt.setString(1, empAccount.getEmpUserName());
             stmt.setString(2, empAccount.getEmpPassword());

             if (roleId != null) {
                 stmt.setInt(3, roleId);
             } else {
                 stmt.setNull(3, java.sql.Types.INTEGER);
             }

             stmt.setInt(4, empAccount.getEmpID());

             stmt.execute();
         } catch (SQLException e) {
             e.printStackTrace();
         }
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

   /* public boolean employeeExistsByName(Person empDetails) {
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
    }*/
    
    public Person saveUserAccount(IT empAccount, Person empDetails) throws SQLException {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not established.");
        }

        String sql = "CALL save_user_account(?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, empDetails.getEmpID());
            stmt.setString(2, empAccount.getEmpUserName());
            stmt.setString(3, empAccount.getEmpPassword());

            stmt.registerOutParameter(4, java.sql.Types.INTEGER);

            stmt.execute();

            int accountId = stmt.getInt(4);
            empAccount.setAccountID(accountId);

        } catch (SQLException e) {
            System.err.println("Error calling save_user_account:");
            e.printStackTrace();
            throw e;
        }

        return empDetails;
    }
    
    public void deleteEmpAccount(int empID){
        if (connection != null) {
            String sql = "CALL delete_emp_account(?)";
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, empID);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
       
    
    public void changePassword(IT empAccount){
        if (connection != null) {
            String sql = "CALL update_emp_account_password(?, ?)";
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, empAccount.getEmpID());
                stmt.setString(2, empAccount.getEmpPassword());
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}