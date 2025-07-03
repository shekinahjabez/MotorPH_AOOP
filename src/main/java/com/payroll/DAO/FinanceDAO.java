/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.DAO;
import com.payroll.domain.Employee;
import com.payroll.domain.Finance;
import com.payroll.domain.Person;
import com.payroll.util.DatabaseConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
public class FinanceDAO {
    private Connection connection;
    
    public FinanceDAO(Connection connection) {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace(); // You can replace with logging if needed
        }
    }
    
    public Person getByEmpID(int empID) throws SQLException {
        String query = "SELECT * FROM public.vw_employee_profile WHERE employee_id = ?";

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
    
    public List<Employee> getEmployeeHours(int empID, Date from, Date to) {
        List<Employee> empHours = new ArrayList<>();

        if (connection != null) {
            try {
                String Query = "SELECT * FROM public.vw_employee_hours WHERE employee_id = ? AND date BETWEEN ? AND ?";
                PreparedStatement preparedStatement = connection.prepareStatement(Query);
                preparedStatement.setInt(1, empID);
                preparedStatement.setDate(2, new java.sql.Date(from.getTime()));
                preparedStatement.setDate(3, new java.sql.Date(to.getTime()));

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Timestamp timeInDate = resultSet.getTimestamp("time_in");
                    Timestamp timeOutDate = resultSet.getTimestamp("time_out");

                    // Skip if either time is null
                    if (timeInDate == null || timeOutDate == null) {
                        //System.out.println("Null time_in or time_out for employee_id " + empID + " on " + resultSet.getDate("date"));
                        continue;
                    }

                    LocalTime timeIn = LocalDateTime.ofInstant(timeInDate.toInstant(), ZoneId.systemDefault()).toLocalTime();
                    LocalTime timeOut = LocalDateTime.ofInstant(timeOutDate.toInstant(), ZoneId.systemDefault()).toLocalTime();

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
                Logger.getLogger(FinanceDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return empHours;
    }

    public float calculateSssContribution(double empSalary) {
        String query = "SELECT public.get_sss_contribution(CAST(? AS NUMERIC)) AS contribution";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, empSalary);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("contribution");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calling get_sss_contribution: " + e.getMessage());
            throw new RuntimeException("Failed to calculate SSS contribution", e);
        }

        return 0f;
    }
    
    public Person updatePayrollDetails(Person empDetails){
        if (connection == null) {
              System.err.println("Database connection not established.");
              return null;
            }

            String call = "CALL update_payroll_details(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (CallableStatement stmt = connection.prepareCall(call)) {
                stmt.setString(1, empDetails.getEmpSSS());
                stmt.setLong(2, empDetails.getEmpPhilHealth());
                stmt.setString(3, empDetails.getEmpTIN());
                stmt.setLong(4, empDetails.getEmpPagibig());
                stmt.setBigDecimal(5, BigDecimal.valueOf(empDetails.getEmpBasicSalary()));
                stmt.setBigDecimal(6, BigDecimal.valueOf(empDetails.getEmpRice()));
                stmt.setBigDecimal(7, BigDecimal.valueOf(empDetails.getEmpPhone()));
                stmt.setBigDecimal(8, BigDecimal.valueOf(empDetails.getEmpClothing()));
                stmt.setBigDecimal(9, BigDecimal.valueOf(empDetails.getEmpMonthlyRate()));
                stmt.setBigDecimal(10, BigDecimal.valueOf(empDetails.getEmpHourlyRate()));
                stmt.setInt(11, empDetails.getEmpID());

                /* Debug
                System.out.println("Calling stored procedure with the following values:");
                System.out.println("SSS: " + empDetails.getEmpSSS());
                System.out.println("PhilHealth: " + empDetails.getEmpPhilHealth());
                System.out.println("TIN: " + empDetails.getEmpTIN());
                System.out.println("Pag-ibig: " + empDetails.getEmpPagibig());
                System.out.println("Basic Salary: " + empDetails.getEmpBasicSalary());
                System.out.println("Rice Subsidy: " + empDetails.getEmpRice());
                System.out.println("Phone Allowance: " + empDetails.getEmpPhone());
                System.out.println("Clothing Allowance: " + empDetails.getEmpClothing());
                System.out.println("Gross Semi-Monthly Rate: " + empDetails.getEmpMonthlyRate());
                System.out.println("Hourly Rate: " + empDetails.getEmpHourlyRate());
                System.out.println("Employee ID: " + empDetails.getEmpID());*/

                stmt.execute();
                System.out.println("Payroll details updated successfully.");

            } catch (SQLException e) {
                System.err.println("Error calling stored procedure update_payroll_details:");
                e.printStackTrace();
            }
        return empDetails;
    }
 
    public Finance savePayrollReport(Finance payrollReportDetails) {
        if (connection == null) return null;

        String call = "CALL report.save_payroll_report(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(call)) {

            java.sql.Date startDate = payrollReportDetails.getPayrollPeriodStart() != null
                    ? new java.sql.Date(payrollReportDetails.getPayrollPeriodStart().getTime()) : null;

            java.sql.Date endDate = payrollReportDetails.getPayrollPeriodEnd() != null
                    ? new java.sql.Date(payrollReportDetails.getPayrollPeriodEnd().getTime()) : null;

            stmt.setInt(1, payrollReportDetails.getPayEmpId());
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            stmt.setDouble(4, payrollReportDetails.getPayNumberOfHoursWorked());
            stmt.setBigDecimal(5, BigDecimal.valueOf(payrollReportDetails.getPayComputedSalary()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(6, BigDecimal.valueOf(payrollReportDetails.getPayTotalContributions()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(7, BigDecimal.valueOf(payrollReportDetails.getPayNetPay()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(8, BigDecimal.valueOf(payrollReportDetails.getPayTotalAllowance()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(9, BigDecimal.valueOf(payrollReportDetails.getPaySssContri()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(10, BigDecimal.valueOf(payrollReportDetails.getPayPhealthContri()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(11, BigDecimal.valueOf(payrollReportDetails.getPayPagibigContri()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(12, BigDecimal.valueOf(payrollReportDetails.getPayWithholdingTax()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(13, BigDecimal.valueOf(payrollReportDetails.getPayHourlyRate()).setScale(2, RoundingMode.HALF_UP));
            stmt.setString(14, payrollReportDetails.getPayFullName());
            stmt.setString(15, payrollReportDetails.getPayPosition());
            stmt.setBigDecimal(16, BigDecimal.valueOf(payrollReportDetails.getPayRiceAllowance()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(17, BigDecimal.valueOf(payrollReportDetails.getPayPhoneAllowance()).setScale(2, RoundingMode.HALF_UP));
            stmt.setBigDecimal(18, BigDecimal.valueOf(payrollReportDetails.getPayClothingAllowance()).setScale(2, RoundingMode.HALF_UP));

            stmt.registerOutParameter(19, java.sql.Types.INTEGER);

            // Debugging log
            //System.out.println("Executing payroll report procedure for employee ID: " + payrollReportDetails.getPayEmpId());

            stmt.execute();

            int generatedPayrollId = stmt.getInt(19);
            payrollReportDetails.setPayrollId(generatedPayrollId);

            //System.out.println("Generated payroll ID: " + generatedPayrollId);

        } catch (SQLException e) {
            System.err.println("Error calling stored procedure report.save_payroll_report:");
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
