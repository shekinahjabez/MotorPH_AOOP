/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.payroll.domain;

import java.util.Date;

/**
 *
 * @author leniejoice
 */
public class IT extends Person{
    
    public IT(int empID, String lastName, String firstName, String empAddress, Date empBirthday,
                    String empPhoneNumber, String empSSS, String empTIN, long empPhilHealth,
                    long empPagibig, EmployeeDetails empImmediateSupervisor, EmployeeStatus empStatus,
                    EmployeePosition empPosition, double empBasicSalary, double empRice,
                    double empPhone, double empClothing, double empMonthlyRate, double empHourlyRate) {
        super(empID, lastName, firstName, empAddress, empBirthday, empPhoneNumber, empSSS, empTIN, empPhilHealth,
              empPagibig, empImmediateSupervisor, empStatus, empPosition, empBasicSalary, empRice,
              empPhone, empClothing, empMonthlyRate, empHourlyRate);
    }
   
}
