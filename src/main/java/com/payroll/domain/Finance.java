/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import java.util.Date;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import com.payroll.domain.Finance;

/**
 *
 * @author leniejoice
 */
public class Finance extends Person {
    
    private int payrollId;
    private Date payrollPeriodStart;
    private Date payrollPeriodEnd;
    private double numberOfHoursWorked;
    private double grossPay;
    private double deduction;
    private double netPay;
      
    
    public Finance() {
        super(0, "", "", "", null, "", "", "", 0, 0, null, null, null,0,0,0,0,0,0); 
        // Initialize `Person` fields with default values
    }
    
    public Finance(int empID, String lastName, String firstName, String empAddress, Date empBirthday,
                   String empPhoneNumber, String empSSS, String empTIN, long empPhilHealth,
                   long empPagibig, Person empImmediateSupervisor, EmployeeStatus empStatus,
                   EmployeePosition empPosition, double empBasicSalary, double empRice,
                   double empPhone, double empClothing, double empMonthlyRate, double empHourlyRate) {
        // Call Parent (Person) Constructor
        super(empID, lastName, firstName, empAddress, empBirthday, empPhoneNumber, empSSS, 
              empTIN, empPhilHealth, empPagibig, empImmediateSupervisor, empStatus, empPosition,
              empBasicSalary,empRice,empPhone,empClothing, empMonthlyRate,empHourlyRate);

    }
    
    @Override
    public int getEmpID() {
        return empID;
    }

    @Override
    public void setEmpID(int empID) {
        this.empID = empID;
    }
    
    public int getPayrollId() {
        return payrollId;
    }

    public Date getPayrollPeriodStart() {
        return payrollPeriodStart;
    }

    public Date getPayrollPeriodEnd() {
        return payrollPeriodEnd;
    }

    public double getNumberOfHoursWorked() {
        return numberOfHoursWorked;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public double getDeduction() {
        return deduction;
    }

    public double getNetPay() {
        return netPay;
    }

    // Setters

    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }

    public void setPayrollPeriodStart(Date payrollPeriodStart) {
        this.payrollPeriodStart = payrollPeriodStart;
    }

    public void setPayrollPeriodEnd(Date payrollPeriodEnd) {
        this.payrollPeriodEnd = payrollPeriodEnd;
    }

    public void setNumberOfHoursWorked(double numberOfHoursWorked) {
        this.numberOfHoursWorked = numberOfHoursWorked;
    }

    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }

    public void setDeduction(double deduction) {
        this.deduction = deduction;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    
    
   
   
}
