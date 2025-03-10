/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.Employee;
import com.payroll.domain.LeaveBalance;
import com.payroll.domain.HR;
import com.payroll.domain.HR.LeaveStatus;
import com.payroll.subdomain.LeaveType;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class EmployeeService {
    private Connection connection;
    
    
    public EmployeeService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();    
    }
    
    public List<LeaveType> getAllLeaveTypes(){
        List<LeaveType> leaveTypes = new ArrayList<>();
        if (connection != null) {
        String Query = "SELECT * FROM public.leave_types";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    LeaveType leaveType = new LeaveType();
                    leaveType.setId(resultSet.getInt("id"));
                    leaveType.setLeaveType(resultSet.getString("leave_type"));
                    leaveTypes.add(leaveType);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return leaveTypes;
    }
    
    public LeaveType getLeaveTypeById(int id){
        LeaveType leaveType = null ;
        if (connection != null) {
            String Query = "SELECT * FROM public.leave_types where id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                leaveType = new LeaveType();
                while (resultSet.next()) {
                    leaveType.setId(resultSet.getInt("id"));
                    leaveType.setLeaveType(resultSet.getString("leave_type"));
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }           
        }                          
        return leaveType;
    }
    
    public void saveLeaveBalance(LeaveBalance leaveBalance){
        if (connection != null) {
            String Query = "INSERT into public.employee_leave (employee_id, taken, available, total) values(?, ?,?,?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,leaveBalance.getEmpID());
                preparedStatement.setInt(2,leaveBalance.getTaken());
                preparedStatement.setInt(3,leaveBalance.getAvailable());
                preparedStatement.setInt(4,leaveBalance.getTotal());
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }         
        }
    }
    
    public void deleteLeaveBalance(int empID){
       if (connection != null) {
            String Query = "delete from public.employee_leave where employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }              
       }  
    }
    
    public void updateLeaveBalance(LeaveBalance leaveBalance){
        if (connection != null) {
            String Query = "update public.employee_leave set taken = ?, available = ?, total = ? where employee_id =?;";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,leaveBalance.getTaken());
                preparedStatement.setInt(2,leaveBalance.getAvailable());
                preparedStatement.setInt(3,leaveBalance.getTotal());
                preparedStatement.setInt(4,leaveBalance.getEmpID());
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }
        
    }
    
    
    public LeaveBalance getLeaveBalance(int empID){
        LeaveBalance leaveBalance = new LeaveBalance();
        if (connection != null) {
            String Query = "select * from public.employee_leave where employee_id =?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
               
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    
                    leaveBalance.setEmpID(resultSet.getInt("employee_id"));
                    leaveBalance.setTaken(resultSet.getInt("taken"));
                    leaveBalance.setAvailable(resultSet.getInt("available"));
                    leaveBalance.setTotal(resultSet.getInt("total"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }
        
        return leaveBalance;
        
    }
    
    
    public Employee timeIn(Employee attendanceDetails) throws SQLException {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }

        String query = "INSERT INTO public.employee_hours (employee_id, date, time_in) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            LocalDate currentDate = LocalDate.now();                 
            LocalTime currentTime = LocalTime.now();                

            java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);   
            java.sql.Time sqlTimeIn = java.sql.Time.valueOf(currentTime); 

            preparedStatement.setInt(1, attendanceDetails.getEmpID());    
            preparedStatement.setDate(2, sqlDate);                        
            preparedStatement.setTime(3, sqlTimeIn);                      
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attendanceDetails.setAttendanceId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Time-in failed, no ID obtained.");
                    }
                }
            } else {
                throw new SQLException("Time-in failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();  
            throw e;              
        }

        return attendanceDetails;
    }
    
    public boolean hasTimeInToday(int empID) throws SQLException {
        String query = "SELECT COUNT(*) FROM public.employee_hours WHERE employee_id = ? AND date = CURRENT_DATE";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public boolean hasTimeOutToday(int empID) throws SQLException {
        String query = "SELECT COUNT(*) FROM public.employee_hours WHERE employee_id = ? AND date = CURRENT_DATE AND time_out IS NOT NULL";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    
    public Employee timeOut(Employee attendanceDetails) {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }

        String query = "UPDATE public.employee_hours " +
                       "SET time_out = ? " +
                       "WHERE employee_id = ? AND date = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            LocalTime currentTime = LocalTime.now();
            java.sql.Time sqlTimeOut = java.sql.Time.valueOf(currentTime);  

            LocalDate currentDate = LocalDate.now();
            java.sql.Date sqlDate = java.sql.Date.valueOf(currentDate);

            preparedStatement.setTime(1, sqlTimeOut);                       
            preparedStatement.setInt(2, attendanceDetails.getEmpID());      
            preparedStatement.setDate(3, sqlDate);                         

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("No time-in record found for Employee ID " 
                    + attendanceDetails.getEmpID() + " on " + currentDate);
            } else {
                System.out.println("Time-out recorded for Employee ID " 
                    + attendanceDetails.getEmpID() + " at " + currentTime);
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return attendanceDetails; 
    }
    
    
    public HR addLeaveRequest(HR leaveDetails){
        //java.sql.Date birthDate = empDetails.getEmpBirthday()!=null? new java.sql.Date(empDetails.getEmpBirthday().getTime()):null;
        java.sql.Date dateFrom = leaveDetails.getDateFrom()!=null? new java.sql.Date(leaveDetails.getDateFrom().getTime()):null;
        java.sql.Date dateTo = leaveDetails.getDateTo()!=null? new java.sql.Date(leaveDetails.getDateTo().getTime()):null;
        Integer leaveTypeId = leaveDetails.getLeaveType() != null ? leaveDetails.getLeaveType().getId() : null;
       
 
        if (connection != null) {
        String Query = "INSERT into public.leave_details (subject, type,date_from,date_to,total_days,reason,status, employee_id)"
                + "values(?, ?, ?, ?,?, ?, ?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Query,Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,leaveDetails.getSubject());
            preparedStatement.setObject(2,leaveTypeId, Types.INTEGER);
            preparedStatement.setDate(3,dateFrom);
            preparedStatement.setDate(4,dateTo);
            preparedStatement.setInt(5,leaveDetails.getTotalDays());
            preparedStatement.setString(6,leaveDetails.getReason());
            preparedStatement.setString(7,leaveDetails.getStatus().name());
            preparedStatement.setInt(8,leaveDetails.getEmpID());


            int affectedrows = preparedStatement.executeUpdate();
                if(affectedrows > 0){
                 try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leaveDetails.setLeaveId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Leave request failed, no ID obtained.");
                     }
                }
        };
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }         
    }                          
    return leaveDetails;

}
    public void deleteLeaveRequest(int id){
        if (connection != null) {
            String Query = "delete from public.leave_details where id =?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1,id);    
                
                preparedStatement.executeUpdate();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }           
        }                              
    }   
    
    public void deleteLeaveRequestbyEmpID(int empID){
        if (connection != null) {
            String Query = "delete from public.leave_details where employee_id =?";
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
    
    public List<HR> getLeavesByEmployee(int empID){
        List<HR> allLeaves = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.leave_details where employee_id = ? order by id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    HR leaveDetails = toLeaveDetails(resultSet);
                    allLeaves.add(leaveDetails);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return allLeaves;
 
    }
    
    public List<HR> getAllLeaveRequestByStatus(LeaveStatus leaveStatus){
        List<HR> allLeaveRequest = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.leave_details  where status = ? order by id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,leaveStatus.name());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    HR leaveDetails = toLeaveDetails(resultSet);
                    allLeaveRequest.add(leaveDetails);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return allLeaveRequest;
    }
    
    public void updateLeaveRequestStatus(LeaveStatus leaveStatus, int id){
        if (connection != null) {
            String Query = "UPDATE public.leave_details set status = ? where id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,leaveStatus.name());
                preparedStatement.setInt(2,id);
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
    }
    
    
    private HR toLeaveDetails(ResultSet resultSet) 
        throws SQLException {
        HR leaveDetails = new HR();
            leaveDetails.setLeaveId(resultSet.getInt("id"));
            leaveDetails.setEmpID(resultSet.getInt("employee_id"));
            leaveDetails.setSubject(resultSet.getString("subject"));
            leaveDetails.setDateFrom(resultSet.getDate("date_from"));
            leaveDetails.setDateTo(resultSet.getDate("date_to"));
            leaveDetails.setTotalDays(resultSet.getInt("total_days"));
            leaveDetails.setReason(resultSet.getString("reason"));
            leaveDetails.setStatus(LeaveStatus.valueOf(resultSet.getString("status")));
            
            int leaveTypeId  = resultSet.getInt("type");
            if (leaveTypeId > 0){
                LeaveType type = getLeaveTypeById(leaveTypeId);
                leaveDetails.setLeaveType(type);
            }   
       
        return leaveDetails;
    }
    
    
}
