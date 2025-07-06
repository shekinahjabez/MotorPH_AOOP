/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.DAO;
import com.payroll.domain.Person;
import com.payroll.domain.Employee;
import com.payroll.domain.HR.LeaveStatus;
import com.payroll.domain.IT;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import com.payroll.util.DatabaseConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
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
public class HRDAO {
    
    private Connection connection;
    
    public HRDAO(Connection connection) {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging this instead
        }
    }

    
    public Person getByEmpID(int empID){
        return getByEmpID(empID,true);
    } 
    
    public Person getByEmpID(int empID, boolean fetchSupervisor){
        Person employeeDetails = null ;
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
    
    public void updateEmployeeCredentials(IT empAccount){
        if (connection == null) return;

        String call = "CALL update_employee_credentials(?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(call)) {
            stmt.setString(1, empAccount.getEmpUserName());
            stmt.setString(2, empAccount.getEmpPassword());
            stmt.setInt(3, empAccount.getEmpID());

            // Debugging output
            //System.out.println("Executing update_employee_credentials for employee ID: " + empAccount.getEmpID());

            stmt.execute();

            System.out.println("Credentials updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating employee credentials:");
            e.printStackTrace();
        }
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
    
    public Person updateEmployeeDetails(Person empDetails){
        if (connection == null) return null;

        String call = "CALL update_employee_details(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        java.sql.Date birthDate = empDetails.getEmpBirthday() != null
            ? new java.sql.Date(empDetails.getEmpBirthday().getTime())
            : null;

        Integer supervisorId = empDetails.getEmpImmediateSupervisor() != null
            ? empDetails.getEmpImmediateSupervisor().getEmpID()
            : null;

        Integer positionId = empDetails.getEmpPosition() != null
            ? empDetails.getEmpPosition().getId()
            : null;

        Integer statusId = empDetails.getEmpStatus() != null
            ? empDetails.getEmpStatus().getId()
            : null;

        try (CallableStatement stmt = connection.prepareCall(call)) {

            stmt.setString(1, empDetails.getLastName());
            stmt.setString(2, empDetails.getFirstName());
            stmt.setDate(3, birthDate);
            stmt.setString(4, empDetails.getEmpAddress());
            stmt.setString(5, empDetails.getEmpPhoneNumber());
            stmt.setString(6, empDetails.getEmpSSS());
            stmt.setLong(7, empDetails.getEmpPhilHealth());
            stmt.setString(8, empDetails.getEmpTIN());
            stmt.setLong(9, empDetails.getEmpPagibig());

            if (statusId != null) {
                stmt.setInt(10, statusId);
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            if (positionId != null) {
                stmt.setInt(11, positionId);
            } else {
                stmt.setNull(11, java.sql.Types.INTEGER);
            }

            if (supervisorId != null) {
                stmt.setInt(12, supervisorId);
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }

            stmt.setBigDecimal(13, BigDecimal.valueOf(empDetails.getEmpBasicSalary()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(14, BigDecimal.valueOf(empDetails.getEmpRice()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(15, BigDecimal.valueOf(empDetails.getEmpPhone()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(16, BigDecimal.valueOf(empDetails.getEmpClothing()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(17, BigDecimal.valueOf(empDetails.getEmpMonthlyRate()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(18, BigDecimal.valueOf(empDetails.getEmpHourlyRate()).setScale(2, RoundingMode.HALF_UP));
            stmt.setInt(19, empDetails.getEmpID());

            stmt.execute();
            System.out.println("Update successful.");

        } catch (SQLException e) {
            System.err.println("Error calling update_employee_details procedure:");
            e.printStackTrace();
        }

        return empDetails;
    }
    
    public boolean isDuplicateEmployee(Person empDetails) {
        String query = """
            SELECT employee_id FROM public.employee
            WHERE lastname = ? AND firstname = ? AND birthday = ? AND address = ? AND phone_number = ?
            AND sss = ? AND philhealth = ? AND tin = ? AND pag_ibig = ? AND status = ? AND position = ? AND immediate_supervisor = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, empDetails.getLastName());
            preparedStatement.setString(2, empDetails.getFirstName());
            preparedStatement.setDate(3, new java.sql.Date(empDetails.getEmpBirthday().getTime()));
            preparedStatement.setString(4, empDetails.getEmpAddress());
            preparedStatement.setString(5, empDetails.getEmpPhoneNumber());
            preparedStatement.setString(6, empDetails.getEmpSSS());
            preparedStatement.setLong(7, empDetails.getEmpPhilHealth());
            preparedStatement.setString(8, empDetails.getEmpTIN());
            preparedStatement.setLong(9, empDetails.getEmpPagibig());
            // Status
            if (empDetails.getEmpStatus() != null) {
                preparedStatement.setObject(10, empDetails.getEmpStatus().getId(), Types.INTEGER);
            } else {
                preparedStatement.setNull(10, Types.INTEGER);
            }

            // Position
            if (empDetails.getEmpPosition() != null) {
                preparedStatement.setObject(11, empDetails.getEmpPosition().getId(), Types.INTEGER);
            } else {
                preparedStatement.setNull(11, Types.INTEGER);
            }

            // Supervisor
            if (empDetails.getEmpImmediateSupervisor() != null) {
                preparedStatement.setObject(12, empDetails.getEmpImmediateSupervisor().getEmpID(), Types.INTEGER);
            } else {
                preparedStatement.setNull(12, Types.INTEGER);
            }

            try (ResultSet resultset = preparedStatement.executeQuery()) {
                return resultset.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Integer getEmployeeIdIfExists(Person empDetails) {
        String query = """
            SELECT employee_id FROM public.employee
            WHERE lastname = ? AND firstname = ? AND birthday = ? AND address = ? AND phone_number = ?
            AND sss = ? AND philhealth = ? AND tin = ? AND pag_ibig = ? AND status = ? AND position = ? AND immediate_supervisor = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, empDetails.getLastName());
            preparedStatement.setString(2, empDetails.getFirstName());
            preparedStatement.setDate(3, new java.sql.Date(empDetails.getEmpBirthday().getTime()));
            preparedStatement.setString(4, empDetails.getEmpAddress());
            preparedStatement.setString(5, empDetails.getEmpPhoneNumber());
            preparedStatement.setString(6, empDetails.getEmpSSS());
            preparedStatement.setLong(7, empDetails.getEmpPhilHealth());
            preparedStatement.setString(8, empDetails.getEmpTIN());
            preparedStatement.setLong(9, empDetails.getEmpPagibig());
            preparedStatement.setObject(10, empDetails.getEmpStatus().getId(), Types.INTEGER);
            preparedStatement.setObject(11, empDetails.getEmpPosition().getId(), Types.INTEGER);
            preparedStatement.setObject(12, empDetails.getEmpImmediateSupervisor().getEmpID(), Types.INTEGER);

            try (ResultSet resultset = preparedStatement.executeQuery()) {
                if (resultset.next()) {
                    return resultset.getInt("employee_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    public Person addEmployeeDetails(Person empDetails) {
        if (connection == null) return null;

        String sql = "Call add_employee_details(?, ?, ?, ?, ?, ?, ?::bigint, ?, ?::bigint, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, empDetails.getLastName());                       // text
            stmt.setString(2, empDetails.getFirstName());                      // text

            if (empDetails.getEmpBirthday() != null) {
                stmt.setDate(3, new java.sql.Date(empDetails.getEmpBirthday().getTime())); // date
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, empDetails.getEmpAddress());                    // text
            stmt.setString(5, empDetails.getEmpPhoneNumber());                // text
            stmt.setString(6, empDetails.getEmpSSS());                        // text
            stmt.setLong(7, empDetails.getEmpPhilHealth());                   // bigint
            stmt.setString(8, empDetails.getEmpTIN());                        // text
            stmt.setLong(9, empDetails.getEmpPagibig());                      // bigint

            stmt.setInt(10, empDetails.getEmpStatus() != null ? empDetails.getEmpStatus().getId() : java.sql.Types.INTEGER);  // integer
            stmt.setInt(11, empDetails.getEmpPosition() != null ? empDetails.getEmpPosition().getId() : java.sql.Types.INTEGER); // integer
            if (empDetails.getEmpImmediateSupervisor() != null) {
                stmt.setInt(12, empDetails.getEmpImmediateSupervisor().getEmpID()); // integer
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }

            // Use BigDecimal for NUMERIC
            stmt.setBigDecimal(13, BigDecimal.valueOf(empDetails.getEmpBasicSalary()));  // numeric
            stmt.setBigDecimal(14, BigDecimal.valueOf(empDetails.getEmpRice()));
            stmt.setBigDecimal(15, BigDecimal.valueOf(empDetails.getEmpPhone()));
            stmt.setBigDecimal(16, BigDecimal.valueOf(empDetails.getEmpClothing()));
            stmt.setBigDecimal(17, BigDecimal.valueOf(empDetails.getEmpMonthlyRate()));
            stmt.setBigDecimal(18, BigDecimal.valueOf(empDetails.getEmpHourlyRate()));

            // OUT parameter (employee_id)
            stmt.registerOutParameter(19, java.sql.Types.INTEGER);           // OUT integer

            stmt.execute();

            int generatedId = stmt.getInt(19);
            empDetails.setEmpID(generatedId);
            System.out.println("New employee created with ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error calling add_employee_details:");
            e.printStackTrace();
        }

        return empDetails;
    }
    
    public boolean deleteEmployeeDetails(int empID) {
        boolean isDeleted = false;

        if (connection != null) {
            String sql = "Call delete_employee_details(?, ?)";

            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setInt(1, empID); // IN param
                stmt.registerOutParameter(2, java.sql.Types.INTEGER); // OUT param

                stmt.execute();

                int deletedCount = stmt.getInt(2); // Get OUT param
                System.out.println("Deleted count from procedure: " + deletedCount); // Debug
                isDeleted = deletedCount > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isDeleted;
    }
    
    public List<Person> getAllEmployee(){
        List<Person> allEmployee = new ArrayList<>();
            if (connection != null) {
            String Query = "SELECT * FROM employee order by employee_id ASC";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    Person employeeDetails = toEmployeeDetails(resultSet);
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
    
    
    public List<Employee> getEmpHoursByEmpID(int empID) {
        List<Employee> empHours = new ArrayList<>();

        if (connection == null) {
            System.err.println("Database connection is null. Cannot fetch employee hours.");
            return empHours;
        }

        String query = "SELECT * FROM public.vw_employee_hours WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Employee e = new Employee(); 
                    e.setEmpID(resultSet.getInt("employee_id"));
                    e.setDate(resultSet.getDate("date"));
                    e.setAttendanceId(resultSet.getInt("id"));
                    e.setTimeIn(resultSet.getObject("time_in", LocalTime.class));
                    e.setTimeOut(resultSet.getObject("time_out", LocalTime.class));
                    empHours.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return empHours;
    }
    
    private Person toEmployeeDetails(ResultSet resultSet, boolean fetchSupervisor) 
        throws SQLException {
        Person employeeDetails = new Employee();
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
                    Person superVisor = getByEmpID(superVisorId, false);
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
    
    private Person toEmployeeDetails(ResultSet resultSet) 
        throws SQLException {
        return toEmployeeDetails(resultSet, true);
    }
        
}