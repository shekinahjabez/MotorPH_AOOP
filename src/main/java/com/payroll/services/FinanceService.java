/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.services;
import com.payroll.domain.Employee;
import com.payroll.domain.Finance;
import com.payroll.domain.Person;
import com.payroll.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
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
public class FinanceService {
    private Connection connection;
    
    public FinanceService(DatabaseConnection dbConnection){
        this.connection = dbConnection.connect();    
    }
    
    public Person getByEmpID(int empID) throws SQLException {
        String query = "SELECT * FROM employee WHERE employee_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return toPayrollDetails(resultSet); // ✅ Directly return the object if found
                }
            }
        }
        return null; // ✅ Return null if no employee found
    }
   
    
    public List<Employee> getEmployeeHours(int empID, Date from, Date to){
        List<Employee> empHours = new ArrayList<>();
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
                    Employee attendance = new Employee();
                    attendance.setEmpID(resultSet.getInt("employee_id"));
                    attendance.setDate(resultSet.getDate("date"));
                    attendance.setTimeIn(timeIn);
                    attendance.setTimeOut(timeOut);
                    
                   
                   empHours.add(attendance);
                    }
                }     
            } catch (SQLException ex) {
                Logger.getLogger(FinanceService.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public Person updatePayrollDetails(Person empDetails){
            if (connection != null) {
            String Query = "UPDATE public.employee \n"
                    + "SET \n"
                    + "    sss = ?,\n"
                    + "    philhealth = ?,\n"
                    + "    tin = ?,\n"
                    + "    pag_ibig = ?,\n"
                    + "    basic_salary = ?,\n"
                    + "    rice_subsidy = ?,\n"
                    + "    phone_allowance = ?,\n"
                    + "    clothing_allowance = ?,\n"
                    + "    gross_semi_monthly_rate = ?,\n"
                    + "    hourly_rate = ?\n"
                    + "WHERE employee_id = ?";
            
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setString(1,empDetails.getEmpSSS());
                preparedStatement.setLong(2,empDetails.getEmpPhilHealth());
                preparedStatement.setString(3,empDetails.getEmpTIN());
                preparedStatement.setLong(4,empDetails.getEmpPagibig());
                preparedStatement.setDouble(5,empDetails.getEmpBasicSalary());
                preparedStatement.setDouble(6,empDetails.getEmpRice());
                preparedStatement.setDouble(7,empDetails.getEmpPhone());
                preparedStatement.setDouble(8,empDetails.getEmpClothing());
                preparedStatement.setDouble(9,empDetails.getEmpMonthlyRate());
                preparedStatement.setDouble(10,empDetails.getEmpHourlyRate());
                preparedStatement.setInt(11, empDetails.getEmpID());
                
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }         
        }                          
        return empDetails;
    }
    
    public LocalDate[] getPayrollPeriodFromEmployeeHours(int empId, int month, int year) {
        String query = """
            SELECT MIN(date) AS start_date, MAX(date) AS end_date
            FROM employee_hours
            WHERE employee_id = ?
              AND EXTRACT(MONTH FROM date) = ?
              AND EXTRACT(YEAR FROM date) = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);
            preparedStatement.setInt(2, month + 1); // Calendar.JANUARY = 0
            preparedStatement.setInt(3, year);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() && resultSet.getDate("start_date") != null) {
                LocalDate start = resultSet.getDate("start_date").toLocalDate();
                LocalDate end = resultSet.getDate("end_date").toLocalDate();
                return new LocalDate[]{start, end};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }    
    

    
    public Finance savePayrollReport(Finance payrollReportDetails) {
        if (connection == null) return null;

        String query = """
            INSERT INTO public.payroll 
            (employee_id, payroll_period_start_date, payroll_period_end_date, number_of_hours_worked, gross_pay, deduction, net_pay)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Convert dates
            java.sql.Date payrollPeriodStart = payrollReportDetails.getPayrollPeriodStart() != null ? new java.sql.Date(payrollReportDetails.getPayrollPeriodStart().getTime()): null;
            java.sql.Date payrollPeriodEnd = payrollReportDetails.getPayrollPeriodEnd() != null? new java.sql.Date(payrollReportDetails.getPayrollPeriodEnd().getTime()): null;

            // Set parameters
            preparedStatement.setInt(1, payrollReportDetails.getEmpID());
            preparedStatement.setDate(2, payrollPeriodStart);
            preparedStatement.setDate(3, payrollPeriodEnd);
            preparedStatement.setDouble(4, payrollReportDetails.getNumberOfHoursWorked());
            preparedStatement.setDouble(5, payrollReportDetails.getGrossPay());
            preparedStatement.setDouble(6, payrollReportDetails.getDeduction());
            preparedStatement.setDouble(7, payrollReportDetails.getNetPay());

            // Execute and get generated ID
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payrollReportDetails.setPayrollId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Inserting payroll record failed, no ID returned.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payrollReportDetails;
    }
    
    
    
    
    private Person toPayrollDetails(ResultSet resultSet) 
        throws SQLException {
        Person payrollDetails = new Employee();
            payrollDetails.setEmpID(resultSet.getInt("employee_id"));
            payrollDetails.setEmpSSS(resultSet.getString("sss"));
            payrollDetails.setEmpPhilHealth(resultSet.getLong("philhealth"));
            payrollDetails.setEmpTIN(resultSet.getString("tin"));
            payrollDetails.setEmpPagibig(resultSet.getLong("pag_ibig"));
            payrollDetails.setEmpBasicSalary(resultSet.getDouble("basic_salary"));
            payrollDetails.setEmpRice(resultSet.getDouble("rice_subsidy"));
            payrollDetails.setEmpPhone(resultSet.getDouble("phone_allowance"));
            payrollDetails.setEmpClothing(resultSet.getDouble("clothing_allowance"));
            payrollDetails.setEmpMonthlyRate(resultSet.getDouble("gross_semi_monthly_rate"));
            payrollDetails.setEmpHourlyRate(resultSet.getDouble("hourly_rate"));
            
        return payrollDetails;
    }
     
}
