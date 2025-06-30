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
import java.sql.CallableStatement;
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

    public EmployeeService(Connection connection) {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging in production
        }
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
            String Query = "SELECT * FROM public.leave_types WHERE id = ?";
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
            String Query = "SELECT * FROM public.employee_leave WHERE employee_id = ?";
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
        String query = "SELECT COUNT(*) FROM public.vw_employee_timein_today WHERE employee_id = ?";

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
        String query = "SELECT COUNT(*) FROM public.vw_employee_hours_today WHERE employee_id = ?";

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
    
    public void autoFillLastUnclosedTimeOut(int empID) throws SQLException {
        if (connection == null) {
            throw new IllegalStateException("Database connection is not initialized.");
        }

        String call = "CALL auto_fill_last_unclosed_timeout(?)";

        try (CallableStatement callableStatement = connection.prepareCall(call)) {
            callableStatement.setInt(1, empID);
            callableStatement.execute();
            System.out.println("Auto-filled last missing time-out for employee " + empID);
        }
    }

    
    public Employee timeOut(Employee attendanceDetails) {
        if (connection == null) {
              throw new IllegalStateException("Database connection is not initialized.");
          }

          String call = "CALL record_employee_timeout(?)";

          try (CallableStatement callableStatement = connection.prepareCall(call)) {
              callableStatement.setInt(1, attendanceDetails.getEmpID());
              callableStatement.execute();

              System.out.println("Procedure executed for time-out: Employee ID " + attendanceDetails.getEmpID());

          } catch (SQLException e) {
              e.printStackTrace();
          }

          return attendanceDetails;
      }
    
    public void deleteAttendanceRecords(int empID) {
        if (connection == null) {
              throw new IllegalStateException("Database connection is not initialized.");
          }

          String call = "CALL delete_attendance_records(?)";

          try (CallableStatement callableStatement = connection.prepareCall(call)) {
              callableStatement.setInt(1, empID);
              callableStatement.execute();
              System.out.println("Deletion procedure executed for employee ID: " + empID);
          } catch (SQLException e) {
              e.printStackTrace();
          }
      }
    
    
    public HR addLeaveRequest(HR leaveDetails){
        if (connection == null) {
            System.err.println("Database connection is not initialized.");
            return leaveDetails;
        }

        java.sql.Date dateFrom = leaveDetails.getDateFrom() != null
                ? new java.sql.Date(leaveDetails.getDateFrom().getTime())
                : null;
        java.sql.Date dateTo = leaveDetails.getDateTo() != null
                ? new java.sql.Date(leaveDetails.getDateTo().getTime())
                : null;
        Integer leaveTypeId = (leaveDetails.getLeaveType() != null)
                ? leaveDetails.getLeaveType().getId()
                : null;

        String call = "CALL add_leave_request(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (CallableStatement stmt = connection.prepareCall(call)) {
            stmt.setString(1, leaveDetails.getSubject());
            if (leaveTypeId != null) {
                stmt.setInt(2, leaveTypeId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setDate(3, dateFrom);
            stmt.setDate(4, dateTo);
            stmt.setInt(5, leaveDetails.getTotalDays());
            stmt.setString(6, leaveDetails.getReason());
            stmt.setString(7, leaveDetails.getStatus().name());
            stmt.setInt(8, leaveDetails.getEmpID());
            stmt.setInt(9, leaveDetails.getApproverId());

            // Register the OUT parameter (10th) for leave_id
            stmt.registerOutParameter(10, Types.INTEGER);
            /*System.out.println("Calling procedure with values: " + leaveDetails);
            System.out.println("Prepared statement class: " + stmt.getClass().getName());
            System.out.println("Calling add_leave_request with parameters:");
            System.out.println("Subject: " + leaveDetails.getSubject());
            System.out.println("Type ID: " + leaveTypeId);
            System.out.println("Date From: " + dateFrom);
            System.out.println("Date To: " + dateTo);
            System.out.println("Total Days: " + leaveDetails.getTotalDays());
            System.out.println("Reason: " + leaveDetails.getReason());
            System.out.println("Status: " + leaveDetails.getStatus().name());
            System.out.println("Employee ID: " + leaveDetails.getEmpID());
            System.out.println("Approver ID: " + leaveDetails.getApproverId());*/
            stmt.execute();

            int generatedId = stmt.getInt(10);
            if (generatedId > 0) {
                leaveDetails.setLeaveId(generatedId);
                System.out.println("Leave request created with ID: " + generatedId);
            } else {
                System.err.println("Leave request was inserted, but no ID was returned.");
            }

        } catch (SQLException e) {
            System.err.println("Error creating leave request:");
            e.printStackTrace();
        }

        return leaveDetails;
    }
    
    public void deleteLeaveRequest(int id){
        if (connection != null) {
                String call = "CALL delete_leave_request(?)";
                 try (CallableStatement stmt = connection.prepareCall(call)) {
                   stmt.setInt(1, id);
                   stmt.execute();
                   System.out.println("Leave request with ID " + id + " has been deleted.");
                } catch (SQLException e) {
                   e.printStackTrace();
                }
        }
    }  
    
    public void deleteLeaveRequestbyEmpID(int empID){
        if (connection != null) {
            String call = "CALL delete_leave_requests_by_emp_id(?)";
            try (CallableStatement stmt = connection.prepareCall(call)) {
                 stmt.setInt(1, empID);
                 stmt.execute();
                 System.out.println("Leave requests for employee ID " + empID + " have been deleted.");
            } catch (SQLException e) {
                 e.printStackTrace();
            }
        }
    } 
    
    public List<HR> getLeavesByEmployee(int empID){
        List<HR> allLeaves = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM vw_leave_requests WHERE employee_id = ? ORDER BY id ASC";
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
            String Query = "SELECT * FROM vw_leave_requests WHERE status = ? ORDER BY id ASC";
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
    
    public void updateLeaveRequestStatus(LeaveStatus leaveStatus, int id, int approverId){
        if (connection != null) {
            String call = "CALL update_leave_request_status(?, ?, ?)";
            try (CallableStatement stmt = connection.prepareCall(call)) {
                stmt.setString(1, leaveStatus.name());
                stmt.setInt(2, approverId);
                stmt.setInt(3, id);
                stmt.execute();
                System.out.println("Leave request ID " + id + " updated to " + leaveStatus.name());
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
            leaveDetails.setApproverId(resultSet.getInt("approver_id"));
            
            int leaveTypeId  = resultSet.getInt("type");
            if (leaveTypeId > 0){
                LeaveType type = getLeaveTypeById(leaveTypeId);
                leaveDetails.setLeaveType(type);
            }   
       
        return leaveDetails;
    }
    
    
}
