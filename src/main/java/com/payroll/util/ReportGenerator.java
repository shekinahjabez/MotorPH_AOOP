/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
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
    
public void generatePayslipReport() {
        try {
            // Compile the JRXML file
            JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/report/Payslip.jrxml");

            // Prepare parameters map (empty for now since we're not using any)
            Map<String, Object> parameters = new HashMap<>();

            // Get database connection
            Connection connection = DatabaseConnection.getConnection();

            // Fill the report with the parameters and connection
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Display the report
            JasperViewer.viewReport(jasperPrint, false);

            // Close connection
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public void generateEmployeeReport() {
        try {
            // Compile the JRXML file
            JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/report/EmployeeReport.jrxml");

            // Prepare parameters map (empty for now since we're not using any)
            Map<String, Object> parameters = new HashMap<>();

            // Get database connection
            Connection connection = DatabaseConnection.getConnection();

            // Fill the report with the parameters and connection
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Display the report
            JasperViewer.viewReport(jasperPrint, false);

            // Close connection
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
