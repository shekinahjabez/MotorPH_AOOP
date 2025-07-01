/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author leniejoice
 */
public class ReportGenerator {

    public void generatePayrollReport(int payrollId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Map<String, Object> parameters = getCommonParameters();
            parameters.put("payroll_id", payrollId);

            JasperReport jasperReport = compileReport("report/Payslip.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            if (jasperPrint.getPages().isEmpty()) {
                showMessage("No pages found for payroll ID: " + payrollId, "No Data");
            } else {
                JasperViewer.viewReport(jasperPrint, false);
            }
        } catch (Exception e) {
            showError("Failed to generate payroll report", e);
        }
    }

    public void generateTimecardReport(int employeeId, int month, int year) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Map<String, Object> parameters = getCommonParameters();
            parameters.put("employee_id", employeeId);
            parameters.put("month", month);
            parameters.put("year", year);

            JasperReport jasperReport = compileReport("report/Timecard.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            if (jasperPrint.getPages().isEmpty()) {
                showMessage("No data available for Employee ID: " + employeeId, "Empty Report");
            } else {
                JasperViewer.viewReport(jasperPrint, false);
            }

        } catch (Exception e) {
            showError("Failed to generate timecard report", e);
        }
    }

    public void generateEmployeeReport() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Map<String, Object> parameters = getCommonParameters();

            JasperReport jasperReport = compileReport("report/EmployeeReport.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            showError("Failed to generate employee report", e);
        }
    }
    
    public void generateRoleReport() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Map<String, Object> parameters = getCommonParameters();

            JasperReport jasperReport = compileReport("report/RoleReport.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            showError("Failed to generate role report", e);
        }
    }
    
    public void generateEmployeeProfileReport(Integer employeeID) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            Map<String, Object> parameters = getCommonParameters();
            
            parameters.put("employee_id", employeeID); 

            JasperReport jasperReport = compileReport("report/EmployeeProfileReport.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            showError("Failed to generate employee report", e);
        }
    }

    // ---------------- Helper Methods ----------------

    private JasperReport compileReport(String resourcePath) throws FileNotFoundException, JRException {
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (reportStream == null) {
            throw new FileNotFoundException("Report file not found in classpath: " + resourcePath);
        }
        return JasperCompileManager.compileReport(reportStream);
    }

    private Map<String, Object> getCommonParameters() throws FileNotFoundException {
        Map<String, Object> parameters = new HashMap<>();
        URL logoUrl = getClass().getClassLoader().getResource("report/mph_logo.png");
        if (logoUrl == null) {
            throw new FileNotFoundException("Logo not found at /report/mph_logo.png");
        }
        parameters.put("LogoPath", logoUrl);
        return parameters;
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String title, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, title + ":\n" + e.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
    }
}
