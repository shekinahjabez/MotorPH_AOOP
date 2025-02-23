/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;
import com.payroll.domain.EmployeeHours;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Computer leniejoice
 */
public class PayrollService {
    private Connection connection;
    
    public PayrollService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();    
    }
    
    
    public List<EmployeeHours> getEmployeeHours(int empID, Date from, Date to){
        List<EmployeeHours> empHours = new ArrayList<>();
        if (connection != null){
            try {
                String Query = "SELECT * FROM public.employee_hours where employee_id = ? and date between ? and ?";
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                preparedStatement.setDate(2,new java.sql.Date(from.getTime()));
                preparedStatement.setDate(3,new java.sql.Date(to.getTime()));
                
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                   Timestamp timeInDate = resultSet.getTimestamp("time_in");
                   LocalTime timeIn = LocalDateTime.ofInstant(timeInDate.toInstant(),ZoneId.systemDefault()).toLocalTime();
                   Timestamp timeOutDate = resultSet.getTimestamp("time_out");
                   LocalTime timeOut = LocalDateTime.ofInstant(timeOutDate.toInstant(),ZoneId.systemDefault()).toLocalTime();
                    
                   if (!timeIn.equals(LocalTime.MIDNIGHT) || !timeOut.equals(LocalTime.MIDNIGHT)) {
                    EmployeeHours employeeHours = new EmployeeHours();
                    employeeHours.setEmpID(resultSet.getInt("employee_id"));
                    employeeHours.setDate(resultSet.getDate("date"));
                    employeeHours.setTimeIn(timeIn);
                    employeeHours.setTimeOut(timeOut);
                    
                   
                   empHours.add(employeeHours);
                    }
                }     
            } catch (SQLException ex) {
                Logger.getLogger(PayrollService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return empHours;   
    }
    
    public float  calculateSssContribution(double empSalary){
        String sssContributionQuery = """
                select contribution from sss
                where (cr_above is null or ? > cr_above)
                and (cr_below is null or ? < cr_below)  
                """;
        float contribution = 0f;
        try (PreparedStatement sssContributionStatement = connection.prepareStatement(sssContributionQuery)) {
            sssContributionStatement.setDouble(1, empSalary);
            sssContributionStatement.setDouble(2, empSalary);

            ResultSet sssContributionResult = sssContributionStatement.executeQuery();
                if (!sssContributionResult.next()) return 0f;
                contribution = sssContributionResult.getFloat("contribution");
            sssContributionResult.close();
        } catch (SQLException e) {
            System.err.println("An error occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return contribution;
    }
}
