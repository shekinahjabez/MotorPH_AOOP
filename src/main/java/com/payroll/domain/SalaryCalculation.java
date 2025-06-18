/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;
import com.payroll.util.*;
import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.domain.Employee;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author leniejoice
 */
public class SalaryCalculation {
    private Connection connection;
    public SalaryCalculation(Connection connection){
        this.connection = connection;  
    }
    
//CORRECTED
    public static double calculatePhilHealthContribution(double empSalary) {
        // 1. Calculate the raw contribution
        double rawContribution = empSalary * 0.015;

        // 2. Round the result to two decimal places
        double roundedContribution = Math.round(rawContribution * 100.0) / 100.0;

        // 3. Return the rounded result
        return roundedContribution;
    }  
    
    //BUG
    /*public static double calculatePhilHealthContribution(double empSalary)  {
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
    }*/
    
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
     
    public static double getTotalAllowance(Person empDetails){
        double total = 0;  
        if (empDetails != null) {
            total += empDetails.getEmpRice();    
            total += empDetails.getEmpPhone();      
            total += empDetails.getEmpClothing();   
        }
        return total;  
     }
    
    public static double getTotalHoursWorked(List<Employee> empHours){
        double totalHoursWorked = 0;
        for (Employee employeeHours: empHours){
            totalHoursWorked += employeeHours.getHoursWorked();
        }
        //return String.format("%d:%02d", totalHoursWorked / 3600, (totalHoursWorked % 3600) / 60);
        return totalHoursWorked/3600;
    }
    
    public static String getFormattedTotalHoursWorked(List<Employee> empHours){
        long totalHoursWorked = 0;
        for (Employee employeeHours: empHours){
            totalHoursWorked += employeeHours.getHoursWorked();
        }
        return String.format("%d:%02d", totalHoursWorked / 3600, (totalHoursWorked % 3600) / 60);
    }
     
    public static double getBasicSalary(List<Employee> empHours, IT empAccount) {
        return empAccount.getEmpDetails().getEmpBasicSalary(); // fixed monthly basic salary
    } 
     
    public static double getComputedSalary(List<Employee> empHours, IT empAccount) {
        double totalHoursWorked = SalaryCalculation.getTotalHoursWorked(empHours);
        double hourlyRate = empAccount.getEmpDetails().getEmpHourlyRate();
        return totalHoursWorked * hourlyRate;
    }
    
    public static double getGrossSalary(List<Employee> empHours,IT empAccount){
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
    
    public static double getNetPay(double empSalary, double sssContri) {
        double taxableIncome = getTaxableIncome(empSalary, sssContri);
        return taxableIncome - calculateWithholdingTax(taxableIncome);
    }
}   