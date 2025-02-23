/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;
import com.payroll.domain.EmployeeDetails;
import com.payroll.domain.EmployeeHours;
import com.payroll.domain.EmployeePosition;
import com.payroll.domain.EmployeeStatus;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.sql.Types;
import javax.swing.JComboBox;

/**
 *
 * @author leniejoice
 */
public class EmployeeDetailsService {
    
    private Connection connection;
    private DatabaseConnection dbConnection;
    
    public EmployeeDetailsService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();   
    }

    public EmployeeDetailsService() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public EmployeeDetails getByEmpID(int empID){
        return getByEmpID(empID,true);
    } 
    
    public EmployeeDetails getByEmpID(int empID, boolean fetchSupervisor){
        EmployeeDetails employeeDetails = null ;
        if (connection != null) {
            String Query = "SELECT * FROM employee where employee_id = ?";
            ResultSet resultSet = null;
            PreparedStatement preparedStatement =null;
            try {
                preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    employeeDetails = toEmployeeDetails(resultSet, fetchSupervisor);
                    
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }   
        }                          
        return employeeDetails;
    } 
    
    public EmployeePosition getPositionById(int id){
        EmployeePosition empPosition = null ;
        if (connection != null) {
            String Query = "SELECT * FROM public.employee_position where id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                empPosition = new EmployeePosition();
                while (resultSet.next()) {
                    empPosition.setId(resultSet.getInt("id"));
                    empPosition.setPosition(resultSet.getString("position"));
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return empPosition;
    }
    
    public EmployeeStatus getStatusById(int id){
        EmployeeStatus empStatus = null ;
        if (connection != null) {
            String Query = "SELECT * FROM public.employee_status where id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,id);
                ResultSet resultSet = preparedStatement.executeQuery();
                empStatus = new EmployeeStatus();
                while (resultSet.next()) {
                    empStatus.setId(resultSet.getInt("id"));
                    empStatus.setStatus(resultSet.getString("status"));
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }           
        }                          
        return empStatus;
    }
    
    public EmployeePosition loadPositionData(JComboBox<String> positionComboBox){
     
        return null;
     
    }
    
    public EmployeeDetails updateEmployeeDetails(EmployeeDetails empDetails){
         java.sql.Date birthDate = empDetails.getEmpBirthday()!=null? new java.sql.Date(empDetails.getEmpBirthday().getTime()):null;
         Integer superVisorId = empDetails.getEmpImmediateSupervisor() != null ? empDetails.getEmpImmediateSupervisor().getEmpID() : null;
         Integer positionId = empDetails.getEmpPosition() != null ? empDetails.getEmpPosition().getId() : null;
         Integer statusId = empDetails.getEmpStatus() != null ? empDetails.getEmpStatus().getId() : null;
         
         
            if (connection != null) {
            String Query = "UPDATE public.employee \n"
                    + "SET \n"
                    + "    lastname = ?,\n"
                    + "    firstname = ?,\n"
                    + "    birthday = ?,\n"
                    + "    address = ?,\n"
                    + "    phone_number = ?,\n"
                    + "    sss = ?,\n"
                    + "    philhealth = ?,\n"
                    + "    tin = ?,\n"
                    + "    pag_ibig = ?,\n"
                    + "    status = ?,\n"
                    + "    position = ?,\n"
                    + "    immediate_supervisor = ?,\n"
                    + "    basic_salary = ?,\n"
                    + "    rice_subsidy = ?,\n"
                    + "    phone_allowance = ?,\n"
                    + "    clothing_allowance = ?,\n"
                    + "    gross_semi_monthly_rate = ?,\n"
                    + "    hourly_rate = ?\n"
                    + "WHERE employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empDetails.getLastName());
                preparedStatement.setString(2,empDetails.getFirstName());
                preparedStatement.setDate(3,birthDate); 
                preparedStatement.setString(4,empDetails.getEmpAddress());
                preparedStatement.setString(5,empDetails.getEmpPhoneNumber());
                preparedStatement.setString(6,empDetails.getEmpSSS());
                preparedStatement.setLong(7,empDetails.getEmpPhilHealth());
                preparedStatement.setString(8,empDetails.getEmpTIN());
                preparedStatement.setLong(9,empDetails.getEmpPagibig());
                preparedStatement.setInt(10, statusId);
                preparedStatement.setInt(11, positionId);
                preparedStatement.setInt(12, superVisorId);
                preparedStatement.setDouble(13,empDetails.getEmpBasicSalary());
                preparedStatement.setDouble(14,empDetails.getEmpRice());
                preparedStatement.setDouble(15,empDetails.getEmpPhone());
                preparedStatement.setDouble(16,empDetails.getEmpClothing());
                preparedStatement.setDouble(17,empDetails.getEmpMonthlyRate());
                preparedStatement.setDouble(18,empDetails.getEmpHourlyRate());
                preparedStatement.setInt(19, empDetails.getEmpID());
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return empDetails;
    }
    
    public EmployeeDetails saveEmployeeDetails(EmployeeDetails empDetails){
         java.sql.Date birthDate = empDetails.getEmpBirthday()!=null? new java.sql.Date(empDetails.getEmpBirthday().getTime()):null;
         Integer superVisorId = empDetails.getEmpImmediateSupervisor() != null ? empDetails.getEmpImmediateSupervisor().getEmpID() : null;
         Integer positionId = empDetails.getEmpPosition() != null ? empDetails.getEmpPosition().getId() : null;
         Integer statusId = empDetails.getEmpStatus() != null ? empDetails.getEmpStatus().getId() : null;
         
         
            if (connection != null) {
            String Query = "INSERT into public.employee (lastname, firstname,birthday,address,phone_number,sss,philhealth,tin,pag_ibig,status,position,immediate_supervisor,basic_salary,rice_subsidy,phone_allowance,clothing_allowance,gross_semi_monthly_rate,hourly_rate)"
                    + "values(?, ?, ?, ?,?, ?, ?, ?,?, ?, ?, ?,?, ?, ?, ?,?,?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query,Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1,empDetails.getLastName());
                preparedStatement.setString(2,empDetails.getFirstName());
                preparedStatement.setDate(3,birthDate);
                preparedStatement.setString(4,empDetails.getEmpAddress());
                preparedStatement.setString(5,empDetails.getEmpPhoneNumber());
                preparedStatement.setString(6,empDetails.getEmpSSS());
                preparedStatement.setLong(7,empDetails.getEmpPhilHealth());
                preparedStatement.setString(8,empDetails.getEmpTIN());
                preparedStatement.setLong(9,empDetails.getEmpPagibig());
                preparedStatement.setObject(10, statusId, Types.INTEGER);
                preparedStatement.setObject(11, positionId,Types.INTEGER);
                preparedStatement.setObject(12, superVisorId,Types.INTEGER);
                preparedStatement.setDouble(13,empDetails.getEmpBasicSalary());
                preparedStatement.setDouble(14,empDetails.getEmpRice());
                preparedStatement.setDouble(15,empDetails.getEmpPhone());
                preparedStatement.setDouble(16,empDetails.getEmpClothing());
                preparedStatement.setDouble(17,empDetails.getEmpMonthlyRate());
                preparedStatement.setDouble(18,empDetails.getEmpHourlyRate());
                
         
                int affectedrows = preparedStatement.executeUpdate();
                    if(affectedrows > 0){
                     try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            empDetails.setEmpID(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Updating user failed, no ID obtained.");
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
    
    public boolean deleteEmpDetails(int empID) {
        boolean isDeleted = false;

        if (connection != null) {
            String query = "DELETE FROM public.employee WHERE employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, empID);

                int affectedRows = preparedStatement.executeUpdate();
                
                if (affectedRows > 0) {
                    isDeleted = true;
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }  
        }
        return isDeleted;
    }
    
    public List<EmployeeDetails> getAllEmployee(){
        List<EmployeeDetails> allEmployee = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM employee order by employee_id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    EmployeeDetails employeeDetails = toEmployeeDetails(resultSet);
                    allEmployee.add(employeeDetails);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return allEmployee;
 
    }
    
    public List<EmployeePosition> getAllPosition(){
        List<EmployeePosition> positions = new ArrayList<>();
        if (connection != null) {
        String Query = "SELECT * FROM employee_position";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    EmployeePosition position = new EmployeePosition();
                    position.setId(resultSet.getInt("id"));
                    position.setPosition(resultSet.getString("position"));
                    positions.add(position);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return positions;
    }
    
     public List<EmployeeStatus> getAllStatuses(){
        List<EmployeeStatus> statuses = new ArrayList<>();
        if (connection != null) {
        String Query = "SELECT * FROM employee_status";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    EmployeeStatus status = new EmployeeStatus();
                    status.setId(resultSet.getInt("id"));
                    status.setStatus(resultSet.getString("status"));
                    statuses.add(status);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return statuses;
    }
    
    
    
    public EmployeeDetails getByEmpPhilHealthID(long empPhilHealthID){
        EmployeeDetails employeeDetails = null ;
            if (connection != null) {
            String Query = "SELECT * FROM employee where philhealth = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setLong(1,empPhilHealthID);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeDetails = toEmployeeDetails (resultSet);
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return employeeDetails;
    } 
    
    public EmployeeDetails getByEmpSSS(String empSSS){
        EmployeeDetails employeeDetails = null ;
            if (connection != null) {
            String Query = "SELECT * FROM employee where sss = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empSSS);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeDetails = toEmployeeDetails (resultSet);
                }
                
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return employeeDetails;
    } 
    
    public EmployeeDetails getByEmpTIN(String empTIN){
        EmployeeDetails employeeDetails = null ;
            if (connection != null) {
            String Query = "SELECT * FROM employee where tin = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empTIN);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeDetails = toEmployeeDetails (resultSet);
                }
                
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }      
        }                          
        return employeeDetails;
    } 
    
    public EmployeeDetails getByEmpPagIbig(long empPagIbig){
        EmployeeDetails employeeDetails = null ;
            if (connection != null) {
            String Query = "SELECT * FROM employee where pag_ibig = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setLong(1,empPagIbig);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                    employeeDetails = toEmployeeDetails (resultSet);
                }
                
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }        
        }                          
        return employeeDetails;
    } 

    public List<EmployeeHours> getEmpHoursByEmpID(int empID){
        List<EmployeeHours> empHours = new ArrayList<>();
        if (connection != null) {
            String Query = "SELECT * FROM employee_hours where employee_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1,empID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    EmployeeHours e = new EmployeeHours();
                    e.setEmpID(resultSet.getInt("employee_id"));
                    e.setDate(resultSet.getDate("date"));
                    e.setId(resultSet.getInt("id"));
                    e.setTimeIn(resultSet.getObject("time_in", LocalTime.class));
                    e.setTimeOut(resultSet.getObject("time_out", LocalTime.class));
                    empHours.add(e);
                          
                }          
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }          
        }                          
        return empHours;
    } 
    
    private EmployeeDetails toEmployeeDetails(ResultSet resultSet, boolean fetchSupervisor) 
        throws SQLException {
        EmployeeDetails employeeDetails = new EmployeeDetails();
            employeeDetails.setEmpID(resultSet.getInt("employee_id"));
            employeeDetails.setFirstName(resultSet.getString("firstname"));
            employeeDetails.setLastName(resultSet.getString("lastname"));
            employeeDetails.setEmpBirthday(resultSet.getDate("birthday"));
            employeeDetails.setEmpAddress(resultSet.getString("address"));
            employeeDetails.setEmpPhoneNumber(resultSet.getString("phone_number"));
            employeeDetails.setEmpSSS(resultSet.getString("sss"));
            employeeDetails.setEmpPhilHealth(resultSet.getLong("philhealth"));
            employeeDetails.setEmpTIN(resultSet.getString("tin"));
            employeeDetails.setEmpPagibig(resultSet.getLong("pag_ibig"));
            employeeDetails.setEmpBasicSalary(resultSet.getDouble("basic_salary"));
            employeeDetails.setEmpRice(resultSet.getDouble("rice_subsidy"));
            employeeDetails.setEmpPhone(resultSet.getDouble("phone_allowance"));
            employeeDetails.setEmpClothing(resultSet.getDouble("clothing_allowance"));
            employeeDetails.setEmpMonthlyRate(resultSet.getDouble("gross_semi_monthly_rate"));
            employeeDetails.setEmpHourlyRate(resultSet.getDouble("hourly_rate"));
            int superVisorId  = resultSet.getInt("immediate_supervisor");
            if (superVisorId > 0 && fetchSupervisor){
                EmployeeDetails superVisor = getByEmpID(superVisorId, false);
                employeeDetails.setEmpImmediateSupervisor(superVisor);
            }       
            int positionId  = resultSet.getInt("position");
            if (positionId > 0){
                EmployeePosition position = getPositionById(positionId);
                employeeDetails.setEmpPosition(position);
            }    
            int statusId  = resultSet.getInt("status");
            if (statusId > 0){
                EmployeeStatus status = getStatusById(statusId);
                employeeDetails.setEmpStatus(status);
            }
        return employeeDetails;
    }
    private EmployeeDetails toEmployeeDetails(ResultSet resultSet) 
        throws SQLException {
        return toEmployeeDetails(resultSet, true);
    }
        
}