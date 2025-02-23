/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.util;
import com.payroll.domain.EmployeeAccount;
import com.payroll.domain.EmployeeDetails;
import com.payroll.domain.EmployeeHours;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class PayrollUtils {
    private Connection connection;
    public PayrollUtils(DatabaseConnection dbConnection){
        this.connection = dbConnection.getConnection();  
    }
    
     public static double calculatePhilHealthContribution(double empSalary)  {
        double contribution = empSalary * 0.015;
        contribution = Math.round(contribution * 100) / 100.0;
        if (empSalary <= 10000) {
            return empSalary * 0.015;
        } else if (empSalary <= 59999.99) {
            return empSalary * 0.015;
        } else if (empSalary == 60000) {
            return empSalary * 0.015;
        }
        return contribution;
    }
    
     public static double calculatePagibigContribution(double empSalary)  {
        double pagibig;
        if (empSalary <= 1499.99) {
            pagibig = empSalary * 0.01;
        } else if (empSalary > 1500) {
            pagibig = empSalary * 0.02;
        } else {
            pagibig = 0.0;
        }
        double maxPagibig = 100.0;
        return Math.min(pagibig, maxPagibig);
    }

     public static double calculateWithholdingTax(double taxableIncome) {
        double tax;
        if (taxableIncome <= 20832) {
            tax = 0;
        } else if (taxableIncome <= 33333) {
            tax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            tax = 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }
        return tax;
    }
     
    public static double getTotalAllowance(EmployeeDetails empDetails){
        double total = 0;  
        if (empDetails != null) {
            total += empDetails.getEmpRice();    
            total += empDetails.getEmpPhone();      
            total += empDetails.getEmpClothing();   
        }
        return total;  
     }
    
    public static double getTotalHoursWorked(List<EmployeeHours> empHours){
        double totalHoursWorked = 0;
        for (EmployeeHours employeeHours: empHours){
            totalHoursWorked += employeeHours.getHoursWorked();
        }
        //return String.format("%d:%02d", totalHoursWorked / 3600, (totalHoursWorked % 3600) / 60);
        return totalHoursWorked/3600;
    }
    
     public static String getFormattedTotalHoursWorked(List<EmployeeHours> empHours){
        long totalHoursWorked = 0;
        for (EmployeeHours employeeHours: empHours){
            totalHoursWorked += employeeHours.getHoursWorked();
        }
        return String.format("%d:%02d", totalHoursWorked / 3600, (totalHoursWorked % 3600) / 60);
    }
     
    public static double getBasicSalary(List<EmployeeHours> empHours,EmployeeAccount empAccount){
        double totalHoursWorked = PayrollUtils.getTotalHoursWorked(empHours);
        double hourlyRate = empAccount.getEmpDetails().getEmpHourlyRate();
        return totalHoursWorked * hourlyRate;
    }
    
    public static double getGrossSalary(List<EmployeeHours> empHours,EmployeeAccount empAccount){
        double basicSalary = getBasicSalary(empHours, empAccount);
        return basicSalary + getTotalAllowance(empAccount.getEmpDetails());
                
    }
    
    public static double getTotalDeductions(double empSalary, double sssContri){
        double philhealthContri = calculatePhilHealthContribution(empSalary);
        double pagibigContri = calculatePagibigContribution(empSalary);
        return philhealthContri + sssContri + pagibigContri;
    }
    
    public static double getTaxableIncome(double empSalary, double sssContri){
        double totalDeductions = getTotalDeductions(empSalary, sssContri);
        return empSalary - totalDeductions;
    }
    
    public static double getNetPay(double empSalary,double sssContri){
       double netPay = getTaxableIncome(empSalary, sssContri) - calculateWithholdingTax(empSalary);
       return netPay;
    }
}   