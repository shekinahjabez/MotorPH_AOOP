/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;

import com.payroll.domain.LeaveBalance;
import com.payroll.domain.LeaveDetails;
import com.payroll.domain.LeaveDetails.LeaveStatus;
import com.payroll.domain.LeaveType;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class LeaveDetailsService {
    private Connection connection;
    private DatabaseConnection dbConnection;
    
    
    public LeaveDetailsService(DatabaseConnection dbConnection){
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
    
    public LeaveDetails saveLeave(LeaveDetails leaveDetails){
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
            preparedStatement.setString(7,leaveDetails.getStatus());
            preparedStatement.setInt(8,leaveDetails.getEmpID());


            int affectedrows = preparedStatement.executeUpdate();
                if(affectedrows > 0){
                 try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leaveDetails.setId(generatedKeys.getInt(1));
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
    
    public List<LeaveDetails> getLeavesByEmployee(int empID){
        List<LeaveDetails> allLeaves = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.leave_details where employee_id = ? order by id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    LeaveDetails leaveDetails = toLeaveDetails(resultSet);
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
    
    public List<LeaveDetails> getAllLeaveRequestByStatus(LeaveStatus leaveStatus){
        List<LeaveDetails> allLeaveRequest = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM public.leave_details  where status = ? order by id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,leaveStatus.name());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    LeaveDetails leaveDetails = toLeaveDetails(resultSet);
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
    
    
    private LeaveDetails toLeaveDetails(ResultSet resultSet) 
        throws SQLException {
        LeaveDetails leaveDetails = new LeaveDetails();
            leaveDetails.setId(resultSet.getInt("id"));
            leaveDetails.setEmpID(resultSet.getInt("employee_id"));
            leaveDetails.setSubject(resultSet.getString("subject"));
            leaveDetails.setDateFrom(resultSet.getDate("date_from"));
            leaveDetails.setDateTo(resultSet.getDate("date_to"));
            leaveDetails.setTotalDays(resultSet.getInt("total_days"));
            leaveDetails.setReason(resultSet.getString("reason"));
            leaveDetails.setStatus(resultSet.getString("status"));
            
            int leaveTypeId  = resultSet.getInt("type");
            if (leaveTypeId > 0){
                LeaveType type = getLeaveTypeById(leaveTypeId);
                leaveDetails.setLeaveType(type);
            }   
       
        return leaveDetails;
    }
    
    
}
